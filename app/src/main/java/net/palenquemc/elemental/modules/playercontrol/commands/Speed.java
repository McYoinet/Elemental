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

public class Speed implements TabExecutor {
        private Elemental plugin;
    
    public Speed(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        if(!sender.hasPermission("elmental.fly")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        switch(args.length) {
            case 1 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;
                float speed = (float) Double.parseDouble(args[0]);

                if(speed < 1 || speed > 10) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.invalid_value")));

                    return true;
                }

                float processedSpeed = speed / 10;

                if(player.isFlying()) {
                    player.setFlySpeed(processedSpeed);

                    player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.set.flying.self"), Placeholder.unparsed("amount", String.valueOf(speed))));

                    return true;
                } else {
                    player.setWalkSpeed(processedSpeed);

                    player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.set.walking.self"), Placeholder.unparsed("amount", String.valueOf(speed))));
                
                    return true;
                }
            }

            case 2 -> {
                float speed = (float) Double.parseDouble(args[0]);
                Player targetPlayer = plugin.getServer().getPlayer(args[1]);

                if(!sender.hasPermission("elemental.speed.others")) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                    
                    return true;
                }
                
                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found")));

                    return true;
                }

                if(speed < 1 || speed > 10) {
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.invalid_value")));

                    return true;
                }

                float processedSpeed = speed / 10;

                if(targetPlayer.isFlying()) {
                    targetPlayer.setFlySpeed(processedSpeed);

                    targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.set.flying.by_other"), Placeholder.unparsed("command_sender", sender.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.set.flying.to_other"), Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));

                    return true;
                } else {
                    targetPlayer.setWalkSpeed(processedSpeed);

                    targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.set.walking.by_other"), Placeholder.unparsed("command_sender", sender.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.set.walking.to_other"), Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));

                    return true;
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.speed.usage")));

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("<amount>");
        } else if(args.length == 2) {
            arguments.add("[player]");

            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }

        return arguments;
    }
}
