package net.palenquemc.elemental.modules.playercontrol.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.NameUtils;

public class NicknameApplyJoin implements Listener {
    private final Elemental plugin;

    public NicknameApplyJoin(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();
    
    @EventHandler
    public void applyNicknameOnJoin(PlayerJoinEvent event) {
        NameUtils names = new NameUtils(plugin);

        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        Player player = event.getPlayer();

        if(playerControl.getBoolean("player_control_module.nickname.keep_nickname_after_disconnect")) {
            if(names.hasNickname(player.getName())) {
                String nickname = names.getNickname(player.getName());

                player.displayName(mm.deserialize("<nickname>", Placeholder.unparsed("nickname", nickname)));
                player.playerListName(mm.deserialize("<nickname>", Placeholder.unparsed("nickname", nickname)));
            }
        }
    }
}
