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
import net.palenquemc.elemental.utils.ChatUtils;

public class XPCommand implements TabExecutor {
    private final Elemental plugin;
    
    public XPCommand(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String setXPSelf = chat.papi(player, playerControl.getString("player_control_module.xp.set.self"));
        String addXPSelf = chat.papi(player, playerControl.getString("player_control_module.xp.add.self"));
        String removeXPSelf = chat.papi(player, playerControl.getString("player_control_module.xp.remove.self"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.xp.usage"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String amountMustBeNumber = chat.papi(player, playerControl.getString("player_control_module.xp.amount_must_be_number"));

        String setByOther = playerControl.getString("player_control_module.xp.set.by_other");
        String setToOther = playerControl.getString("player_control_module.xp.set.to_other");
        String addByOther = playerControl.getString("player_control_module.xp.add.by_other");
        String addToOther = playerControl.getString("player_control_module.xp.add.to_other");
        String removeByOther = playerControl.getString("player_control_module.xp.remove.by_other");
        String removeToOther = playerControl.getString("player_control_module.xp.remove.to_other");

        if(!sender.hasPermission("elmental.xp")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch(args.length) {
            case 2 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }

                int amount = Integer.parseInt(args[1]);

                switch(args[0]) {
                    case "set" -> {
                        player.setExperienceLevelAndProgress(amount);

                        player.sendMessage(mm.deserialize(setXPSelf, Placeholder.unparsed("amount", String.valueOf(amount))));
                    
                        return true;
                    }

                    case "add" -> {
                        player.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() + amount);

                        player.sendMessage(mm.deserialize(addXPSelf, Placeholder.unparsed("amount", String.valueOf(amount))));
                    
                        return true;
                    }

                    case "remove" -> {
                        player.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() - amount);

                        player.sendMessage(mm.deserialize(removeXPSelf, Placeholder.unparsed("amount", String.valueOf(amount))));
                    
                        return true;
                    }

                    default -> {
                        player.sendMessage(mm.deserialize(usage));
                    
                        return true;
                    }
                }
            }

            case 3 -> {
                Player targetPlayer = plugin.getServer().getPlayer(args[2]);
                int amount = 0;

                try {
                    amount = Integer.parseInt(args[1]);
                } catch(NumberFormatException e) {
                    sender.sendMessage(mm.deserialize(amountMustBeNumber));

                    return true;
                }

                if(!sender.hasPermission("elemental.xp.others")) {
                    sender.sendMessage(mm.deserialize(noPerms));
                    
                    return true;
                }

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[2])));

                    return true;
                }

                switch(args[0]) {
                    case "set" -> {
                        targetPlayer.setExperienceLevelAndProgress(amount);

                        setByOther = chat.papi(targetPlayer, setByOther);
                        setToOther = chat.papi(targetPlayer, setToOther);

                        targetPlayer.sendMessage(mm.deserialize(setByOther, Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("command_sender", sender.getName())));
                        sender.sendMessage(mm.deserialize(setToOther, Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                        return true;
                    }

                    case "add" -> {
                        targetPlayer.setExperienceLevelAndProgress(targetPlayer.calculateTotalExperiencePoints() + amount);

                        addByOther = chat.papi(targetPlayer, addByOther);
                        addToOther = chat.papi(targetPlayer, addToOther);

                        targetPlayer.sendMessage(mm.deserialize(addByOther, Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("command_sender", sender.getName())));
                        sender.sendMessage(mm.deserialize(addToOther, Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                        return true;
                    }

                    case "remove" -> {
                        targetPlayer.setExperienceLevelAndProgress(targetPlayer.calculateTotalExperiencePoints() + amount);

                        removeByOther = chat.papi(targetPlayer, removeByOther);
                        removeToOther = chat.papi(targetPlayer, removeToOther);

                        targetPlayer.sendMessage(mm.deserialize(removeByOther, Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("command_sender", sender.getName())));
                        sender.sendMessage(mm.deserialize(removeToOther, Placeholder.unparsed("amount", String.valueOf(amount)), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                        return true;
                    }

                    default -> {
                        targetPlayer.sendMessage(mm.deserialize(usage));
                    
                        return true;
                    }
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(usage));

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
