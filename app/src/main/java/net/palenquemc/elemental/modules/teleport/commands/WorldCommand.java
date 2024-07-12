package net.palenquemc.elemental.modules.teleport.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class WorldCommand implements TabExecutor {
    private Elemental plugin;
    
    public WorldCommand(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration teleport = plugin.config.getConfig("teleport.yml");

        if(!sender.hasPermission("elemental.teleport")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.world.usage")));
            
            return true;
        }

        if(args.length >= 1) {
            String worldname = args[0];
            World world = plugin.getServer().getWorld(worldname);

            if(plugin.getServer().getWorld(worldname) == null) {
                sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.world.world_not_found"), Placeholder.unparsed("world", worldname)));

                return true;
            }
            
            if(args.length == 1) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));
    
                    return true;
                }
    
                Player player = (Player) sender;
    
                player.teleport(world.getSpawnLocation());
                sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.world.world_changed.self"), Placeholder.unparsed("world", worldname)));
            } else if(args.length == 2) {
                if(!sender.hasPermission("elemental.teleport.others")) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
    
                    return true;
                }

                Player targetPlayer = plugin.getServer().getPlayer(args[1]);

                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.target_not_found"), Placeholder.unparsed("target_player", args[1])));
                    
                    return true;
                }

                targetPlayer.teleport(world.getSpawnLocation());
                
                sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.world.world_changed.to_other"), Placeholder.unparsed("target_player", args[1]), Placeholder.unparsed("world", args[0])));
                targetPlayer.sendMessage(mm.deserialize(teleport.getString("teleport_module.world.world_changed.by_other"), Placeholder.unparsed("world", worldname), Placeholder.unparsed("command_sender", sender.getName())));

                return true;
            } else if(args.length > 2) {
                sender.sendMessage(mm.deserialize(teleport.getString("teleport_module.world.usage")));

                return true;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("<world>");

            plugin.getServer().getWorlds().forEach(world -> {
                arguments.add(world.getName());
            });
        } else if(args.length == 2) {
            arguments.add("[player]");
            
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }

        return arguments;
    }
}
