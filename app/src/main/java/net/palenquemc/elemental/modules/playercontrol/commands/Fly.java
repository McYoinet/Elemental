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

public class Fly implements TabExecutor {
    private final Elemental plugin;
    
    public Fly(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        ChatUtils chat = new ChatUtils();

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String flyDisableSelf = chat.papi(player, playerControl.getString("player_control_module.fly.disable.self"));
        String flyEnableSelf = chat.papi(player, playerControl.getString("player_control_module.fly.enable.self"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.fly.usage"));
        String flyDisableByOther = playerControl.getString("player_control_module.fly.disable.by_other");
        String flyEnableByOther = playerControl.getString("player_control_module.fly.enable.by_other");
        String flyDisableToOther = playerControl.getString("player_control_module.fly.disable.to_other");
        String flyEnableToOther = playerControl.getString("player_control_module.fly.enable.to_other");

        if(!sender.hasPermission("elmental.fly")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }

                if(player.getAllowFlight()) {
                    player.setAllowFlight(false);

                    player.sendMessage(mm.deserialize(flyDisableSelf));

                    return true;
                } else {
                    player.setAllowFlight(true);

                    player.sendMessage(mm.deserialize(flyEnableSelf));
                
                    return true;
                }
            }

            case 1 -> {
                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound));

                    return true;
                }

                if(!sender.hasPermission("elemental.fly.others")) {
                    sender.sendMessage(mm.deserialize(noPerms));
                    
                    return true;
                }

                if(targetPlayer.getAllowFlight()) {
                    targetPlayer.setAllowFlight(false);

                    flyDisableToOther = chat.papi(targetPlayer, flyDisableToOther);
                    flyDisableByOther = chat.papi(targetPlayer, flyDisableByOther);

                    targetPlayer.sendMessage(mm.deserialize(flyDisableByOther, Placeholder.unparsed("target_player", targetPlayer.getName())));
                    sender.sendMessage(mm.deserialize(flyDisableToOther, Placeholder.unparsed("command_sender", sender.getName())));

                    return true;
                } else {
                    targetPlayer.setAllowFlight(true);

                    flyEnableToOther = chat.papi(targetPlayer, flyEnableToOther);
                    flyEnableByOther = chat.papi(targetPlayer, flyEnableByOther);

                    targetPlayer.sendMessage(mm.deserialize(flyEnableByOther, Placeholder.unparsed("target_player", targetPlayer.getName())));
                    sender.sendMessage(mm.deserialize(flyEnableToOther, Placeholder.unparsed("command_sender", sender.getName())));

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
