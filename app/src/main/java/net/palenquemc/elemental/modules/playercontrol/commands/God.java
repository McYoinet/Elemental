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

public class God implements TabExecutor {

    private Elemental plugin;
    
    public God(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        if(!sender.hasPermission("elmental.god")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;

                if(player.isInvulnerable()) {
                    player.setInvulnerable(false);

                    player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.god.disable.self")));

                    return true;
                } else {
                    player.setInvulnerable(true);

                    player.sendMessage(mm.deserialize(playerControl.getString("player_control_module.god.enable.self")));
                
                    return true;
                }
            }

            case 1 -> {
                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found")));

                    return true;
                }

                if(!sender.hasPermission("elemental.god.others")) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                    
                    return true;
                }

                if(targetPlayer.isInvulnerable()) {
                    targetPlayer.setInvulnerable(false);

                    targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.god.disable.by_other"), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.god.disable.to_other"), Placeholder.unparsed("command_sender", sender.getName())));

                    return true;
                } else {
                    targetPlayer.setInvulnerable(true);

                    targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.god.enable.by_other"), Placeholder.unparsed("target_player", targetPlayer.getName())));
                    sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.god.enable.to_other"), Placeholder.unparsed("command_sender", sender.getName())));

                    return true;
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.god.usage")));

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
