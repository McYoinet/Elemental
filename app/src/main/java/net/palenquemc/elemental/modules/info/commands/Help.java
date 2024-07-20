package net.palenquemc.elemental.modules.info.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class Help implements TabExecutor {
    private final Elemental plugin;

    public Help(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration info = plugin.config.getConfig("info.yml");

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String helpMessage = chat.papi(player, info.getString("info_module.help_command.message"));
        String usage = chat.papi(player, info.getString("info_module.help_command.usage"));
        String unknownPage = chat.papi(player, info.getString("info_module.help_command.unknown_page"));

        ConfigurationSection subcommands = info.getConfigurationSection("info_module.help_command.subcommands");
        HashMap<String, String> subcommandsMap = new HashMap<>();

        subcommands.getKeys(false).forEach(commandName -> {
            subcommandsMap.put(commandName, info.getString("info_module.help_command.subcommands." + commandName));
        });

        switch(args.length) {
            case 0 -> {
                sender.sendMessage(mm.deserialize(helpMessage));

                return true;
            }

            case 1 -> {
                if(subcommandsMap.containsKey(args[0])) {
                    sender.sendMessage(mm.deserialize(chat.papi(player, subcommandsMap.get(args[0]))));
                } else sender.sendMessage(mm.deserialize(unknownPage));

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
        
        FileConfiguration info = plugin.config.getConfig("info.yml");

        ConfigurationSection subcommands = info.getConfigurationSection("help_module.help_command.subcommands");

        subcommands.getKeys(false).forEach(subcommand -> {
            arguments.add(subcommand);
        });

        return arguments;
    }    
}
