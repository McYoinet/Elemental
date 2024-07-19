package net.palenquemc.elemental.modules.core.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.modules.core.commands.SubcommandTemplate;
import net.palenquemc.elemental.utils.ChatUtils;

public class PathTest implements SubcommandTemplate {
    private final Elemental plugin;

    final MiniMessage mm = MiniMessage.miniMessage();

    public PathTest(Elemental plugin) {
        this.plugin = plugin;
    }

    @Override
    public String permission() {
        return "elemental.pathtest";
    }
    
    @Override
    public List<String> arguments(String[] fullargs) {
        List<String> args = new ArrayList<>();

        if(fullargs.length == 2) {
            args.add("<file>");

            plugin.config.getConfigHashMap().keySet().forEach(file -> {
                args.add(file);
            });
            
        } else if(fullargs.length == 3){
            args.add("<path>");

            if(plugin.config.getConfigHashMap().keySet().contains(fullargs[1])) {
                plugin.config.getConfig(fullargs[1]).getKeys(true).forEach(key -> {
                    args.add(key);
                });
            }
        }

        return args;
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        ChatUtils chat = new ChatUtils();

        FileConfiguration core = plugin.config.getConfig("core.yml");

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String usage = chat.papi(player, core.getString("core_module.path_test.usage"));
        String fileNotFound = chat.papi(player, core.getString("core_module.path_test.file_not_found"));
        String pathNotFound = chat.papi(player, core.getString("core_module.path_test.path_not_found"));
        String pathFound = chat.papi(player, core.getString("core_module.path_test.path_found"));

        if(!sender.hasPermission(permission())) {
            sender.sendMessage(mm.deserialize(noPerms));
        
            return false;
        }

        if(args.length != 3) {
            sender.sendMessage(mm.deserialize(usage));
        
            return false;
        }

        String file = args[1];
        String path = args[2];

        if(!plugin.config.getConfigHashMap().containsKey(file)) {
            sender.sendMessage(mm.deserialize(fileNotFound, Placeholder.unparsed("file", file)));
            
            return false;
        }

        if(!core.getKeys(true).contains(path)) {
            sender.sendMessage(mm.deserialize(pathNotFound, Placeholder.unparsed("path", path), Placeholder.unparsed("file", file)));
            
            return false;
        }

        FileConfiguration chosenConfig = plugin.config.getConfig(file);
        Component value = mm.deserialize(chosenConfig.getString(path));
        
        sender.sendMessage(mm.deserialize(pathFound, Placeholder.unparsed("path", path), Placeholder.unparsed("file", file), Placeholder.component("path_value", value)));

        return true;
    }
    
}
