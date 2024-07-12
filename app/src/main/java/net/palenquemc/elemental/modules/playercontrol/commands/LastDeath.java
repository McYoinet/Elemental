package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class LastDeath implements TabExecutor {
    private Elemental plugin;
    
    public LastDeath(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        if(!sender.hasPermission("elemental.lastdeath")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        switch (args.length) {
            case 0 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;

                Location loc = player.getLastDeathLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.lastdeath.no_deaths_found.self")));

                    return true;
                }

                if(loc.getWorld() == null) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.lastdeath.world_not_found.self")));

                    return true;
                }

                String world = loc.getWorld().getName();
                String coordinates = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " +  Double.toString(loc.getZ());

                player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.lastdeath.coordinates.self"), Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world)));
            
                return true;
            }

            case 1 -> {
                if(!sender.hasPermission("elemental.lastdeath.others")) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));

                    return true;
                }

                OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(args[0]);
                
                if(!targetPlayer.hasPlayedBefore()) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found"), Placeholder.unparsed("target_player", args[0])));
                    
                    return true;
                }
                
                Location loc = targetPlayer.getLastDeathLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.lastdeath.no_deaths_found.other"), Placeholder.unparsed("target_player", args[0])));

                    return true;
                }

                if(loc.getWorld() == null) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.lastdeath.world_not_found.other"), Placeholder.unparsed("target_player", args[0])));

                    return true;
                }

                String world = loc.getWorld().getName();
                String coordinates = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " +  Double.toString(loc.getZ());

                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.lastdeath.coordinates.other"), Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world), Placeholder.unparsed("target_player", targetPlayer.getName())));
            
                return true;
            }

            default -> {
                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.lastdeath.usage")));
            
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
