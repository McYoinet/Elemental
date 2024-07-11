package net.palenquemc.elemental.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class PlayerSpawnLocationListener implements Listener {
    
    private Elemental plugin;
    
    public PlayerSpawnLocationListener(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");
        FileConfiguration config = plugin.config.getConfig("config.yml");

        if(config.getBoolean("config.spawn.force_on_join")) {
            String worldname = config.getString("config.spawn.location.world");
            World spawnWorld = plugin.getServer().getWorld(worldname);

            if(spawnWorld == null) {
                Bukkit.getConsoleSender().sendMessage(mm.deserialize(messages.getString("messages.spawn.world_not_found"), Placeholder.unparsed("world", worldname)));
            
                return;
            }

            double x = config.getDouble("config.spawn.location.pos_x");
            double y = config.getDouble("config.spawn.location.pos_y");
            double z = config.getDouble("config.spawn.location.pos_z");

            float yaw = (float) config.getDouble("config.spawn.location.yaw");
            float pitch = (float) config.getDouble("config.spawn.location.pitch");

            Location loc = new Location(spawnWorld, x, y, z, yaw, pitch);

            event.setSpawnLocation(loc);
        }
    }
}
