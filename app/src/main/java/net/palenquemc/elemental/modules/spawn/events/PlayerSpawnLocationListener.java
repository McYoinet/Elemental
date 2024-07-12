package net.palenquemc.elemental.modules.spawn.events;

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
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration spawn = plugin.config.getConfig("spawn.yml");

        if(spawn.getBoolean("config.spawn.force_on_join")) {
            String worldname = spawn.getString("spawn_module.spawn.location.world");
            World spawnWorld = plugin.getServer().getWorld(worldname);

            if(spawnWorld == null) {
                Bukkit.getConsoleSender().sendMessage(mm.deserialize(core.getString("spawn_module.messages.world_not_found"), Placeholder.unparsed("world", worldname)));
            
                return;
            }

            double x = spawn.getDouble("spawn_module.spawn.location.pos_x");
            double y = spawn.getDouble("spawn_module.spawn.location.pos_y");
            double z = spawn.getDouble("spawn_module.spawn.location.pos_z");

            float yaw = (float) spawn.getDouble("spawn.spawn.location.yaw");
            float pitch = (float) spawn.getDouble("spawn.spawn.location.pitch");

            Location loc = new Location(spawnWorld, x, y, z, yaw, pitch);

            event.setSpawnLocation(loc);
        }
    }
}
