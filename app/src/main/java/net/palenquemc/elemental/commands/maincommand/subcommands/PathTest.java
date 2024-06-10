package net.palenquemc.elemental.commands.maincommand.subcommands;

import java.util.ArrayList;
import java.util.List;

import net.palenquemc.elemental.commands.SubcommandTemplate;

public class PathTest implements SubcommandTemplate {
    @Override
    public String permission() {
        return "elemental.pathtest";
    }
    
    @Override
    public List<String> arguments(String[] fullargs) {
        List<String> args = new ArrayList<>();

        if(fullargs.length == 2) {
            args.add("path");
        }

        return args;
    }

    @Override
    public boolean execute() {
        return false;
    }
    
}
