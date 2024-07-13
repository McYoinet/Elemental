package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class XPCommand implements TabExecutor {
        private Elemental plugin;
    
    public XPCommand(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        if(!sender.hasPermission("elmental.xp")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        switch(args.length) {
            case 2 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;
                int amount = Integer.parseInt(args[1]);

                switch(args[0]) {
                    case "set" -> {
                        player.setExperienceLevelAndProgress(amount);

                        player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.set.self"), Placeholder.unparsed("amount", String.valueOf(amount))));
                    
                        return true;
                    }

                    case "add" -> {
                        player.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() + amount);

                        player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.add.self"), Placeholder.unparsed("amount", String.valueOf(amount))));
                    
                        return true;
                    }

                    case "remove" -> {
                        player.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() - amount);

                        player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.remove.self"), Placeholder.unparsed("amount", String.valueOf(amount))));
                    
                        return true;
                    }

                    default -> {
                        player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.usage")));
                    
                        return true;
                    }
                }
            }

            case 3 -> {
                Player targetPlayer = plugin.getServer().getPlayer(args[2]);
                int amount = Integer.parseInt(args[1]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found")));

                    return true;
                }

                if(!sender.hasPermission("elemental.xp.others")) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                    
                    return true;
                }

                switch(args[0]) {
                    case "set" -> {
                        targetPlayer.setExperienceLevelAndProgress(amount);

                        targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.set.by_other"), Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("command_sender", sender.getName())));
                        sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.set.to_other"), Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                        return true;
                    }

                    case "add" -> {
                        targetPlayer.setExperienceLevelAndProgress(targetPlayer.calculateTotalExperiencePoints() + amount);

                        targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.add.by_other"), Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("command_sender", sender.getName())));
                        sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.add.to_other"), Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                        return true;
                    }

                    case "remove" -> {
                        targetPlayer.setExperienceLevelAndProgress(targetPlayer.calculateTotalExperiencePoints() + amount);

                        targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.remove.by_other"), Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("command_sender", sender.getName())));
                        sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.remove.to_other"), Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                        return true;
                    }

                    default -> {
                        targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.usage")));
                    
                        return true;
                    }
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.xp.usage")));

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        switch(args.length) {
            case 1 -> {
                arguments.add("set");
                arguments.add("add");
                arguments.add("remove");
            }

            case 2 -> {
                arguments.add("<amount>");
            }

            case 3 -> {
                arguments.add("[player]");

                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    arguments.add(player.getName());
                });
            }
        }

        return arguments;
    }
}
