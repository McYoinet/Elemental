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
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission("elmental.broadcast")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.broadcast.missing_message")));

            return true;
        }

        String message = String.join(" ", args);

        plugin.getServer().sendMessage(mm.deserialize(messages.getString("messages.broadcast.broadcast"), Placeholder.parsed("message", message)));

        if(messages.getBoolean("messages.broadcast.sound.enable")) {
            String source = messages.getString("messages.broadcast.sound.source");
            String key = messages.getString("messages.broadcast.sound.key");

            float volume = Float.parseFloat(messages.getString("messages.broadcast.sound.volume"));
            float pitch = Float.parseFloat(messages.getString("messages.broadcast.sound.pitch"));

            Sound sound = Sound.sound(Key.key(key), Sound.Source.valueOf(source), volume, pitch);
            
            plugin.getServer().playSound(sound);
        }

        if(messages.getBoolean("messages.broadcast.title.enable")) {
            Component mainTitle = mm.deserialize(messages.getString("messages.broadcast.title.main_title"), Placeholder.parsed("message", message));
            Component subtitle = mm.deserialize(messages.getString("messages.broadcast.title.subtitle"), Placeholder.parsed("message", message));

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
