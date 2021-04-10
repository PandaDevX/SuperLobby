package com.multocraft.superlobby.join;

import com.multocraft.superlobby.file.FileHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

public class ListMotdListener implements Listener {

    public ListMotdListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onList(ServerListPingEvent e) {
        e.setMotd(FileHandler.getConfigContent("server-motd").replace("\\n", "\n"));
    }
}
