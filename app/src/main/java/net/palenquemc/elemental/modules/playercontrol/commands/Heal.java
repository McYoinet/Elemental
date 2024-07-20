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

public class Heal implements TabExecutor {
    private final Elemental plugin;
    
    public Heal(Elemental plugin) {
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
        String healSelf = chat.papi(player, playerControl.getString("player_control_module.heal.self"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.heal.usage"));

        String healByOther = playerControl.getString("player_control_module.heal.by_other");
        String healToOther = playerControl.getString("player_control_module.heal.to_other");

        if(!sender.hasPermission("elmental.heal")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }

                player.setHealth(20);

                player.sendMessage(mm.deserialize(healSelf));

                return true;
            }

            case 1 -> {
                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));

                    return true;
                }

                if(!sender.hasPermission("elemental.heal.others")) {
                    sender.sendMessage(mm.deserialize(noPerms));
                    
                    return true;
                }

                targetPlayer.setHealth(20);

                healByOther = chat.papi(targetPlayer, healByOther);
                healToOther = chat.papi(targetPlayer, healToOther);

                targetPlayer.sendMessage(mm.deserialize(healByOther, Placeholder.unparsed("command_sender", sender.getName())));
                sender.sendMessage(mm.deserialize(healToOther, Placeholder.unparsed("target_player", targetPlayer.getName())));
            
                return true;
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
