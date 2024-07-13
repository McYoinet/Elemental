package net.palenquemc.elemental.modules.teleport.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class RequestTeleport implements TabExecutor {

    private Elemental plugin;
    
    public RequestTeleport(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HashMap<String, String> tpRequests = plugin.tpRequests;

        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration teleport = plugin.config.getConfig("teleport.yml");

        if(!sender.hasPermission("elemental.teleport.request")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

            return true;
        }

        Player player = (Player) sender;

        if(args.length != 2) {
            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.usage")));

            return true;
        }

        if(args[1] == player.getName()) {
            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.cannot_request_to_self")));

            return true;
        }

        Player targetPlayer = plugin.getServer().getPlayer(args[1]);

        if(targetPlayer == null) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found"), Placeholder.unparsed("target_player", args[1])));
        
            return true;
        }

        if(args[0].equalsIgnoreCase("send")) {
            if(tpRequests.containsKey(player.getName())) {
                Player oldPlayer = plugin.getServer().getPlayer(tpRequests.get(player.getName()));

                player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.cancelled.sender"), Placeholder.unparsed("target_destination", oldPlayer.getName())));
                oldPlayer.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.cancelled.recipient"), Placeholder.unparsed("command_sender", player.getName())));
            }

            tpRequests.put(player.getName(), targetPlayer.getName());

            player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.sent"), Placeholder.unparsed("target_destination", targetPlayer.getName())));
            targetPlayer.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.received"), Placeholder.unparsed("command_sender", player.getName())));
            
            return true;
        } else if(args[0].equalsIgnoreCase("accept")) {
            if(tpRequests.containsKey(targetPlayer.getName()) && tpRequests.get(targetPlayer.getName()) == player.getName()) {
                targetPlayer.teleport(player);

                tpRequests.remove(targetPlayer.getName());
                
                targetPlayer.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.accepted.sender"), Placeholder.unparsed("target_destination", player.getName())));
                player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.accepted.recipient"), Placeholder.unparsed("command_sender", targetPlayer.getName())));

                return true;
            } else {
                player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.not_found.recipient"), Placeholder.unparsed("command_sender", targetPlayer.getName())));

                return true;
            }
        } else if(args[0].equalsIgnoreCase("deny")) {
            if(tpRequests.containsKey(targetPlayer.getName()) && tpRequests.get(targetPlayer.getName()) == player.getName()) {
                tpRequests.remove(targetPlayer.getName());

                player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.denied.sender"), Placeholder.unparsed("target_destination", targetPlayer.getName())));
                targetPlayer.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.denied.recipient"), Placeholder.unparsed("command_sender", player.getName())));

                return true;
            } else {
                player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.not_found.recipient"), Placeholder.unparsed("command_sender", targetPlayer.getName())));
            
                return true;
            }
        } else if(args[0].equalsIgnoreCase("cancel")) {
            if(!tpRequests.containsKey(player.getName())) {
                player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.not_found.sender"), Placeholder.unparsed("target_destination", targetPlayer.getName())));

                return true;
            }

            tpRequests.remove(player.getName());

            player.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.cancelled.sender"), Placeholder.unparsed("target_destination", targetPlayer.getName())));
            targetPlayer.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.request.cancelled.recipient"), Placeholder.unparsed("command_sender", player.getName())));

            return true;
        } else {
            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.request_teleport.usage")));

            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("send");
            arguments.add("accept");
            arguments.add("deny");
            arguments.add("cancel");
        }

        if(args.length == 2) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }

        return arguments;
    }
}
