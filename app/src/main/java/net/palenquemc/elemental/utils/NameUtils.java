package net.palenquemc.elemental.utils;


import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.palenquemc.elemental.Elemental;

public class NameUtils {
    private Elemental plugin;

    public NameUtils(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    public boolean isNickname(String target) {
        return plugin.nicknames.containsValue(target);
    }

    public boolean hasNickname(String target) {
        return plugin.nicknames.containsKey(target);
    }

    public String getRealName(String target) {
        if(isNickname(target)) {
            StringBuilder realname = new StringBuilder();

            plugin.nicknames.forEach((playername, nick) -> {
                realname.append(playername);
            });

            return realname.toString();
        } else return null;
    }

    public String getNickname(String target) {
        if(hasNickname(target)) {
            return plugin.nicknames.get(target);
        } else return null;
    }

    public boolean setNickname(String target, String nickname) {
        if(plugin.getServer().getPlayer(nickname) != null) {
            return false;
        }

        if(!isNickname(nickname)) {
            plugin.nicknames.put(target, nickname);

            Player player = plugin.getServer().getPlayer(target);

            if(player != null) {
                player.displayName(mm.deserialize(nickname));
                player.playerListName(mm.deserialize(nickname));
            }

            return true;
        } else return false;
    }

    public boolean clearNickname(String target) {
        if(hasNickname(target)) {
            plugin.nicknames.remove(target);

            Player player = plugin.getServer().getPlayer(target);

            if(player != null) {
                player.displayName(mm.deserialize(player.getName()));
                player.playerListName(mm.deserialize(player.getName()));
            }

            return true;
        } else return false;
    }
}
