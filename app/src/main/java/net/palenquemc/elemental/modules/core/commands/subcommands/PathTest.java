package net.palenquemc.elemental.modules.core.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.modules.core.commands.SubcommandTemplate;

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
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission(permission())) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
        
            return false;
        }

        if(args.length != 3) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.path_test.usage")));
        
            return false;
        }

        String file = args[1];
        String path = args[2];

        if(!plugin.config.getConfigHashMap().containsKey(file)) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.path_test.file_not_found"), Placeholder.unparsed("file", file)));
            
            return false;
        }

        if(!messages.getKeys(true).contains(path)) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.path_test.path_not_found"), Placeholder.unparsed("path", path), Placeholder.unparsed("file", file)));
            
            return false;
        }

        FileConfiguration chosenConfig = plugin.config.getConfig(file);
        Component value = mm.deserialize(chosenConfig.getString(path));
        
        sender.sendMessage(mm.deserialize(messages.getString("messages.path_test.path_found"), Placeholder.unparsed("path", path), Placeholder.unparsed("file", file), Placeholder.component("path_value", value)));

        return true;
    }
    
}
