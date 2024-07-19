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

public class Back implements TabExecutor {
    private final Elemental plugin;
    
    public Back(Elemental plugin) {
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
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String usage = chat.papi(player, teleport.getString("teleport_module.back.usage"));
        String noDeathsFound = chat.papi(player, teleport.getString("teleport_module.back.no_deaths_found"));
        String worldNotFound = chat.papi(player, teleport.getString("teleport_module.back.world_not_found"));
        String teleported = chat.papi(player, teleport.getString("teleport_module.back.teleported"));

        if(!sender.hasPermission("elemental.teleport.back")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        if(player == null) {
            sender.sendMessage(mm.deserialize(executableFromPlayer));

            return true;
        }

        if(args.length > 0) {
            sender.sendMessage(mm.deserialize(usage));

            return true;
        }

        Location loc = player.getLastDeathLocation();

        if(loc == null) {
            sender.sendMessage(mm.deserialize(noDeathsFound));

            return true;
        }

        if(loc.getWorld() == null) {
            sender.sendMessage(mm.deserialize(worldNotFound));

            return true;
        }

        String world = loc.getWorld().getName();
        String targetDestination = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " + Double.toString(loc.getZ());

        player.teleport(loc);

        player.sendMessage(mm.deserialize(teleported, Placeholder.unparsed("target_destination", targetDestination), Placeholder.unparsed("world", world)));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }    
}
