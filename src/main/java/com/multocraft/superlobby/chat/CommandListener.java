package com.multocraft.superlobby.chat;

import com.multocraft.superlobby.file.FileHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandListener implements Listener {

    public CommandListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlackListCommand(PlayerCommandPreprocessEvent e) {
        if(e.getPlayer().hasPermission("sl.admin.command")) return;

        List<String> blackList = FileHandler.getConfig("commands.yml", true).getStringList("blacklist");

        for(String cmd : blackList) {
            if(e.getMessage().toLowerCase().startsWith(cmd)) {
                e.setCancelled(true);
                ChatUtil.sendPlayerMessage(e.getPlayer(), "&cYou cannot use that command");
                break;
            }
        }
        blackList = null;
    }
}
