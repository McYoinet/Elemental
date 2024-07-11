package net.palenquemc.elemental.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.palenquemc.elemental.Elemental;

public class PlayerQuitListener implements Listener {

    private Elemental plugin;
    
    public PlayerQuitListener(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");
        Player player = event.getPlayer();

        // Server message
        if(messages.getBoolean("messages.player_quit.actions.server_message.enable")) {
            event.quitMessage(null);

            switch(messages.getString("messages.player_quit.actions.server_message.scope")) {
                case "world" -> {
                    player.getWorld().sendMessage(mm.deserialize(messages.getString("messages.player_quit.actions.server_message.text"), Placeholder.unparsed("player", player.getName())));
                }
                
                case "global" -> {
                    List<String> blacklistedWorlds = messages.getStringList("messages.player_quit.actions.server_message.blacklisted_worlds");
                
                    for(World world : plugin.getServer().getWorlds()) {
                        if(!blacklistedWorlds.contains(world.getName())) {
                            world.sendMessage(mm.deserialize(messages.getString("messages.player_quit.actions.server_message.text"), Placeholder.unparsed("player", player.getName())));
                        }
                    }
                }

                default -> {
                    Bukkit.getConsoleSender().sendMessage(mm.deserialize(messages.getString("messages.player_quit.invalid_scope"), Placeholder.unparsed("path", "messages.player_quit.actions.server_message.scope")));
                }
            }
        }

        // Server title
        if(messages.getBoolean("messages.player_quit.actions.server_title.enable")) {
            Component mainTitle = mm.deserialize(messages.getString("messages.player_quit.actions.server_title.main_title"), Placeholder.parsed("player", player.getName()));
            Component subtitle = mm.deserialize(messages.getString("messages.player_quit.actions.server_title.subtitle"), Placeholder.parsed("player", player.getName()));

            Title title = Title.title(mainTitle, subtitle);

            switch(messages.getString("messages.player_quit.actions.server_title.scope")) {
                case "world" -> {
                    player.getWorld().showTitle(title);
                }
                
                case "global" -> {
                    List<String> blacklistedWorlds = messages.getStringList("messages.player_quit.actions.server_title.blacklisted_worlds");
                
                    for(World world : plugin.getServer().getWorlds()) {
                        if(!blacklistedWorlds.contains(world.getName())) {
                            world.showTitle(title);
                        }
                    }
                }

                default -> {
                    Bukkit.getConsoleSender().sendMessage(mm.deserialize(messages.getString("messages.player_quit.invalid_scope"), Placeholder.unparsed("path", "messages.player_quit.actions.server_message.scope")));
                }
            }
        }

        // Server sound
        if(messages.getBoolean("messages.player_quit.actions.server_title.enable")) {
            String source = messages.getString("messages.player_quit.actions.server_sound.source");
            String key = messages.getString("messages.player_quit.actions.server_sound.key");

            float volume = Float.parseFloat(messages.getString("messages.player_quit.actions.server_sound.volume"));
            float pitch = Float.parseFloat(messages.getString("messages.player_quit.actions.server_sound.pitch"));

            Sound sound = Sound.sound(Key.key(key), Sound.Source.valueOf(source), volume, pitch);

            switch(messages.getString("messages.player_quit.actions.server_sound.scope")) {
                case "world" -> {
                    player.getWorld().playSound(sound);
                }
                
                case "global" -> {
                    List<String> blacklistedWorlds = messages.getStringList("messages.player_quit.actions.server_sound.blacklisted_worlds");
                
                    for(World world : plugin.getServer().getWorlds()) {
                        if(!blacklistedWorlds.contains(world.getName())) {
                            world.playSound(sound);
                        }
                    }
                }

                default -> {
                    Bukkit.getConsoleSender().sendMessage(mm.deserialize(messages.getString("messages.player_quit.invalid_scope"), Placeholder.unparsed("path", "messages.player_quit.actions.server_message.scope")));
                }
            }
        }
    }
    
}
