package net.palenquemc.elemental.modules.spawn.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class Spawn implements TabExecutor {
    private Elemental plugin;
    
    public Spawn(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");
        FileConfiguration config = plugin.config.getConfig("config.yml");

        if(!sender.hasPermission("elmental.spawn")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));
        
            return true;
        }

        Player player = (Player) sender;

        String worldname = config.getString("config.spawn.location.world");
        World world = plugin.getServer().getWorld(worldname);

        if(world == null) {
            player.sendMessage(mm.deserialize(messages.getString("messages.spawn.world_not_found"), Placeholder.unparsed("world", worldname)));

            return true;
        }

        Double x = config.getDouble("config.spawn.location.pos_x");
        Double y = config.getDouble("config.spawn.location.pos_y");
        Double z = config.getDouble("config.spawn.location.pos_z");

        float yaw = (float) config.getDouble("config.spawn.location.yaw");
        float pitch = (float) config.getDouble("config.spawn.location.pitch");

        Location spawnpoint = new Location(world, x, y, z, yaw, pitch);

        player.teleport(spawnpoint);
        player.sendMessage(mm.deserialize(messages.getString("messages.spawn.teleported")));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }

    
    
}
