package com.multocraft.superlobby.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

    public File file;
    public FileConfiguration config;
    private String name;
    private Plugin plugin;
    private boolean resource;

    public FileUtil(Plugin plugin, String name, boolean resource) {
        this.plugin = plugin;
        this.resource = resource;
        this.name = name;
    }

    public void createFolderFor(String path) {
        File folder = new File(plugin.getDataFolder(), path);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void createFile() {
        if(file == null)
            this.file = resource ? new File(plugin.getDataFolder(), "settings/" + name) : new File(plugin.getDataFolder() + "/data/" + name);
        if(resource) {
            if(!file.exists()) {
                plugin.saveResource("settings/" + name, false);
            }
            return;
        }
        if(!file.exists()) {
            try {
                file.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        if(file == null)
            this.file = resource ? new File(plugin.getDataFolder(), "settings/" + name) : new File(plugin.getDataFolder() + "/data/" + name);
        try {
            config.save(this.file);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration get() {
        if(config == null)
            reload();
        return config;
    }

    public void reload() {
        if(file == null)
            this.file = resource ? new File(plugin.getDataFolder(), "settings/" + name) : new File(plugin.getDataFolder() + "/data/" + name);
        if(resource) {
            config = YamlConfiguration.loadConfiguration(file);

            InputStream defaultStream = plugin.getResource("settings/" + name);
            if(defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
                config.setDefaults(defaultConfig);
            }
        }
        if(!file.exists()) {
            try {
                file.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
}