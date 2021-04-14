package com.multocraft.superlobby.npc;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.file.FileHandler;
import com.multocraft.superlobby.file.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class NPCFile {

    public static void saveNPC(String name, String skin, Location location, String displayName) {
        FileUtil fileUtil = new FileUtil(SuperLobby.getInstance(), "npc.yml", false);
        FileConfiguration config = fileUtil.get();
        config.set(name + ".skin", skin);
        config.set(name + ".displayName", displayName);
        config.set(name + ".location.world", location.getWorld().getName());
        config.set(name + ".location.x", location.getX());
        config.set(name + ".location.y", location.getY());
        config.set(name + ".location.z", location.getZ());
        config.set(name + ".location.yaw", location.getYaw());
        config.set(name + ".location.pitch", location.getPitch());
        fileUtil.save();
    }

    public static void spawnNPCS() {
        FileConfiguration config = FileHandler.getConfig("npc.yml", false);
        if(config.getKeys(false).isEmpty()) return;
        for(String key : config.getKeys(false)) {
            World world = Bukkit.getWorld(config.getString(key + ".location.world"));
            double x = config.getDouble(key + ".location.x");
            double y = config.getDouble(key + ".location.y");
            double z = config.getDouble(key + ".location.z");
            float yaw =(float)config.getDouble(key + ".location.yaw");
            float pitch =(float)config.getDouble(key + ".location.pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);
            String skin = config.getString(key + ".skin");
            String disPlayName = config.getString(key + ".displayName");
            NPC npc = new NPC(location, disPlayName, ReflectionUtil.getSkin(skin)[0], ReflectionUtil.getSkin(skin)[1]);
            npc.spawn();
            if(!Bukkit.getOnlinePlayers().isEmpty()) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    npc.showPlayer(player);
                }
            }
            NPC.npcList.add(npc);

            world = null;
            location = null;
            skin = null;
            disPlayName = null;
            npc = null;
        }
        config = null;
    }

    public static FileConfiguration getConfig() {
        return FileHandler.getConfig("npc.yml", false);
    }

    public static void removeNPC(String name) {
        FileUtil fileUtil = new FileUtil(SuperLobby.getInstance(), "npc.yml", false);
        fileUtil.get().set(name, null);
        fileUtil.save();
    }
}
