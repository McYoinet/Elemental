package net.palenquemc.elemental.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.modules.core.commands.ElementalCommand;
import net.palenquemc.elemental.modules.playercontrol.commands.ClearInventory;
import net.palenquemc.elemental.modules.playercontrol.commands.Feed;
import net.palenquemc.elemental.modules.playercontrol.commands.Fly;
import net.palenquemc.elemental.modules.playercontrol.commands.Gamemode;
import net.palenquemc.elemental.modules.playercontrol.commands.Getpos;
import net.palenquemc.elemental.modules.playercontrol.commands.God;
import net.palenquemc.elemental.modules.playercontrol.commands.Heal;
import net.palenquemc.elemental.modules.playercontrol.commands.LastDeath;
import net.palenquemc.elemental.modules.playercontrol.commands.Nickname;
import net.palenquemc.elemental.modules.playercontrol.commands.PlayerInfo;
import net.palenquemc.elemental.modules.playercontrol.commands.Speed;
import net.palenquemc.elemental.modules.playercontrol.commands.XPCommand;
import net.palenquemc.elemental.modules.playercontrol.events.NicknameApplyJoin;
import net.palenquemc.elemental.modules.playercontrol.events.NicknameRemoveLeave;
import net.palenquemc.elemental.modules.servercontrol.commands.Broadcast;
import net.palenquemc.elemental.modules.servercontrol.commands.ListCommand;
import net.palenquemc.elemental.modules.spawn.commands.SetSpawn;
import net.palenquemc.elemental.modules.spawn.commands.Spawn;
import net.palenquemc.elemental.modules.spawn.events.PlayerJoinListener;
import net.palenquemc.elemental.modules.spawn.events.PlayerQuitListener;
import net.palenquemc.elemental.modules.spawn.events.PlayerSpawnLocationListener;
import net.palenquemc.elemental.modules.teleport.commands.Back;
import net.palenquemc.elemental.modules.teleport.commands.RandomTeleport;
import net.palenquemc.elemental.modules.teleport.commands.RequestTeleport;
import net.palenquemc.elemental.modules.teleport.commands.Teleport;
import net.palenquemc.elemental.modules.teleport.commands.WorldCommand;

public class ModuleManager {
    private final Elemental plugin;

    public ModuleManager(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    private void loadCoreModule() {
        plugin.getCommand("elemental").setExecutor(new ElementalCommand(plugin));
    
        Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Loaded <light_purple>core<white> module.", Placeholder.parsed("prefix", plugin.internalPrefix)));
    }

    private boolean loadPlayerControlModule() {
        FileConfiguration modules = plugin.config.getConfig("modules.yml");

        if(modules.getBoolean("modules.player_control")) {
            plugin.getCommand("gamemode").setExecutor(new Gamemode(plugin));
            plugin.getCommand("feed").setExecutor(new Feed(plugin));
            plugin.getCommand("heal").setExecutor(new Heal(plugin));
            plugin.getCommand("fly").setExecutor(new Fly(plugin));
            plugin.getCommand("clearinventory").setExecutor(new ClearInventory(plugin));
            plugin.getCommand("getpos").setExecutor(new Getpos(plugin));
            plugin.getCommand("lastdeath").setExecutor(new LastDeath(plugin));
            plugin.getCommand("god").setExecutor(new God(plugin));
            plugin.getCommand("speed").setExecutor(new Speed(plugin));
            plugin.getCommand("xp").setExecutor(new XPCommand(plugin));
            plugin.getCommand("playerinfo").setExecutor(new PlayerInfo(plugin));
            plugin.getCommand("nickname").setExecutor(new Nickname(plugin));

            plugin.getServer().getPluginManager().registerEvents(new NicknameApplyJoin(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new NicknameRemoveLeave(plugin), plugin);

            Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Loaded <light_purple>player_control<white> module.", Placeholder.parsed("prefix", plugin.internalPrefix)));

            return true;
        } else {
            return false;
        }
    }

    private boolean loadServerControlModule() {
        FileConfiguration modules = plugin.config.getConfig("modules.yml");

        if(modules.getBoolean("modules.server_control")) {
            plugin.getCommand("broadcast").setExecutor(new Broadcast(plugin));
            plugin.getCommand("list").setExecutor(new ListCommand(plugin));

            Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Loaded <light_purple>server_control<white> module.", Placeholder.parsed("prefix", plugin.internalPrefix)));

            return true;
        } else {
            return false;
        }
    }

    private boolean loadSpawnModule() {
        FileConfiguration modules = plugin.config.getConfig("modules.yml");

        if(modules.getBoolean("modules.spawn")) {
            plugin.getCommand("setspawn").setExecutor(new SetSpawn(plugin));
            plugin.getCommand("spawn").setExecutor(new Spawn(plugin));

            plugin.getServer().getPluginManager().registerEvents(new PlayerSpawnLocationListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new PlayerQuitListener(plugin), plugin);

            Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Loaded <light_purple>spawn<white> module.", Placeholder.parsed("prefix", plugin.internalPrefix)));

            return true;
        } else {
            return false;
        }
    }

    private boolean loadTeleportModule() {
        FileConfiguration modules = plugin.config.getConfig("modules.yml");
        
        if(modules.getBoolean("modules.teleport")) {
            plugin.getCommand("teleport").setExecutor(new Teleport(plugin));
            plugin.getCommand("world").setExecutor(new WorldCommand(plugin));
            plugin.getCommand("rtp").setExecutor(new RandomTeleport(plugin));
            plugin.getCommand("teleportrequest").setExecutor(new RequestTeleport(plugin));
            plugin.getCommand("back").setExecutor(new Back(plugin));

            Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Loaded <light_purple>teleport<white> module.", Placeholder.parsed("prefix", plugin.internalPrefix)));

            return true;
        } else {
            return false;
        }
    }

    public void loadModules() {
        loadCoreModule();
        loadPlayerControlModule();
        loadServerControlModule();
        loadSpawnModule();
        loadTeleportModule();
    }
}
