package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class Gamemode implements TabExecutor {
    private final Elemental plugin;

    public Gamemode(Elemental plugin) {
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

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.gamemode.usage"));
        String unknownGamemode = chat.papi(player, playerControl.getString("player_control_module.gamemode.unknown_gamemode"));
        String gmSetAll = chat.papi(player, playerControl.getString("player_control_module.gamemode.set.all"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String serverIsEmpty = chat.papi(player, core.getString("core_module.server_is_empty"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String gmSetSelf = chat.papi(player, playerControl.getString("player_control_module.gamemode.set.self"));

        String gmSetByOther = playerControl.getString("player_control_module.gamemode.set.by_other");
        String gmSetToOther = playerControl.getString("player_control_module.gamemode.set.to_other");

        if(!sender.hasPermission("elemental.gamemode")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(usage));
            
            return true;
        }

        if(gamemodeString(args[0]) == null) {
            sender.sendMessage(mm.deserialize(unknownGamemode));

            return true;
        }

        GameMode gm = GameMode.valueOf(gamemodeString(args[0]));
        String gmString = StringUtils.capitalize(gm.toString().toLowerCase());

        if(args.length > 1) {
            if(!sender.hasPermission("elemental.gamemode.others")) {
                sender.sendMessage(mm.deserialize(noPerms));

                return true;
            }

            if(args.length == 2) {
                if(args[1].equalsIgnoreCase("@a")) {
                    for(Player p : plugin.getServer().getOnlinePlayers()) {
                        p.setGameMode(gm);

                        gmSetByOther = chat.papi(p, gmSetByOther);

                        p.sendMessage(mm.deserialize(gmSetByOther, Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("command_sender", sender.getName())));                       
                    }

                    sender.sendMessage(mm.deserialize(gmSetAll, Placeholder.unparsed("gamemode", gmString)));

                    return true;
                }

                Player targetPlayer = plugin.getServer().getPlayer(args[1]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[1])));

                    return true;
                }

                targetPlayer.setGameMode(gm);

                gmSetByOther = chat.papi(targetPlayer, gmSetByOther);
                gmSetToOther = chat.papi(targetPlayer, gmSetToOther);

                targetPlayer.sendMessage(mm.deserialize(gmSetByOther, Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("command_sender", sender.getName())));
                sender.sendMessage(mm.deserialize(gmSetToOther, Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("target_player", targetPlayer.getName())));

                return true;
            } else if(args.length > 2) {
                ArrayList<String> targetPlayersList = new ArrayList<>();

                for (int i = 1; i < args.length - 1; i++) {
                    if(plugin.getServer().getPlayer(args[i]) != null) {
                        targetPlayersList.add(args[i]);
                    }
                }

                if(targetPlayersList.isEmpty()) {
                    sender.sendMessage(mm.deserialize(serverIsEmpty));

                    return true;
                }

                for(String name : targetPlayersList) {
                    Player targetPlayer = plugin.getServer().getPlayer(name);

                    gmSetByOther = chat.papi(targetPlayer, gmSetByOther);
                    gmSetToOther = chat.papi(targetPlayer, gmSetToOther);

                    targetPlayer.sendMessage(mm.deserialize(gmSetByOther, Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("command_sender", sender.getName())));
                    sender.sendMessage(mm.deserialize(gmSetToOther, Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("target_player", targetPlayer.getName())));
                }

                return true;
            }

        } else if(args.length == 1) {
            if(player == null) {
                sender.sendMessage(mm.deserialize(executableFromPlayer));

                return true;
            }

            player.setGameMode(gm);
            sender.sendMessage(mm.deserialize(gmSetSelf, Placeholder.unparsed("gamemode", gmString)));

            return true;
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {

            arguments.add("survival");
            arguments.add("creative");
            arguments.add("adventure");
            arguments.add("spectator");

            arguments.add("0");
            arguments.add("1");
            arguments.add("2");
            arguments.add("3");
        } else if(args.length > 1) {
            arguments.add("[player]");
            arguments.add("@a");
            
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                arguments.add(player.getName());
            }
        }

        return arguments;
    }

    private String gamemodeString(String value) {
        if(value.equalsIgnoreCase("0") || value.equalsIgnoreCase("s") || value.equalsIgnoreCase("survival")) {
            return "SURVIVAL";
        } else if(value.equalsIgnoreCase("1") || value.equalsIgnoreCase("c") || value.equalsIgnoreCase("creative")) {
            return "CREATIVE";
        } else if(value.equalsIgnoreCase("2") || value.equalsIgnoreCase("a") || value.equalsIgnoreCase("adventure")) {
            return "ADVENTURE";
        } else if(value.equalsIgnoreCase("3") || value.equalsIgnoreCase("sp") || value.equalsIgnoreCase("spectator")) {
            return "SPECTATOR";
        }

        return null;
    }
}
