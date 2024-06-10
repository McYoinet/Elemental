package net.palenquemc.elemental.commands;

import java.util.List;

public interface SubcommandTemplate {
    public String permission();
    
    public List<String> arguments(String[] fullargs);

    public boolean execute();
}
