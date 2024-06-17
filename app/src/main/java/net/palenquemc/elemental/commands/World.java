package net.palenquemc.elemental.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.palenquemc.elemental.Elemental;

public class World implements TabExecutor {
    private Elemental plugin;
    
    public World(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.config.getConfig("messages.yml");

        if(!sender.hasPermission("elmental.teleport")) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.insufficient_permissions")));
            
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(mm.deserialize(messages.getString("messages.world.usage")));
            
            return true;
        }

        if(args.length == 1) {

        } else if(args.length == 2) {

        } else if(args.length > 2) {

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        return arguments;
    }
}
