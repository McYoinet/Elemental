package net.palenquemc.elemental.modules.core.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.modules.core.commands.SubcommandTemplate;
import net.palenquemc.elemental.utils.ChatUtils;

public class Subhelp implements SubcommandTemplate {
    private final Elemental plugin;

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

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String help = chat.papi(player, core.getString("core_module.plugin_help"));

        if(!sender.hasPermission(permission())) {
            sender.sendMessage(mm.deserialize(noPerms, Placeholder.unparsed("version", plugin.version)));
        
            return false;
        }

        sender.sendMessage(mm.deserialize(help));
        
        return true;
    }
    
}
