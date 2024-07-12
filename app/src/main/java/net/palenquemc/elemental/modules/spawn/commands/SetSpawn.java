package net.palenquemc.elemental.modules.spawn.commands;

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

public class SetSpawn implements TabExecutor {

    private Elemental plugin;
    
    public SetSpawn(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");
        FileConfiguration config = plugin.config.getConfig("config.yml");

        if(!sender.hasPermission("elmental.setspawn")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        switch (args.length) {
            case 0 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));
                    
                    return true;
                }

                Player player = (Player) sender;

                Location loc = player.getLocation();

                String worldname = player.getWorld().getName();

                Double x = loc.getX();
                Double y = loc.getY();
                Double z = loc.getZ();

                float yaw = loc.getYaw();
                float pitch = loc.getPitch();

                config.set("config.spawn.location.world", worldname);

                config.set("config.spawn.location.pos_x", x);
                config.set("config.spawn.location.pos_y", y);
                config.set("config.spawn.location.pos_z", z);

                config.set("config.spawn.location.yaw", yaw);
                config.set("config.spawn.location.pitch", pitch);

                plugin.config.saveConfigs();

                plugin.config.reloadConfig();
                
                String coordinates = Double.toString(x) + ", " + Double.toString(y) + ", " + Double.toString(z);

                sender.sendMessage(mm.deserialize(messages.getString("messages.setspawn.set"), Placeholder.unparsed("coordinates", coordinates)));
                
                return true;
            }

            case 3 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));
                    
                    return true;
                }

                Player player = (Player) sender;

                String worldname = player.getWorld().getName();

                Double x = Double.parseDouble(args[0]);
                Double y = Double.parseDouble(args[1]);
                Double z = Double.parseDouble(args[2]);

                config.set("config.spawn.location.world", worldname);

                config.set("config.spawn.location.pos_x", x);
                config.set("config.spawn.location.pos_y", y);
                config.set("config.spawn.location.pos_z", z);

                config.set("config.spawn.location.yaw", 180);
                config.set("config.spawn.location.pitch", 0);

                plugin.config.saveConfigs();

                plugin.config.reloadConfig();

                String coordinates = Double.toString(x) + ", " + Double.toString(y) + ", " + Double.toString(z);

                sender.sendMessage(mm.deserialize(messages.getString("messages.setspawn.set"), Placeholder.unparsed("coordinates", coordinates)));

                return true;
            }

            case 4 -> {
                String worldname = args[3];

                Double x = Double.parseDouble(args[0]);
                Double y = Double.parseDouble(args[1]);
                Double z = Double.parseDouble(args[2]);

                config.set("config.spawn.location.world", worldname);

                config.set("config.spawn.location.pos_x", x);
                config.set("config.spawn.location.pos_y", y);
                config.set("config.spawn.location.pos_z", z);

                config.set("config.spawn.location.yaw", 180);
                config.set("config.spawn.location.pitch", 0);

                plugin.config.saveConfigs();

                plugin.config.reloadConfig();

                String coordinates = Double.toString(x) + ", " + Double.toString(y) + ", " + Double.toString(z);

                sender.sendMessage(mm.deserialize(messages.getString("messages.setspawn.set"), Placeholder.unparsed("coordinates", coordinates)));

                return true;
            }

            default -> {
                sender.sendMessage(mm.deserialize(messages.getString("messages.setspawn.usage")));
            
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        
        switch (args.length) {
            case 1 -> {
                arguments.add("[x position]");
            }
            case 2 -> {
                arguments.add("[y position]");
            }
            case 3 -> {
                arguments.add("[z position]");
            }
            case 4 -> {
                arguments.add("[world]");
            }
        }

        return arguments;
    }    
}
