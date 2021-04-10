package com.multocraft.superlobby.chat;

import com.multocraft.superlobby.file.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class ChatListener implements Listener {
    JavaPlugin plugin;

    public ChatListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        String format = "&e" + e.getPlayer().getDisplayName() + " &8>> &7" + e.getMessage();
        format = format.replaceAll("%", "%%");
        format = ChatUtil.colorize(format);
        e.setFormat(format);
        format = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCooldown(AsyncPlayerChatEvent e) {
        if(e.getPlayer().hasPermission("sl.admin.chat")) return;

        if(ChatStorage.coolDown.containsKey(e.getPlayer().getUniqueId().toString())) {
            if(ChatStorage.coolDown.get(e.getPlayer().getUniqueId().toString()) > System.currentTimeMillis()) {
                ChatUtil.sendPlayerMessage(e.getPlayer(), "You can only chat every &b" + plugin.getConfig().getInt("chat-cooldown") + " seconds");
                return;
            }
            ChatStorage.coolDown.remove(e.getPlayer());
        }

        ChatStorage.coolDown.put(e.getPlayer().getUniqueId().toString(), ((System.currentTimeMillis() + plugin.getConfig().getLong("chat-cooldown")) *1000));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFilter(AsyncPlayerChatEvent e) {
        if(e.getPlayer().hasPermission("sl.admin.chat")) return;

        if(e.isAsynchronous()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> {
                        FileUtil fileUtil = new FileUtil(plugin, "words.yml", true);
                        ChatUtil.sensor(Arrays.asList(e.getMessage()), fileUtil.get().getStringList("words"));
                        ChatUtil.sensorAd(e.getMessage());
                        fileUtil = null;
                    });
        } else {
            ((Runnable) () -> {
                FileUtil fileUtil = new FileUtil(plugin, "words.yml", true);
                ChatUtil.sensor(Arrays.asList(e.getMessage()), fileUtil.get().getStringList("words"));
                ChatUtil.sensorAd(e.getMessage());
                fileUtil = null;
            }).run();
        }
    }

    @EventHandler
    public void onCancelTab(PlayerChatTabCompleteEvent e) {
        if(e.getPlayer().hasPermission("sl.admin.chat")) return;

        e.getTabCompletions().clear();
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        if(e.getSender() instanceof Player) {
            Player player = (Player) e.getSender();
            if (player.hasPermission("sl.admin.chat")) return;
            e.setCancelled(true);
            player = null;
        }
    }
}
