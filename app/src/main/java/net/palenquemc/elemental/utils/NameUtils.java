package net.palenquemc.elemental.utils;


import java.util.HashMap;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class NameUtils {
    private final Elemental plugin;

    public NameUtils(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    public HashMap<String, String> getNicknamesHashMap() {
        return plugin.nicknames;
    }

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
                if(target.equals(nick)) {
                    realname.append(playername);
                }
            });

            return realname.toString();
        } else return null;
    }

    public String getNickname(String target) {
        if(hasNickname(target)) {
            return plugin.nicknames.get(target);
        } else return null;
    }

    public boolean containsTags(String string) {
        if(string.contains("<") || string.contains(">")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean setNickname(String target, String nickname) {
        if(plugin.getServer().getPlayer(nickname) != null) {
            return false;
        }

        if(!isNickname(nickname)) {

            plugin.nicknames.put(target, nickname);

            Player player = plugin.getServer().getPlayer(target);

            if(player != null) {
                /* 
                 * Applying nicknames in this way, ie replacing them in Placeholders
                 * with the unparsed method, prevents issues when using MiniMessage tags in them
                 * Also if the nickname had tag colors (like <red>) they'd still be displayed in color
                 * with MiniMessage supporting chat plugins, as it sends them directly as <tag>Nickname
                */

                player.displayName(mm.deserialize("<nickname>", Placeholder.unparsed("nickname", nickname)));
                player.playerListName(mm.deserialize("<nickname>", Placeholder.unparsed("nickname", nickname)));
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
