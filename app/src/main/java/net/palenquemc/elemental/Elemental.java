package net.palenquemc.elemental;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.commands.maincommand.ElementalCommand;
import net.palenquemc.elemental.config.ConfigurationManager;

public class Elemental extends JavaPlugin {
    MiniMessage mm = MiniMessage.miniMessage();

    PluginMeta meta = getPluginMeta();

    public String name = meta.getName();
    public String version = meta.getVersion();

    public String author = meta.getAuthors().get(0);

    public String internalPrefix = "<dark_grey>[<#00e580>Elemental<dark_grey>]<reset>";

    public ConfigurationManager config = new ConfigurationManager(this);

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Plugin enabled on <light_purple>v<version><white>.", Placeholder.parsed("prefix", internalPrefix), Placeholder.unparsed("version", version)));
        Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Developed by <aqua><author><white>.", Placeholder.parsed("prefix", internalPrefix), Placeholder.unparsed("author", author)));
        
        registerCommands();
        
        config.loadConfigurations();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Plugin disabled on <light_purple>v<version><white>.", Placeholder.parsed("prefix", internalPrefix), Placeholder.unparsed("version", version)));
        Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Developed by <aqua><author><white>.", Placeholder.parsed("prefix", internalPrefix), Placeholder.unparsed("author", author)));
    }

    private void registerCommands() {
        getCommand("elemental").setExecutor(new ElementalCommand(this));
    }
}
