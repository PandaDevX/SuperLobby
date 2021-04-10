package com.multocraft.superlobby.file;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FileHandler {

    public static void saveDefaultFile(Plugin plugin, String name) {
        FileUtil fileUtil = new FileUtil(plugin, name, true);
        fileUtil.createFolderFor("settings");
        fileUtil.createFile();
        fileUtil = null;
    }

    public static void saveFile(Plugin plugin, String name) {
        FileUtil fileUtil = new FileUtil(plugin, name, false);
        fileUtil.createFolderFor("data");
        fileUtil.createFile();
        fileUtil = null;
    }

    public static FileConfiguration getConfig(String name, boolean defaultFile) {
        FileUtil fileUtil = new FileUtil(SuperLobby.getInstance(), name, defaultFile);
        return fileUtil.get();
    }

    public static String getConfigContent(String path) {
        return ChatUtil.colorize(SuperLobby.getInstance().getConfig().getString(path));
    }
    public static String getConfigContentRaw(String path) {
        return SuperLobby.getInstance().getConfig().getString(path);
    }

    public static void teleportToSpawn(Player player) {
        player.teleport(getLocation());
    }

    public static void saveLocation(Player player) {
        Location location = player.getLocation();
        FileUtil fileUtil = new FileUtil(SuperLobby.getInstance(), "spawn.yml", false);
        fileUtil.get().set("spawn.world", location.getWorld().getName());
        fileUtil.get().set("spawn.x", location.getX());
        fileUtil.get().set("spawn.y", location.getY());
        fileUtil.get().set("spawn.z", location.getZ());
        fileUtil.get().set("spawn.yaw", location.getYaw());
        fileUtil.get().set("spawn.pitch", location.getPitch());
        fileUtil.save();
        location = null;
        fileUtil = null;
    }

    public static void deleteLocation() {
        FileUtil fileUtil = new FileUtil(SuperLobby.getInstance(), "spawn.yml", false);
        fileUtil.get().set("spawn", null);
        fileUtil.save();
        fileUtil = null;
    }

    public static Location getLocation() {
        try {
            FileConfiguration config = getConfig("spawn.yml", false);
            Float yaw = (float) config.getDouble("spawn.yaw");
            Float pitch = (float) config.getDouble("spawn.pitch");
            Location location = new Location(Bukkit.getWorld(config.getString("spawn.world")),
                    config.getDouble("spawn.x"),
                    config.getDouble("spawn.y"),
                    config.getDouble("spawn.z"),
                    yaw,
                    pitch);
            config = null;
            return location;
        }catch (NullPointerException e) {
            return Bukkit.getWorld(SuperLobby.getInstance().getConfig().getString("lobby.world")).getSpawnLocation();
        }
    }
}
