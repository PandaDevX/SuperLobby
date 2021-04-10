package com.multocraft.superlobby.join;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import com.multocraft.superlobby.player.MainThread;
import com.multocraft.superlobby.player.TabHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinListener implements Listener {

    public JoinListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().cancelTask(SuperLobby.id);
        SuperLobby.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(SuperLobby.getInstance(),
                new TabHandler(), 0, 5L);
        if(e.isAsynchronous()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(SuperLobby.getInstance(),
                    new JoinHandler(e));
        } else {
            new JoinHandler(e).run();
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if(!SuperLobby.getInstance().getConfig().getBoolean("lobby.maintenance")) return;

        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatUtil.colorize("&c&lHey &fServer is under maintenance"));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        if(SuperLobby.id == -1) return;
        Bukkit.getScheduler().cancelTask(SuperLobby.id);
        SuperLobby.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(SuperLobby.getInstance(),
                new TabHandler(), 0, 5L);
        if(MainThread.mainThreadMap.containsKey("server")) {
            MainThread.mainThreadMap.get("server").removePlayer(e.getPlayer());
        }
        if(MainThread.mainThreadMap2.containsKey("server")) {
            org.bukkit.boss.BossBar bar = MainThread.mainThreadMap2.get("server");
            bar.removeAll();
            bar = null;
        }
    }
}
