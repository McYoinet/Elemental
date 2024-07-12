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
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration spawn = plugin.config.getConfig("spawn.yml");

        if(!sender.hasPermission("elmental.spawn")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));
        
            return true;
        }

        Player player = (Player) sender;

        String worldname = spawn.getString("spawn_module.spawn.location.world");
        World world = plugin.getServer().getWorld(worldname);

        if(world == null) {
            player.sendMessage(mm.deserialize(spawn.getString("spawn_module.messages.world_not_found"), Placeholder.unparsed("world", worldname)));

            return true;
        }

        Double x = spawn.getDouble("spawn_module.spawn.location.pos_x");
        Double y = spawn.getDouble("spawn_module.spawn.location.pos_y");
        Double z = spawn.getDouble("spawn_module.spawn.location.pos_z");

        float yaw = (float) spawn.getDouble("spawn_module.spawn.location.yaw");
        float pitch = (float) spawn.getDouble("spawn_module.spawn.location.pitch");

        Location spawnpoint = new Location(world, x, y, z, yaw, pitch);

        player.teleport(spawnpoint);
        player.sendMessage(mm.deserialize(spawn.getString("spawn_module.messages.teleported")));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }

    
    
}
