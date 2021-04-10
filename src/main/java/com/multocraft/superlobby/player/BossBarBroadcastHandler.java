package com.multocraft.superlobby.player;

import com.multocraft.superlobby.file.FileHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.Collection;

public class BossBarBroadcastHandler implements Runnable {

    private int index = 0;

    @Override
    public void run() {
        if(MainThread.mainThreadMap.containsKey("server")) {
            BossBar bar = MainThread.mainThreadMap.get("server");
            for(Player player : bar.getPlayers()) {
                if(bar.getPlayers().contains(bar)) {
                    continue;
                }
                bar.addPlayer(player);
            }
            bar = null;
            return;
        }
        if(MainThread.mainThreadMap2.containsKey("server")) {
            org.bukkit.boss.BossBar bar = MainThread.mainThreadMap2.get("server");
            for(Player player : bar.getPlayers()) {
                if(bar.getPlayers().contains(bar)) {
                    continue;
                }
                bar.addPlayer(player);
            }
            bar = null;
            return;
        }
        if(Bukkit.getOnlinePlayers().size() >= 1) {
            try {
                org.bukkit.boss.BossBar bar = Bukkit.createBossBar(FileHandler.getConfigContent("join.bossbar.message"), BarColor.PURPLE, BarStyle.SOLID);
                if(!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        bar.addPlayer(player);
                    }
                }
                MainThread.mainThreadMap2.put("server", bar);
                index++;
                bar = null;
            }catch (NoSuchMethodError e) {
                BossBar bar = BossBarAPI.addBar((Collection<Player>) Bukkit.getOnlinePlayers(), new TextComponent(TextComponent.fromLegacyText(FileHandler.getConfigContent("join.bossbar.message"))),
                        BossBarAPI.Color.PURPLE,
                        BossBarAPI.Style.NOTCHED_10,1.0f);
                MainThread.mainThreadMap.put("server", bar);
                index++;
                bar = null;
            }
        }
    }
}
