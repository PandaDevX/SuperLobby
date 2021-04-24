package com.multocraft.superlobby;

import com.multocraft.superlobby.chat.ChatListener;
import com.multocraft.superlobby.command.*;
import com.multocraft.superlobby.file.FileHandler;
import com.multocraft.superlobby.items.ServerSelector;
import com.multocraft.superlobby.join.JoinListener;
import com.multocraft.superlobby.npc.NPCCommand;
import com.multocraft.superlobby.npcapi.NPCManager;
import com.multocraft.superlobby.player.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperLobby extends JavaPlugin {

    private NPCManager npcManager;

    public static SuperLobby instance;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        instance = this;

        FileHandler.saveDefaultFile(this, "words.yml");
        FileHandler.saveFile(this, "spawn.yml");
        FileHandler.saveDefaultFile(this, "commands.yml");
        FileHandler.saveFile(this, "npc.yml");

        new JoinListener(this);
        new ChatListener(this);
        new PlayerListener(this);

        new DelSpawnCommand(this);
        new SetSpawn(this);
        new SpawnCommand(this);
        new ServerTeleportCommand(this);
        new SetServerCommand(this);
        new MaintenanceCommand(this);
        new NPCCommand(this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ScoreboardHandler(), 0, 5);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SuperLobby.getInstance(),
                new TabHandler(), 0, 5L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeSetter(), 0, 100L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new BossBarBroadcastHandler(), 0, 5);

        if(!getConfig().getBoolean("lobby.spawnEntity")) {
            for (Entity livingEntity : Bukkit.getWorld(getConfig().getString("lobby.world")).getEntities()) {
                if(livingEntity instanceof Player) {
                    continue;
                }
                livingEntity.remove();
            }
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord",  new ServerSelector());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new TabHandler());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new ScoreboardHandler());

        npcManager = new NPCManager(this, false);
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

        getNpcManager().deleteAllNPCs();
    }

    public static SuperLobby getInstance() {
        return instance;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }
}
