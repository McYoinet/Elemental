package net.palenquemc.elemental.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;

public class ConfigurationManager {
    private final Elemental plugin;

    public ConfigurationManager(Elemental plugin) {
        this.plugin = plugin;
    }

    private final HashMap<String, FileConfiguration> configs = new HashMap<>();

    MiniMessage mm = MiniMessage.miniMessage();

    public void loadConfigurations() {
        configs.put("messages.yml", new YamlConfiguration());
        configs.put("config.yml", new YamlConfiguration());

        configs.forEach((filename, config) -> {
            File file = new File(plugin.getDataFolder(), filename);

            if(!file.exists()) {
                file.getParentFile().mkdirs();
                plugin.saveResource(filename, false);
            }

            try {
                config.load(file);
            } catch(IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Successfully loaded configuration file <light_purple><file><white>.", Placeholder.parsed("prefix", plugin.internalPrefix), Placeholder.unparsed("file", filename)));
        });

        Bukkit.getConsoleSender().sendMessage(mm.deserialize("<prefix> Successfully loaded all configuration files.", Placeholder.parsed("prefix", plugin.internalPrefix)));
    }

    public FileConfiguration getConfig(String filename) {
        return configs.get(filename);
    }

    public boolean reloadConfig() {
        configs.forEach((filename, config) -> {
            File file = new File(plugin.getDataFolder(), filename);

            if(!file.exists()) {
                file.getParentFile().mkdirs();
                plugin.saveResource(filename, false);
            }

            try {
                config.load(file);
            } catch(IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    public HashMap<String, FileConfiguration> getConfigHashMap() {
        return configs;
    }
}
