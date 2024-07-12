package net.palenquemc.elemental.modules.core.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.modules.core.commands.SubcommandTemplate;

public class Subhelp implements SubcommandTemplate {
    private Elemental plugin;

    MiniMessage mm = MiniMessage.miniMessage();

    public Subhelp (Elemental plugin) {
        this.plugin = plugin;
    }

    @Override
    public String permission() {
        return "elemental.pluginhelp";
    }
    
    @Override
    public List<String> arguments(String[] fullargs) {
        return new ArrayList<>();
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");

        if(!sender.hasPermission(permission())) {
            sender.sendMessage(mm.deserialize(core.getString("core.insufficient_permissions")));
        
            return false;
        }

        sender.sendMessage(mm.deserialize(core.getString("core.plugin_help")));
        
        return true;
    }
    
}
