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
import net.palenquemc.elemental.utils.ChatUtils;

public class RequestTeleport implements TabExecutor {

    private final Elemental plugin;
    
    public RequestTeleport(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HashMap<String, String> tpRequests = plugin.tpRequests;

        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration teleport = plugin.config.getConfig("teleport.yml");

        ChatUtils chat = new ChatUtils();

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String usage = chat.papi(player, teleport.getString("teleport_module.request_teleport.usage"));
        String noRequestSelf = chat.papi(player, teleport.getString("teleport_module.request_teleport.cannot_request_to_self"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        
        String reqCancelledSender = teleport.getString("teleport_module.request_teleport.request.cancelled.sender");
        String reqCancelledRecipient = teleport.getString("teleport_module.request_teleport.request.cancelled.recipient");
        String reqSent = teleport.getString("teleport_module.request_teleport.request.sent");
        String reqReceived = teleport.getString("teleport_module.request_teleport.request.received");
        String reqAccepted = teleport.getString("teleport_module.request_teleport.request.accepted.sender");
        String reqAcceptedRecipient = teleport.getString("teleport_module.request_teleport.request.accepted.recipient");
        String reqNotFoundRecipient = teleport.getString("teleport_module.request_teleport.request.not_found.recipient");
        String deniedSender = teleport.getString("teleport_module.request_teleport.request.denied.sender");
        String deniedRecipient = teleport.getString("teleport_module.request_teleport.request.denied.recipient");
        String notFoundRecipient = teleport.getString("teleport_module.request_teleport.request.not_found.recipient");
        String notFoundSender = teleport.getString("teleport_module.request_teleport.request.not_found.sender");

        if(!sender.hasPermission("elemental.teleport.request")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        if(player == null) {
            sender.sendMessage(mm.deserialize(executableFromPlayer));

            return true;
        }

        if(args.length != 2) {
            sender.sendMessage(mm.deserialize(usage));

            return true;
        }

        if(args[1] == player.getName()) {
            sender.sendMessage(mm.deserialize(noRequestSelf));

            return true;
        }

        Player targetPlayer = plugin.getServer().getPlayer(args[1]);

        if(targetPlayer == null) {
            sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[1])));
        
            return true;
        }

        if(args[0].equalsIgnoreCase("send")) {
            if(tpRequests.containsKey(player.getName())) {
                Player oldPlayer = plugin.getServer().getPlayer(tpRequests.get(player.getName()));

                reqCancelledSender = chat.papi(oldPlayer, reqCancelledSender);
                reqCancelledRecipient = chat.papi(oldPlayer, reqCancelledRecipient);

                player.sendMessage(mm.deserialize(reqCancelledSender, Placeholder.unparsed("target_destination", oldPlayer.getName())));
                oldPlayer.sendMessage(mm.deserialize(reqCancelledRecipient, Placeholder.unparsed("command_sender", player.getName())));
            }

            tpRequests.put(player.getName(), targetPlayer.getName());

            reqSent = chat.papi(targetPlayer, reqSent);
            reqReceived = chat.papi(targetPlayer, reqReceived);

            player.sendMessage(mm.deserialize(reqSent, Placeholder.unparsed("target_destination", targetPlayer.getName())));
            targetPlayer.sendMessage(mm.deserialize(reqReceived, Placeholder.unparsed("command_sender", player.getName())));
            
            return true;
        } else if(args[0].equalsIgnoreCase("accept")) {
            if(tpRequests.containsKey(targetPlayer.getName()) && tpRequests.get(targetPlayer.getName()) == player.getName()) {
                targetPlayer.teleport(player);

                tpRequests.remove(targetPlayer.getName());

                reqAccepted = chat.papi(targetPlayer, reqAccepted);
                reqAcceptedRecipient = chat.papi(targetPlayer, reqAcceptedRecipient);
                
                targetPlayer.sendMessage(mm.deserialize(reqAccepted, Placeholder.unparsed("target_destination", player.getName())));
                player.sendMessage(mm.deserialize(reqAcceptedRecipient, Placeholder.unparsed("command_sender", targetPlayer.getName())));

                return true;
            } else {
                reqNotFoundRecipient = chat.papi(targetPlayer, reqNotFoundRecipient);

                player.sendMessage(mm.deserialize(reqNotFoundRecipient, Placeholder.unparsed("command_sender", targetPlayer.getName())));

                return true;
            }
        } else if(args[0].equalsIgnoreCase("deny")) {
            if(tpRequests.containsKey(targetPlayer.getName()) && tpRequests.get(targetPlayer.getName()) == player.getName()) {
                tpRequests.remove(targetPlayer.getName());

                deniedSender = chat.papi(targetPlayer, deniedSender);
                deniedRecipient = chat.papi(targetPlayer, deniedRecipient);

                player.sendMessage(mm.deserialize(deniedSender, Placeholder.unparsed("target_destination", targetPlayer.getName())));
                targetPlayer.sendMessage(mm.deserialize(deniedRecipient, Placeholder.unparsed("command_sender", player.getName())));

                return true;
            } else {
                notFoundRecipient = chat.papi(targetPlayer, notFoundRecipient);

                player.sendMessage(mm.deserialize(notFoundRecipient, Placeholder.unparsed("command_sender", targetPlayer.getName())));
            
                return true;
            }
        } else if(args[0].equalsIgnoreCase("cancel")) {
            if(!tpRequests.containsKey(player.getName())) {
                notFoundSender = chat.papi(targetPlayer, notFoundSender);

                player.sendMessage(mm.deserialize(notFoundSender, Placeholder.unparsed("target_destination", targetPlayer.getName())));

                return true;
            }

            tpRequests.remove(player.getName());

            reqCancelledRecipient = chat.papi(targetPlayer, reqCancelledRecipient);
            reqCancelledSender = chat.papi(targetPlayer, reqCancelledSender);

            player.sendMessage(mm.deserialize(reqCancelledSender, Placeholder.unparsed("target_destination", targetPlayer.getName())));
            targetPlayer.sendMessage(mm.deserialize(reqCancelledRecipient, Placeholder.unparsed("command_sender", player.getName())));

            return true;
        } else {
            sender.sendMessage(mm.deserialize(usage));

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
