package net.palenquemc.elemental.modules.playercontrol.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.NameUtils;

public class NicknameRemoveLeave implements Listener {
    private final Elemental plugin;

    public NicknameRemoveLeave(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();
    
    @EventHandler
    public void applyNicknameOnJoin(PlayerQuitEvent event) {
        NameUtils names = new NameUtils(plugin);

        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        Player player = event.getPlayer();

        if(!playerControl.getBoolean("player_control_module.nickname.keep_nickname_after_disconnect")) {
            if(names.hasNickname(player.getName())) {
                names.clearNickname(player.getName());
            }
        }
    }
}
