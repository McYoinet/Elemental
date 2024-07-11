package net.palenquemc.elemental;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.commands.Back;
import net.palenquemc.elemental.commands.Broadcast;
import net.palenquemc.elemental.commands.Gamemode;
import net.palenquemc.elemental.commands.Getpos;
import net.palenquemc.elemental.commands.LastDeath;
import net.palenquemc.elemental.commands.RandomTeleport;
import net.palenquemc.elemental.commands.RequestTeleport;
import net.palenquemc.elemental.commands.SetSpawn;
import net.palenquemc.elemental.commands.Spawn;
import net.palenquemc.elemental.commands.Teleport;
import net.palenquemc.elemental.commands.WorldCommand;
import net.palenquemc.elemental.commands.maincommand.ElementalCommand;
import net.palenquemc.elemental.config.ConfigurationManager;
import net.palenquemc.elemental.events.PlayerJoinListener;
import net.palenquemc.elemental.events.PlayerSpawnLocationListener;

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
        
        registerEvents();
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
        getCommand("broadcast").setExecutor(new Broadcast(this));
        getCommand("gamemode").setExecutor(new Gamemode(this));
        getCommand("teleport").setExecutor(new Teleport(this));
        getCommand("world").setExecutor(new WorldCommand(this));
        getCommand("rtp").setExecutor(new RandomTeleport(this));
        getCommand("teleportrequest").setExecutor(new RequestTeleport(this));
        getCommand("back").setExecutor(new Back(this));
        getCommand("lastdeath").setExecutor(new LastDeath(this));
        getCommand("getpos").setExecutor(new Getpos(this));
        getCommand("setspawn").setExecutor(new SetSpawn(this));
        getCommand("spawn").setExecutor(new Spawn(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerSpawnLocationListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }
}
