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

public class Teleport implements TabExecutor {
    private Elemental plugin;
    
    public Teleport(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission("elmental.teleport")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.usage")));
            
            return true;
        }

        if(args.length == 1) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));

                return true;
            }

            Player player = (Player) sender;
            Player targetPlayer = plugin.getServer().getPlayer(args[0]);

            if(targetPlayer == null) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.target_not_found"), Placeholder.unparsed("target_player", args[0])));

                return true;
            }

            if(player.getName() == targetPlayer.getName()) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.cannot_teleport_to_self")));

                return true;
            }

            player.teleport(targetPlayer);
            sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.teleported.self"), Placeholder.unparsed("target_destination", args[0])));

            return true;

        } else if(args.length == 2) {
            if(!sender.hasPermission("elemental.teleport.others")) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));

                return true;
            }

            Player targetPlayer = plugin.getServer().getPlayer(args[0]);
            Player destinationPlayer = plugin.getServer().getPlayer(args[1]);

            if(targetPlayer == null) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.target_not_found"), Placeholder.unparsed("target_player", args[0])));
                
                return true;
            }

            if(destinationPlayer == null) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.target_not_found"), Placeholder.unparsed("target_player", args[1])));
            
                return true;
            }

            targetPlayer.teleport(destinationPlayer);

            sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.teleported.to_other"), Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("target_destination", destinationPlayer.getName())));
            targetPlayer.sendMessage(mm.deserialize(messages.getString("messages.teleport.teleported.by_other"), Placeholder.unparsed("target_destination", destinationPlayer.getName()), Placeholder.unparsed("command_sender", sender.getName())));
        } else if(args.length > 2) {
            if(args.length == 3) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));
    
                    return true;
                }

                Player player = (Player) sender;

                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);

                    Location loc = new Location(player.getWorld(), x, y, z);

                    player.teleport(loc);

                    sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.teleported.self"), Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ()))));
                    
                    return true;
                } catch(NumberFormatException e) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.invalid_location")));
    
                    return true;
                }
                
            } else if(args.length == 4){
                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.target_not_found"), Placeholder.unparsed("target_player", args[0])));

                    return true;
                }

                try {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);

                    Location loc = new Location(targetPlayer.getWorld(), x, y, z);

                    targetPlayer.teleport(loc);

                    sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.teleported.self"), Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ()))));
                    targetPlayer.sendMessage(mm.deserialize(messages.getString("messages.teleport.teleported.by_other"), Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ())), Placeholder.unparsed("command_sender", sender.getName())));

                    return true;
                } catch(NumberFormatException e) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.invalid_location")));
    
                    return true;
                }
            } else if(args.length > 4) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.teleport.usage")));
            
                return true;
            }
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    arguments.add(player.getName());
                });

                arguments.add("[x position]");
            }
            case 2 -> {
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    arguments.add(player.getName());
                });

                arguments.add("[y position]");
            }
            case 3 -> {
                arguments.add("[z position]");
            }
            case 4 -> {
                arguments.add("[z position]");
            }
        }

        return arguments;
    }
}
