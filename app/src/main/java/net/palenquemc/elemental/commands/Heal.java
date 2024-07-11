package net.palenquemc.elemental.commands;

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

public class Heal implements TabExecutor {
    private Elemental plugin;
    
    public Heal(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission("elmental.heal")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;

                player.setHealth(20);

                player.sendMessage(mm.deserialize(messages.getString("messages.heal.self")));

                return true;
            }

            case 1 -> {
                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.target_not_found")));

                    return true;
                }

                if(!sender.hasPermission("elemental.heal.others")) {
                    sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
                    
                    return true;
                }

                targetPlayer.setHealth(20);

                targetPlayer.sendMessage(mm.deserialize(messages.getString("messages.heal.to_other"), Placeholder.unparsed("target_player", targetPlayer.getName())));
                sender.sendMessage(mm.deserialize(messages.getString("messages.heal.by_other"), Placeholder.unparsed("command_sender", sender.getName())));
            
                return true;
            }

            default -> {
                sender.sendMessage(mm.deserialize(messages.getString("messages.heal.usage")));

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }

        return arguments;
    }
}
