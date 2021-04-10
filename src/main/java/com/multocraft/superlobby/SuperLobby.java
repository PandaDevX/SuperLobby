package com.multocraft.superlobby;

import com.multocraft.superlobby.chat.ChatListener;
import com.multocraft.superlobby.command.DelSpawnCommand;
import com.multocraft.superlobby.command.SetSpawn;
import com.multocraft.superlobby.command.SpawnCommand;
import com.multocraft.superlobby.file.FileHandler;
import com.multocraft.superlobby.join.JoinListener;
import com.multocraft.superlobby.join.ListMotdListener;
import com.multocraft.superlobby.player.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperLobby extends JavaPlugin {

    public static int id = -1;

    public static SuperLobby instance;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        instance = this;

        FileHandler.saveDefaultFile(this, "words.yml");
        FileHandler.saveFile(this, "spawn.yml");
        FileHandler.saveDefaultFile(this, "commands.yml");

        new JoinListener(this);
        new ChatListener(this);
        new ListMotdListener(this);
        new BreakListener(this);
        new PVPListener(this);

        new DelSpawnCommand(this);
        new SetSpawn(this);
        new SpawnCommand(this);

        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(SuperLobby.getInstance(),
                new TabHandler(), 0, 5L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeSetter(), 0, 100L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new BossBarBroadcastHandler(), 0, 1);

        if(!getConfig().getBoolean("lobby.spawnEntity")) {
            for (Entity livingEntity : Bukkit.getWorld(getConfig().getString("lobby.world")).getEntities()) {
                if(livingEntity instanceof Player) {
                    continue;
                }
                livingEntity.remove();
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if(MainThread.mainThreadMap.containsKey("server")) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                MainThread.mainThreadMap.get("server").removePlayer(player);
            }
        }
        if(MainThread.mainThreadMap2.containsKey("server")) {
            MainThread.mainThreadMap2.get("server").removeAll();
        }
        MainThread.mainThreadMap2.clear();
        MainThread.mainThreadMap.clear();
    }

    public static SuperLobby getInstance() {
        return instance;
    }
}
