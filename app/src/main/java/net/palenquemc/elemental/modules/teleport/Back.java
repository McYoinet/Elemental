package net.palenquemc.elemental.modules.teleport;

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

public class Back implements TabExecutor {
    private Elemental plugin;
    
    public Back(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission("elemental.teleport.back")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));

            return true;
        }

        Player player = (Player) sender;

        if(args.length > 0) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.back.usage")));

            return true;
        }

        Location loc = player.getLastDeathLocation();

        if(loc == null) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.back.no_deaths_found")));

            return true;
        }

        if(loc.getWorld() == null) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.back.world_not_found")));

            return true;
        }

        String world = loc.getWorld().getName();
        String targetDestination = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ());

        player.teleport(loc);

        player.sendMessage(mm.deserialize(messages.getString("messages.back.teleported"), Placeholder.unparsed("target_destination", targetDestination), Placeholder.unparsed("world", world)));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }    
}
