package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class Getpos implements TabExecutor {
    private final Elemental plugin;
    
    public Getpos(Elemental plugin) {
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
        String locNotFoundSelf = chat.papi(player, playerControl.getString("player_control_module.getpos.location_not_found.self"));
        String locSelf = chat.papi(player, playerControl.getString("player_control_module.getpos.location.self"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.getpos.usage"));

        String targetBypass = playerControl.getString("player_control_module.getpos.target_player_bypass");
        String locNotFoundOther = playerControl.getString("player_control_module.getpos.location_not_found.other");
        String getLocOther = playerControl.getString("player_control_module.getpos.location.other");

        if(!sender.hasPermission("elemental.getpos")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch(args.length) {
            case 0 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }

                Location loc = player.getLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(locNotFoundSelf));

                    return true;
                }

                String coordinates = String.format("%.2f", loc.getX()) + ", " + String.format("%.2f", loc.getY()) + ", " + String.format("%.2f", loc.getZ());
                String world = loc.getWorld().getName();

                sender.sendMessage(mm.deserialize(locSelf, Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world)));
            
                return true;
            }

            case 1 -> {
                if(!sender.hasPermission("elemental.getpos.others")) {
                    sender.sendMessage(mm.deserialize(noPerms));
                    
                    return true;
                }

                Player targetPlayer = plugin.getServer().getPlayer(args[0]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));
                    
                    return true;
                }

                if(targetPlayer.hasPermission("elemental.getpos.bypass") && !sender.hasPermission("elemental.getpos.ignorebypass")) {
                    targetBypass = chat.papi(targetPlayer, targetBypass);
                    
                    sender.sendMessage(mm.deserialize(targetBypass, Placeholder.unparsed("target_player", targetPlayer.getName())));
                    
                    return true;
                }

                Location loc = targetPlayer.getLocation();

                if(loc == null) {
                    locNotFoundOther = chat.papi(targetPlayer, locNotFoundOther);

                    sender.sendMessage(mm.deserialize(locNotFoundOther, Placeholder.unparsed("target_player", targetPlayer.getName())));

                    return true;
                }

                String coordinates = String.format("%.2f", loc.getX()) + ", " + String.format("%.2f", loc.getY()) + ", " + String.format("%.2f", loc.getZ());
                String world = loc.getWorld().getName();

                getLocOther = chat.papi(targetPlayer, getLocOther);

                sender.sendMessage(mm.deserialize(getLocOther, Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world), Placeholder.unparsed("target_player", targetPlayer.getName())));
            
                return true;
            }

            default -> {
                sender.sendMessage(mm.deserialize(usage));

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }
        
        return arguments;
    }
}
