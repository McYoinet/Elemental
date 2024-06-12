package net.palenquemc.elemental.commands.maincommand.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.palenquemc.elemental.commands.SubcommandTemplate;

public class Help implements SubcommandTemplate {
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
        return false;
    }
    
}
