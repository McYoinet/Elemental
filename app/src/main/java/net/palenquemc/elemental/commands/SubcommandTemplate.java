package net.palenquemc.elemental.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubcommandTemplate {
    public String permission();
    
    public List<String> arguments(String[] fullargs);

    public boolean execute(CommandSender sender, Command command, String[] args);
}
