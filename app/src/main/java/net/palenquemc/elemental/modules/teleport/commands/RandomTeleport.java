package net.palenquemc.elemental.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class RandomTeleport implements TabExecutor {

    private Elemental plugin;
    
    public RandomTeleport(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration teleport = plugin.config.getConfig("teleport.yml");

        if(!sender.hasPermission("elemental.teleport.random")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        List<Player> players = new ArrayList<>();
        
        if(plugin.getServer().getOnlinePlayers().size() < 3) {
            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.random_teleport.not_enough_players")));
        
            return true;
        }

        plugin.getServer().getOnlinePlayers().forEach(p -> {
            players.add(p);
        });

        Random random = new Random();

        Player destinationPlayer = players.get(random.nextInt(players.size()));

        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

                return true;
            }

            Player player = (Player) sender;

            player.teleport(destinationPlayer);

            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.random_teleport.teleported.self"), Placeholder.unparsed("target_destination", destinationPlayer.getName())));
        
            return true;
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("@a")) {
                plugin.getServer().getOnlinePlayers().forEach(p -> {
                    p.teleport(destinationPlayer);

                    if(p.getName() != sender.getName()) {
                        p.sendMessage(mm.deserialize(teleport.getString("teleport_module.random_teleport.teleported.by_other"), Placeholder.unparsed("target_destination", destinationPlayer.getName()), Placeholder.unparsed("command_sender", sender.getName())));
                    }
                });

                sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.random_teleport.teleported.all"), Placeholder.unparsed("target_destination", destinationPlayer.getName())));

                return true;
            }

            Player targetPlayer = plugin.getServer().getPlayer(args[0]);

            if(targetPlayer == null) {
                sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found"), Placeholder.unparsed("target_player", args[0])));
                
                return true;
            }

            targetPlayer.teleport(destinationPlayer);

            targetPlayer.sendMessage(mm.deserialize(teleport.getString("teleport_module.random_teleport.teleported.by_other"), Placeholder.unparsed("target_destination", destinationPlayer.getName()), Placeholder.unparsed("command_sender", sender.getName())));
            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.random_teleport.teleported.to_other"), Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("target_destination", destinationPlayer.getName())));

            return true;
        } else if(args.length > 1) {
            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.random_teleport.usage")));
            
            return true;
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("[player]");
        }
        
        return arguments;
    }    
}
