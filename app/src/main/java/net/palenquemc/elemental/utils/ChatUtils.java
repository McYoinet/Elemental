package net.palenquemc.elemental.utils;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatUtils {
    public String papi(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
