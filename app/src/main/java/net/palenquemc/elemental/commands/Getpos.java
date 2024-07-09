package net.palenquemc.elemental.commands;

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
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission("elemental.getpos")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;
                Location loc = player.getLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.getpos.location_not_found.self")));

                    return true;
                }

                String coordinates = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ());
                String world = loc.getWorld().getName();

                sender.sendMessage(mm.deserialize(messages.getString("messages.getpos.location.self"), Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world)));
            
                return true;
            }

            case 1 -> {
                if(!sender.hasPermission("elemental.getpos.others")) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
                    
                    return true;
                }

                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.target_not_found"), Placeholder.unparsed("target_player", args[0])));
                    
                    return true;
                }

                if(targetPlayer.hasPermission("elemental.getpos.bypass") && !sender.hasPermission("elemental.getpos.ignorebypass")) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.getpos.target_player_bypass"), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                    return true;
                }

                Location loc = targetPlayer.getLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.getpos.location_not_found.other"), Placeholder.unparsed("target_player", targetPlayer.getName())));

                    return true;
                }

                String coordinates = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ());
                String world = loc.getWorld().getName();

                sender.sendMessage(mm.deserialize(messages.getString("messages.getpos.location.other"), Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world), Placeholder.unparsed("target_player", targetPlayer.getName())));
            
                return true;
            }

            default -> {
                sender.sendMessage(mm.deserialize(messages.getString("messages.getpos.usage")));

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
