package net.palenquemc.elemental.utils;


import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
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
        MiniMessage format = MiniMessage.builder()
            .tags(TagResolver.builder()
            .resolver(StandardTags.color())
            .resolver(StandardTags.decorations())
            .build()
            )
            .build();

        if(plugin.getServer().getPlayer(nickname) != null) {
            return false;
        }

        if(!isNickname(nickname)) {

            plugin.nicknames.put(target, nickname);

            Player player = plugin.getServer().getPlayer(target);

            if(player != null) {
                player.displayName(format.deserialize("<nickname>", Placeholder.unparsed("nickname", nickname)));
                player.playerListName(format.deserialize("<nickname>", Placeholder.unparsed("nickname", nickname)));
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
