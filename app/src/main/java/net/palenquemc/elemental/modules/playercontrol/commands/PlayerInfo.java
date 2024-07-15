package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.TimeUtils;

public class PlayerInfo implements TabExecutor {
    private Elemental plugin;

    public PlayerInfo(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        TimeUtils time = new TimeUtils();

        if(!sender.hasPermission("elmental.playerinfo")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        if(args.length == 1) {
            OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(args[0]);

            if(targetPlayer.getLastSeen() == 0) {
                sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found"), Placeholder.unparsed("target_player", args[0])));
                    
                return true;
            }

            Component generalInfo = mm.deserialize(playerControl.getString("player_control_module.player_info.base"),
            Placeholder.unparsed("target_player", targetPlayer.getName()),
            Placeholder.unparsed("first_join", time.longToDateString(targetPlayer.getFirstPlayed())),
            Placeholder.unparsed("last_seen", time.longToDateString(targetPlayer.getLastLogin())));

            if(targetPlayer.isOnline()) {
                Player targetPlayerOnline = targetPlayer.getPlayer();

                String gamemode = targetPlayerOnline.getGameMode().toString().toLowerCase();
                String formattedGamemode = gamemode.substring(0, 1).toUpperCase() + gamemode.substring(1);

                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.player_info.online_info"),
                    Placeholder.parsed("general_info", mm.serialize(generalInfo)),
                    Placeholder.unparsed("ip_address", targetPlayerOnline.getAddress().toString()),
                    Placeholder.parsed("nickname", mm.serialize(targetPlayerOnline.displayName())),
                    Placeholder.unparsed("gamemode", formattedGamemode),
                    Placeholder.unparsed("world", targetPlayerOnline.getWorld().getName())));
            
                return true;
            } else {
                sender.sendMessage(generalInfo);
            }

            return true;
        } else {
            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.player_info.usage")));

            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            arguments.add(player.getName());
        });

        return arguments;
    }   
}
