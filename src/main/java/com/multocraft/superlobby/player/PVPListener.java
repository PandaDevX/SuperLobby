package com.multocraft.superlobby.player;

import com.multocraft.superlobby.SuperLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PVPListener implements Listener {

    public PVPListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e) {
        if(SuperLobby.getInstance().getConfig().getBoolean("lobby.pvp")) return;

        e.setCancelled(true);
    }
}
