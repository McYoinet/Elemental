package net.palenquemc.elemental.modules.spawn.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class PlayerJoinListener implements Listener {

    private Elemental plugin;
    
    public PlayerJoinListener(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration spawn = plugin.config.getConfig("spawn.yml");

        ChatUtils chat = new ChatUtils(plugin);
        Player player = event.getPlayer();

        String playerMessage = chat.papi(player, spawn.getString("spawn_module.messages.player_join_actions.player_message.text"));
        String serverMessage = chat.papi(player, spawn.getString("spawn_module.messages.player_join_actions.server_message.text"));
        String invalidScope = chat.papi(player, spawn.getString("spawn_module.messages.invalid_scope"));
        String pathTitle = chat.papi(player, spawn.getString("spawn_module.messages.player_join_actions.player_title.main_title"));
        String pathSubtitle = chat.papi(player, spawn.getString("spawn_module.messages.player_join_actions.player_title.subtitle"));
        String pathServerTitle = chat.papi(player, spawn.getString("spawn_module.messages.player_join_actions.server_title.main_title"));
        String pathServerSubtitle = chat.papi(player, spawn.getString("spawn_module.messages.player_join_actions.server_title.subtitle"));

        // Server message
        if(spawn.getBoolean("spawn_module.messages.player_join_actions.server_message.enable")) {
            event.joinMessage(null);

            switch(spawn.getString("spawn_module.messages.player_join_actions.server_message.scope")) {
                case "world" -> {
                    player.getWorld().sendMessage(mm.deserialize(serverMessage, Placeholder.unparsed("player", player.getName())));
                }
                
                case "global" -> {
                    List<String> blacklistedWorlds = spawn.getStringList("spawn_module.messages.player_join_actions.server_message.blacklisted_worlds");
                
                    for(World world : plugin.getServer().getWorlds()) {
                        if(!blacklistedWorlds.contains(world.getName())) {
                            world.sendMessage(mm.deserialize(serverMessage, Placeholder.unparsed("player", player.getName())));
                        }
                    }
                }

                default -> {
                    Bukkit.getConsoleSender().sendMessage(mm.deserialize(invalidScope, Placeholder.unparsed("path", "spawn_module.messages.player_join_actions.server_message.scope")));
                }
            }
        }

        // Player message
        if(spawn.getBoolean("spawn_module.messages.player_join_actions.player_message.enable")) {
            player.sendMessage(mm.deserialize(playerMessage, Placeholder.unparsed("player", player.getName())));
        }

        // Player title
        if(spawn.getBoolean("spawn_module.messages.player_join_actions.player_title.enable")) {
            Component mainTitle = mm.deserialize(pathTitle, Placeholder.parsed("player", player.getName()));
            Component subtitle = mm.deserialize(pathSubtitle, Placeholder.parsed("player", player.getName()));
        
            Title title = Title.title(mainTitle, subtitle);

            player.showTitle(title);
        }

        // Server title
        if(spawn.getBoolean("spawn_module.messages.player_join_actions.server_title.enable")) {
            Component mainTitle = mm.deserialize(pathServerTitle, Placeholder.parsed("player", player.getName()));
            Component subtitle = mm.deserialize(pathServerSubtitle, Placeholder.parsed("player", player.getName()));

            Title title = Title.title(mainTitle, subtitle);

            switch(spawn.getString("spawn_module.messages.player_join_actions.server_title.scope")) {
                case "world" -> {
                    player.getWorld().showTitle(title);
                }
                
                case "global" -> {
                    List<String> blacklistedWorlds = spawn.getStringList("spawn_module.messages.player_join_actions.server_title.blacklisted_worlds");
                
                    for(World world : plugin.getServer().getWorlds()) {
                        if(!blacklistedWorlds.contains(world.getName())) {
                            world.showTitle(title);
                        }
                    }
                }

                default -> {
                    Bukkit.getConsoleSender().sendMessage(mm.deserialize(invalidScope, Placeholder.unparsed("path", "spawn_module.messages.player_join_actions.server_message.scope")));
                }
            }
        }

        // Player sound
        if(spawn.getBoolean("spawn_module.messages.player_join_actions.player_sound.enable")) {
            String source = spawn.getString("spawn_module.messages.player_join_actions.player_sound.source");
            String key = spawn.getString("spawn_module.messages.player_join_actions.player_sound.key");

            float volume = Float.parseFloat(spawn.getString("spawn_module.messages.player_join_actions.player_sound.volume"));
            float pitch = Float.parseFloat(spawn.getString("spawn_module.messages.player_join_actions.player_sound.pitch"));

            Sound sound = Sound.sound(Key.key(key), Sound.Source.valueOf(source), volume, pitch);

            player.playSound(sound);
        }

        // Server sound
        if(spawn.getBoolean("spawn_module.messages.player_join_actions.server_title.enable")) {
            String source = spawn.getString("spawn_module.messages.player_join_actions.server_sound.source");
            String key = spawn.getString("spawn_module.messages.player_join_actions.server_sound.key");

            float volume = Float.parseFloat(spawn.getString("spawn_module.messages.player_join_actions.server_sound.volume"));
            float pitch = Float.parseFloat(spawn.getString("spawn_module.messages.player_join_actions.server_sound.pitch"));

            Sound sound = Sound.sound(Key.key(key), Sound.Source.valueOf(source), volume, pitch);

            switch(spawn.getString("spawn_module.messages.player_join_actions.server_sound.scope")) {
                case "world" -> {
                    player.getWorld().playSound(sound);
                }
                
                case "global" -> {
                    List<String> blacklistedWorlds = spawn.getStringList("spawn_module.messages.player_join_actions.server_sound.blacklisted_worlds");
                
                    for(World world : plugin.getServer().getWorlds()) {
                        if(!blacklistedWorlds.contains(world.getName())) {
                            world.playSound(sound);
                        }
                    }
                }

                default -> {
                    Bukkit.getConsoleSender().sendMessage(mm.deserialize(invalidScope, Placeholder.unparsed("path", "spawn_module.messages.player_join_actions.server_message.scope")));
                }
            }
        }
    }
    
}
