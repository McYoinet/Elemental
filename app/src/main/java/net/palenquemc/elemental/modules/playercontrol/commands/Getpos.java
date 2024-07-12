package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class Getpos implements TabExecutor {
    private Elemental plugin;
    
    public Getpos(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        if(!sender.hasPermission("elemental.getpos")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;
                Location loc = player.getLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.getpos.location_not_found.self")));

                    return true;
                }

                String coordinates = String.format("%.2f", loc.getX()) + ", " + String.format("%.2f", loc.getY()) + ", " + String.format("%.2f", loc.getZ());
                String world = loc.getWorld().getName();

                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.getpos.location.self"), Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world)));
            
                return true;
            }

            case 1 -> {
                if(!sender.hasPermission("elemental.getpos.others")) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                    
                    return true;
                }

                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found"), Placeholder.unparsed("target_player", args[0])));
                    
                    return true;
                }

                if(targetPlayer.hasPermission("elemental.getpos.bypass") && !sender.hasPermission("elemental.getpos.ignorebypass")) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.getpos.target_player_bypass"), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                    return true;
                }

                Location loc = targetPlayer.getLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.getpos.location_not_found.other"), Placeholder.unparsed("target_player", targetPlayer.getName())));

                    return true;
                }

                String coordinates = String.format("%.2f", loc.getX()) + ", " + String.format("%.2f", loc.getY()) + ", " + String.format("%.2f", loc.getZ());
                String world = loc.getWorld().getName();

                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.getpos.location.other"), Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world), Placeholder.unparsed("target_player", targetPlayer.getName())));
            
                return true;
            }

            default -> {
                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.getpos.usage")));

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }
        
        return arguments;
    }
}
