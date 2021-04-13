package com.multocraft.superlobby.player;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;

public class TabHandler implements Runnable, PluginMessageListener {
    private int header_index = 0;
    private int footer_index = 0;
    private String[] playerList = null;


    @Override
    public void run() {
        sendAllPlayers();
        List<String> headers = ChatUtil.colorizeList(SuperLobby.getInstance().getConfig().getStringList("join.headers"));
        List<String> footers = ChatUtil.colorizeList(SuperLobby.getInstance().getConfig().getStringList("join.footers"));

        if(header_index >= headers.size()) {
            header_index = 0;
        }
        if(footer_index >= footers.size()) {
            footer_index = 0;
        }
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                TabUtil.setTab(player, headers.get(header_index).replace("\\n", "\n")
                        .replace("{player}", player.getName())
                        .replace("{displayname}", player.getDisplayName())
                        .replace("{online}", Bukkit.getOnlinePlayers().size() + ""), footers.get(footer_index).replace("\\n", "\n")
                        .replace("{player}", player.getName())
                        .replace("{displayname}", player.getDisplayName())
                        .replace("{online}", (playerList != null ? playerList.length : 0) + ""));
            }
        }
        header_index++;
        footer_index++;
        headers = null;
        footers = null;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals("BungeeCord"))
            return;
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String command = input.readUTF();
        if(command.equals("PlayerList")) {
            if(input.readUTF().equals("ALL")) {
                playerList = input.readUTF().split(", ");
            }
        }
    }

    public void sendAllPlayers() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("PlayerList");
        output.writeUTF("ALL");
        Bukkit.getServer().sendPluginMessage(SuperLobby.getInstance(), "BungeeCord", output.toByteArray());
    }
}
