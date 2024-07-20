package net.palenquemc.elemental.modules.core.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.modules.core.commands.SubcommandTemplate;
import net.palenquemc.elemental.utils.ChatUtils;

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

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String reloaded = chat.papi(player, core.getString("core_module.plugin_reloaded"));

        if(!sender.hasPermission(permission())) {
            sender.sendMessage(mm.deserialize(noPerms));

            return false;
        }

        boolean result = plugin.config.reloadConfig();
        sender.sendMessage(mm.deserialize(reloaded));

        return result;
    }
    
}
