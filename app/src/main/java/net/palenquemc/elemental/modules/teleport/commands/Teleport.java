package net.palenquemc.elemental.modules.teleport.commands;

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
import net.palenquemc.elemental.utils.ChatUtils;

public class Teleport implements TabExecutor {
    private final Elemental plugin;
    
    public Teleport(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration teleport = plugin.config.getConfig("teleport.yml");

        ChatUtils chat = new ChatUtils();

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String usage = chat.papi(player, teleport.getString("teleport_module.teleport.usage"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String invalidLocation = chat.papi(player, teleport.getString("teleport_module.teleport.invalid_location"));
        String cannotTeleportSelf = chat.papi(player, teleport.getString("teleport_module.teleport.cannot_teleport_to_self"));
        String teleportedSelf = chat.papi(player, teleport.getString("teleport_module.teleport.teleported.self"));
        String teleportedAll = chat.papi(player, teleport.getString("teleport_module.teleport.teleported.all"));

        String teleportedByOther = teleport.getString("teleport_module.teleport.teleported.by_other");
        String teleportedToOther = teleport.getString("teleport_module.teleport.teleported.to_other");

        if(!sender.hasPermission("elmental.teleport")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(usage));
            
            return true;
        }

        if(args.length == 1) {
            if(player == null) {
                sender.sendMessage(mm.deserialize(executableFromPlayer));

                return true;
            }

            Player targetPlayer = plugin.getServer().getPlayer(args[0]);

            if(targetPlayer == null) {
                sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));

                return true;
            }

            if(player.getName() == targetPlayer.getName()) {
                sender.sendMessage(mm.deserialize(cannotTeleportSelf));

                return true;
            }

            player.teleport(targetPlayer);
            sender.sendMessage(mm.deserialize(teleportedSelf, Placeholder.unparsed("target_destination", args[0])));

            return true;

        } else if(args.length == 2) {
            if(!sender.hasPermission("elemental.teleport.others")) {
                sender.sendMessage(mm.deserialize(noPerms));

                return true;
            }

            Player destinationPlayer = plugin.getServer().getPlayer(args[1]);

            if(destinationPlayer == null) {
                sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[1])));
            
                return true;
            }

            if(args[0].equalsIgnoreCase("@a")) {
                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    p.teleport(destinationPlayer);

                    if(p.getName() != sender.getName()) {
                        teleportedByOther = chat.papi(p, teleportedByOther);
                        
                        p.sendMessage(mm.deserialize(teleportedByOther, Placeholder.unparsed("target_destination", destinationPlayer.getName()), Placeholder.unparsed("command_sender", sender.getName())));                       
                    }
                }
                
                sender.sendMessage(mm.deserialize(teleportedAll, Placeholder.unparsed("target_destination", destinationPlayer.getName())));

                return true;
            }

            Player targetPlayer = plugin.getServer().getPlayer(args[0]);

            if(targetPlayer == null) {
                sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));
                
                return true;
            }

            targetPlayer.teleport(destinationPlayer);

            teleportedToOther = chat.papi(targetPlayer, teleportedToOther);
            teleportedByOther = chat.papi(targetPlayer, teleportedByOther);

            sender.sendMessage(mm.deserialize(teleportedToOther, Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("target_destination", destinationPlayer.getName())));
            targetPlayer.sendMessage(mm.deserialize(teleportedByOther, Placeholder.unparsed("target_destination", destinationPlayer.getName()), Placeholder.unparsed("command_sender", sender.getName())));
        } else if(args.length > 2) {
            if(args.length == 3) {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));
    
                    return true;
                }

                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);

                    Location loc = new Location(player.getWorld(), x, y, z);

                    player.teleport(loc);

                    sender.sendMessage(mm.deserialize(teleportedSelf, Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ()))));
                    
                    return true;
                } catch(NumberFormatException e) {
                    sender.sendMessage(mm.deserialize(invalidLocation));
    
                    return true;
                }
                
            } else if(args.length == 4){
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));
    
                    return true;
                }

                if(args[0].equalsIgnoreCase("@a")) {
                    double x, y, z;
                    Location loc;
                    
                    try {
                        x = Double.parseDouble(args[1]);
                        y = Double.parseDouble(args[2]);
                        z = Double.parseDouble(args[3]);
                        
                        loc = new Location(player.getWorld(), x, y, z);
                    } catch(NumberFormatException e) {
                        sender.sendMessage(mm.deserialize(invalidLocation));
        
                        return true;
                    }

                    for(Player targetPlayer : plugin.getServer().getOnlinePlayers()) {
                        targetPlayer.teleport(loc);
    
                        if(targetPlayer.getName() != sender.getName()) {
                            teleportedByOther = chat.papi(targetPlayer, teleportedByOther);

                            sender.sendMessage(mm.deserialize(teleportedByOther, Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ())), Placeholder.unparsed("command_sender", sender.getName())));                       
                        }
                    }
                    
                    sender.sendMessage(mm.deserialize(teleportedAll, Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ()))));
    
                    return true;
                }
                
                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));

                    return true;
                }

                try {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);

                    Location loc = new Location(player.getWorld(), x, y, z);

                    targetPlayer.teleport(loc);

                    teleportedByOther = chat.papi(targetPlayer, teleportedByOther);

                    sender.sendMessage(mm.deserialize(teleportedSelf, Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ()))));
                    targetPlayer.sendMessage(mm.deserialize(teleportedByOther, Placeholder.unparsed("target_destination", Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ())), Placeholder.unparsed("command_sender", sender.getName())));

                    return true;
                } catch(NumberFormatException e) {
                    sender.sendMessage(mm.deserialize(invalidLocation));
    
                    return true;
                }
            } else if(args.length > 4) {
                sender.sendMessage(mm.deserialize(usage));
            
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
