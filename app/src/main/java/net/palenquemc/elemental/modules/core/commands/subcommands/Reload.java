package net.palenquemc.elemental.modules.core.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.modules.core.commands.SubcommandTemplate;

public class Reload implements SubcommandTemplate {
    private final Elemental plugin;
    
    final MiniMessage mm = MiniMessage.miniMessage();

    public Reload(Elemental plugin) {
        this.plugin = plugin;
    }

    @Override
    public String permission() {
        return "elemental.reload";
    }
    
    @Override
    public List<String> arguments(String[] fullargs) {
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");

        if(!sender.hasPermission(permission())) {
            sender.sendMessage(mm.deserialize(core.getString("core.insufficient_permissions")));

            return false;
        }

        boolean result = plugin.config.reloadConfig();
        sender.sendMessage(mm.deserialize(core.getString("core.plugin_reloaded")));

        return result;
    }
    
}
