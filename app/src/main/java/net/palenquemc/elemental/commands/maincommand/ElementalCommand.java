package net.palenquemc.elemental.commands.maincommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.commands.SubcommandTemplate;
import net.palenquemc.elemental.commands.maincommand.subcommands.Help;
import net.palenquemc.elemental.commands.maincommand.subcommands.PathTest;

public class ElementalCommand implements TabExecutor {
    private final Elemental plugin;
    
    public ElementalCommand(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    HashMap<String, SubcommandTemplate> subcommands = new HashMap<>();

    private void addSubcommands() {
        subcommands.put("help", new Help());
        subcommands.put("pathtest", new PathTest());
        subcommands.put("reload", new PathTest());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        addSubcommands();

        FileConfiguration messages = plugin.config.getConfig("messages.yml");
            
        // if(!sender.hasPermission("elemental.plugininfo")) {
        //     sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
        //     return true;
        // }

        if(args.length == 0) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.plugin_info"), Placeholder.unparsed("version", plugin.version)));
        } else if(args.length == 1 && subcommands.containsKey(args[0])){
            subcommands.get(args[0]).execute();
        } else {
            sender.sendMessage(mm.deserialize(messages.getString("messages.unknown_subcommand")));
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        addSubcommands();

        List<String> arguments = new ArrayList<>();

        subcommands.forEach((name, subcommand) -> {
            arguments.add(name);
        });

        subcommands.forEach((name, subcommand) -> {
            if(!sender.hasPermission(subcommand.permission())) {
                arguments.remove(arguments.indexOf(name));
            }
        });

        if(args.length == 1) {
            return arguments;
        } else if(args.length > 1){
            if(subcommands.containsKey(args[0])) {
                List<String> subargs = subcommands.get(args[0]).arguments(args);
                
                return subargs;
            }
        }
        
        return null;
    }
    
}
