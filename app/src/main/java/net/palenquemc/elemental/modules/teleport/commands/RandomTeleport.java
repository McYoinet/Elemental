package net.palenquemc.elemental.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class RandomTeleport implements TabExecutor {

    private final Elemental plugin;
    
    public RandomTeleport(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration teleport = plugin.config.getConfig("teleport.yml");

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String notEnoughPlayers = chat.papi(player, teleport.getString("teleport_module.random_teleport.not_enough_players"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String teleportedSelf = chat.papi(player, teleport.getString("teleport_module.random_teleport.teleported.self"));
        String teleportedAll = chat.papi(player, teleport.getString("teleport_module.random_teleport.teleported.all"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, teleport.getString("teleport_module.random_teleport.usage"));

        String teleportedByOther = teleport.getString("teleport_module.random_teleport.teleported.by_other");
        String teleportedToOther = teleport.getString("teleport_module.random_teleport.teleported.to_other");

        if(!sender.hasPermission("elemental.teleport.random")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        List<UUID> players = new ArrayList<>();
        
        if(plugin.getServer().getOnlinePlayers().size() < 3) {
            sender.sendMessage(mm.deserialize(notEnoughPlayers));
        
            return true;
        }

        plugin.getServer().getOnlinePlayers().forEach(p -> {
            players.add(p.getUniqueId());
        });

        Random random = new Random();

        Player destinationPlayer = plugin.getServer().getPlayer(players.get(random.nextInt(players.size())));

        if(args.length == 0) {
            if(player == null) {
                sender.sendMessage(mm.deserialize(executableFromPlayer));

                return true;
            }

            player.teleport(destinationPlayer);

            sender.sendMessage(mm.deserialize(teleportedSelf, Placeholder.unparsed("target_destination", destinationPlayer.getName())));
        
            return true;
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("@a")) {
                plugin.getServer().getOnlinePlayers().forEach(p -> {
                    p.teleport(destinationPlayer);

                    if(p.getName() != sender.getName()) {
                        String messageTeleportedByOther = chat.papi(p, teleport.getString("teleport_module.random_teleport.teleported.by_other"));
                        p.sendMessage(mm.deserialize(messageTeleportedByOther, Placeholder.unparsed("target_destination", destinationPlayer.getName()), Placeholder.unparsed("command_sender", sender.getName())));
                    }
                });

                sender.sendMessage(mm.deserialize(teleportedAll, Placeholder.unparsed("target_destination", destinationPlayer.getName())));

                return true;
            }

            Player targetPlayer = plugin.getServer().getPlayer(args[0]);

            if(targetPlayer == null) {
                sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));
                
                return true;
            }

            targetPlayer.teleport(destinationPlayer);

            teleportedByOther = chat.papi(targetPlayer, teleportedByOther);
            teleportedToOther = chat.papi(targetPlayer, teleportedToOther);

            targetPlayer.sendMessage(mm.deserialize(teleportedByOther, Placeholder.unparsed("target_destination", destinationPlayer.getName()), Placeholder.unparsed("command_sender", sender.getName())));
            sender.sendMessage(mm.deserialize(teleportedToOther, Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("target_destination", destinationPlayer.getName())));

            return true;
        } else if(args.length > 1) {
            sender.sendMessage(mm.deserialize(usage));
            
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("[player]");
        }
        
        return arguments;
    }    
}
