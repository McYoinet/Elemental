package net.palenquemc.elemental.utils;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.palenquemc.elemental.Elemental;

public class ChatUtils {
    private Elemental plugin;

    public ChatUtils(Elemental plugin) {
        this.plugin = plugin;
    }

    public String papi(Player player, String text) {
        if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        } else return text;
    }
}
