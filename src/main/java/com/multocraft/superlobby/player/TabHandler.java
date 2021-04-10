package com.multocraft.superlobby.player;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TabHandler implements Runnable {
    private int header_index = 0;
    private int footer_index = 0;


    @Override
    public void run() {
        List<String> headers = ChatUtil.colorizeList(SuperLobby.getInstance().getConfig().getStringList("join.headers"));
        List<String> footers = ChatUtil.colorizeList(SuperLobby.getInstance().getConfig().getStringList("join.footers"));

        if(header_index >= headers.size()) {
            header_index = 0;
        }
        if(footer_index >= footers.size()) {
            footer_index = 0;
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            TabUtil.setTab(player, headers.get(header_index).replace("\\n", "\n")
                    .replace("{player}", player.getName())
                    .replace("{displayname}", player.getDisplayName())
                    .replace("{online}", Bukkit.getOnlinePlayers().size() + ""), footers.get(footer_index).replace("\\n", "\n")
            .replace("{player}", player.getName())
            .replace("{displayname}", player.getDisplayName())
            .replace("{online}", Bukkit.getOnlinePlayers().size() + ""));
        }
        header_index++;
        footer_index++;
        headers = null;
        footers = null;
    }
}
