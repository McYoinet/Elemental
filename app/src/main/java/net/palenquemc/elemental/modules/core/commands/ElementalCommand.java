package net.palenquemc.elemental.modules.core.commands;

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
import net.palenquemc.elemental.modules.core.commands.subcommands.PathTest;
import net.palenquemc.elemental.modules.core.commands.subcommands.Reload;
import net.palenquemc.elemental.modules.core.commands.subcommands.Subhelp;

public class ElementalCommand implements TabExecutor {
    private final Elemental plugin;

    public ElementalCommand(Elemental plugin) {
        this.plugin = plugin;

        addSubcommands();
    }

    MiniMessage mm = MiniMessage.miniMessage();

    HashMap<String, SubcommandTemplate> subcommands = new HashMap<>();

    private void addSubcommands() {
        subcommands.put("help", new Subhelp(plugin));
        subcommands.put("pathtest", new PathTest(plugin));
        subcommands.put("reload", new Reload(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");

        if(args.length == 0) {
            if(!sender.hasPermission("elemental.plugininfo")) {
                sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                
                return false;
            }

            sender.sendMessage(mm.deserialize(core.getString("core_module.plugin_info"), Placeholder.unparsed("version", plugin.version)));
            
            return true;
        } else if(args.length >= 1 && subcommands.containsKey(args[0])){
            boolean result = subcommands.get(args[0]).execute(sender, command, args);
            
            return result;
        } else {
            sender.sendMessage(mm.deserialize(core.getString("core_module.unknown_subcommand")));
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
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
