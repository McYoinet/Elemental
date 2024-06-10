package net.palenquemc.elemental.commands.maincommand.subcommands;

import java.util.List;

import net.palenquemc.elemental.commands.SubcommandTemplate;

public class Reload implements SubcommandTemplate {
    @Override
    public String permission() {
        return "elemental.reload";
    }
    
    @Override
    public List<String> arguments(String[] fullargs) {
        return null;
    }

    @Override
    public boolean execute() {
        return false;
    }
    
}
