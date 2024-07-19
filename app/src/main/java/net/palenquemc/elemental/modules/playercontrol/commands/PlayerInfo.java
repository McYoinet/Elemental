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
import net.palenquemc.elemental.utils.ChatUtils;
import net.palenquemc.elemental.utils.TimeUtils;

public class PlayerInfo implements TabExecutor {
    private final Elemental plugin;

    public PlayerInfo(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        ChatUtils chat = new ChatUtils();

        Player player = null;
        if(sender instanceof Player p) player = p;

        TimeUtils time = new TimeUtils();

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.player_info.usage"));

        String playerInfoBase = playerControl.getString("player_control_module.player_info.base");
        String playerInfoOnline = playerControl.getString("player_control_module.player_info.online_info");

        if(!sender.hasPermission("elmental.playerinfo")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        if(args.length == 1) {
            OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(args[0]);

            if(targetPlayer.getLastSeen() == 0) {
                sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));
                    
                return true;
            }

            playerInfoBase = chat.papi(targetPlayer.getPlayer(), playerInfoBase);

            Component generalInfo = mm.deserialize(playerInfoBase,
            Placeholder.unparsed("target_player", targetPlayer.getName()),
            Placeholder.unparsed("first_join", time.longToDateString(targetPlayer.getFirstPlayed())),
            Placeholder.unparsed("last_seen", time.longToDateString(targetPlayer.getLastLogin())));

            if(targetPlayer.isOnline()) {
                Player targetPlayerOnline = targetPlayer.getPlayer();

                playerInfoOnline = chat.papi(targetPlayerOnline, playerInfoOnline);

                String gamemode = targetPlayerOnline.getGameMode().toString().toLowerCase();
                String formattedGamemode = gamemode.substring(0, 1).toUpperCase() + gamemode.substring(1);

                sender.sendMessage(mm.deserialize(playerInfoOnline,
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
            sender.sendMessage(mm.deserialize(usage));

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
