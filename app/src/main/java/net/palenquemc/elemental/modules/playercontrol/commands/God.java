package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class God implements TabExecutor {

    private final Elemental plugin;
    
    public God(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String godDisableSelf = chat.papi(player, playerControl.getString("player_control_module.god.disable.self"));
        String godEnableSelf = chat.papi(player, playerControl.getString("player_control_module.god.enable.self"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.god.usage"));

        String godDisableByOther = playerControl.getString("player_control_module.god.disable.by_other");
        String godDisableToOther = playerControl.getString("player_control_module.god.disable.to_other");
        String godEnableByOther = playerControl.getString("player_control_module.god.enable.by_other");
        String godEnableToOther = playerControl.getString("player_control_module.god.enable.to_other");

        if(!sender.hasPermission("elmental.god")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }

                if(player.isInvulnerable()) {
                    player.setInvulnerable(false);

                    player.sendMessage(mm.deserialize(godDisableSelf));

                    return true;
                } else {
                    player.setInvulnerable(true);

                    player.sendMessage(mm.deserialize(godEnableSelf));
                
                    return true;
                }
            }

            case 1 -> {
                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound));

                    return true;
                }

                if(!sender.hasPermission("elemental.god.others")) {
                    sender.sendMessage(mm.deserialize(noPerms));
                    
                    return true;
                }

                if(targetPlayer.isInvulnerable()) {
                    targetPlayer.setInvulnerable(false);

                    godDisableByOther = chat.papi(targetPlayer, godDisableByOther);
                    godDisableToOther = chat.papi(targetPlayer, godDisableToOther);

                    targetPlayer.sendMessage(mm.deserialize(godDisableByOther, Placeholder.unparsed("target_player", targetPlayer.getName())));
                    sender.sendMessage(mm.deserialize(godDisableToOther, Placeholder.unparsed("command_sender", sender.getName())));

                    return true;
                } else {
                    targetPlayer.setInvulnerable(true);

                    godEnableByOther = chat.papi(targetPlayer, godEnableByOther);
                    godEnableToOther = chat.papi(targetPlayer, godEnableToOther);

                    targetPlayer.sendMessage(mm.deserialize(godEnableByOther, Placeholder.unparsed("target_player", targetPlayer.getName())));
                    sender.sendMessage(mm.deserialize(godEnableToOther, Placeholder.unparsed("command_sender", sender.getName())));

                    return true;
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(usage));

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }

        return arguments;
    }
}
