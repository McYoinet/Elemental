package net.palenquemc.elemental.modules.servercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.palenquemc.elemental.Elemental;

public class Broadcast implements TabExecutor {
    private Elemental plugin;
    
    public Broadcast(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration serverControl = plugin.config.getConfig("server_control.yml");

        if(!sender.hasPermission("elmental.broadcast")) {
            sender.sendMessage(mm.deserialize(core.getString("core.insufficient_permissions")));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(serverControl.getString("server_control_module.broadcast.missing_message")));

            return true;
        }

        String message = String.join(" ", args);

        plugin.getServer().sendMessage(mm.deserialize(serverControl.getString("server_control_module.broadcast.broadcast"), Placeholder.parsed("message", message)));

        if(core.getBoolean("server_control_module.broadcast.sound.enable")) {
            String source = core.getString("server_control_module.broadcast.sound.source");
            String key = core.getString("server_control_module.broadcast.sound.key");

            float volume = Float.parseFloat(core.getString("server_control_module.broadcast.sound.volume"));
            float pitch = Float.parseFloat(core.getString("server_control_module.broadcast.sound.pitch"));

            Sound sound = Sound.sound(Key.key(key), Sound.Source.valueOf(source), volume, pitch);
            
            plugin.getServer().playSound(sound);
        }

        if(core.getBoolean("server_control_module.broadcast.title.enable")) {
            Component mainTitle = mm.deserialize(core.getString("server_control_module.broadcast.title.main_title"), Placeholder.parsed("message", message));
            Component subtitle = mm.deserialize(core.getString("server_control_module.broadcast.title.subtitle"), Placeholder.parsed("message", message));

            Title title = Title.title(mainTitle, subtitle);

            plugin.getServer().showTitle(title);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("<message>");
        }

        return arguments;
    }
}
