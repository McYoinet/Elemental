package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class LastDeath implements TabExecutor {
    private final Elemental plugin;
    
    public LastDeath(Elemental plugin) {
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
        String noDeathsFoundSelf = chat.papi(player, playerControl.getString("player_control_module.lastdeath.no_deaths_found.self"));
        String worldNotFoundSelf = chat.papi(player, playerControl.getString("player_control_module.lastdeath.world_not_found.self"));
        String coordsSelf = chat.papi(player, playerControl.getString("player_control_module.lastdeath.coordinates.self"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.lastdeath.usage"));

        String noDeathsFoundOther = playerControl.getString("player_control_module.lastdeath.no_deaths_found.other");
        String noWorldFoundOther = playerControl.getString("player_control_module.lastdeath.world_not_found.other");
        String coordsOther = playerControl.getString("player_control_module.lastdeath.coordinates.other");

        if(!sender.hasPermission("elemental.lastdeath")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch (args.length) {
            case 0 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }

                Location loc = player.getLastDeathLocation();

                if(loc == null) {
                    sender.sendMessage(mm.deserialize(noDeathsFoundSelf));

                    return true;
                }

                if(loc.getWorld() == null) {
                    sender.sendMessage(mm.deserialize(worldNotFoundSelf));

                    return true;
                }

                String world = loc.getWorld().getName();
                String coordinates = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " +  Double.toString(loc.getZ());

                player.sendMessage(mm.deserialize(coordsSelf, Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world)));
            
                return true;
            }

            case 1 -> {
                if(!sender.hasPermission("elemental.lastdeath.others")) {
                    sender.sendMessage(mm.deserialize(noPerms));

                    return true;
                }

                OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(args[0]);
                
                if(targetPlayer.getLastSeen() == 0) {
                    sender.sendMessage(mm.deserialize(targetNotFound, Placeholder.unparsed("target_player", args[0])));
                    
                    return true;
                }
                
                Location loc = targetPlayer.getLastDeathLocation();

                if(loc == null) {
                    noDeathsFoundOther = chat.papi(targetPlayer.getPlayer(), noDeathsFoundOther);

                    sender.sendMessage(mm.deserialize(noDeathsFoundOther, Placeholder.unparsed("target_player", args[0])));

                    return true;
                }

                if(loc.getWorld() == null) {
                    noWorldFoundOther = chat.papi(targetPlayer.getPlayer(), noWorldFoundOther);

                    sender.sendMessage(mm.deserialize(noWorldFoundOther, Placeholder.unparsed("target_player", args[0])));

                    return true;
                }

                String world = loc.getWorld().getName();
                String coordinates = Double.toString(loc.getX()) + ", " + Double.toString(loc.getY()) + ", " +  Double.toString(loc.getZ());

                coordsOther = chat.papi(targetPlayer.getPlayer(), coordsOther);

                sender.sendMessage(mm.deserialize(coordsOther, Placeholder.unparsed("coordinates", coordinates), Placeholder.unparsed("world", world), Placeholder.unparsed("target_player", targetPlayer.getName())));
            
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
