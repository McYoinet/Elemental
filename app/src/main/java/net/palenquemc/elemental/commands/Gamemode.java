package net.palenquemc.elemental.commands;

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

public class Gamemode implements TabExecutor {
    private Elemental plugin;

    public Gamemode(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission("elemental.gamemode")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.gamemode.usage")));
            
            return true;
        }

        if(gamemodeString(args[0]) == null) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.gamemode.unknown_gamemode")));

            return true;
        }

        GameMode gm = GameMode.valueOf(gamemodeString(args[0]));
        String gmString = StringUtils.capitalize(gm.toString().toLowerCase());

        if(args.length > 1) {
            if(!sender.hasPermission("elemental.gamemode.others")) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));

                return true;
            }

            if(args.length == 2) {
                if(args[1].equalsIgnoreCase("@a")) {
                    for(Player player : plugin.getServer().getOnlinePlayers()) {
                        player.setGameMode(gm);

                        player.sendMessage(mm.deserialize(messages.getString("messages.gamemode.set.by_other"), Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("command_sender", sender.getName())));                       
                    }

                    sender.sendMessage(mm.deserialize(messages.getString("messages.gamemode.set.all"), Placeholder.unparsed("gamemode", gmString)));

                    return true;
                }

                Player targetPlayer = plugin.getServer().getPlayer(args[1]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.target_not_found"), Placeholder.unparsed("target_player", args[1])));

                    return true;
                }

                targetPlayer.setGameMode(gm);

                targetPlayer.sendMessage(mm.deserialize(messages.getString("messages.gamemode.set.by_other"), Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("command_sender", sender.getName())));
                sender.sendMessage(mm.deserialize(messages.getString("messages.gamemode.set.to_other"), Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("target_player", targetPlayer.getName())));

                return true;
            } else if(args.length > 2) {
                ArrayList<String> targetPlayersList = new ArrayList<>();

                for (int i = 1; i < args.length - 1; i++) {
                    if(plugin.getServer().getPlayer(args[i]) != null) {
                        targetPlayersList.add(args[i]);
                    }
                }

                if(targetPlayersList.isEmpty()) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.server_is_empty")));

                    return true;
                }

                for(String name : targetPlayersList) {
                    Player targetPlayer = plugin.getServer().getPlayer(name);

                    targetPlayer.sendMessage(mm.deserialize(messages.getString("messages.gamemode.set.by_other"), Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("command_sender", sender.getName())));
                    sender.sendMessage(mm.deserialize(messages.getString("messages.gamemode.set.to_other"), Placeholder.unparsed("gamemode", gmString), Placeholder.unparsed("target_player", targetPlayer.getName())));
                }

                return true;
            }

        } else if(args.length == 1) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));

                return true;
            }

            Player player = (Player) sender;

            player.setGameMode(gm);
            sender.sendMessage(mm.deserialize(messages.getString("messages.gamemode.set.self"), Placeholder.unparsed("gamemode", gmString)));

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
