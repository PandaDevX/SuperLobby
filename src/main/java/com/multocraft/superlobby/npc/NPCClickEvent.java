package com.multocraft.superlobby.npc;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.multocraft.superlobby.SuperLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCClickEvent implements Listener {

    @EventHandler
    public void onClick(NPCInteractEvent e) {
        if(e.getRawName().toLowerCase().contains("prison")) {
            connect(e.getPlayer(), "prison");
            return;
        }
        if(e.getRawName().toLowerCase().contains("skyblock")) {
            connect(e.getPlayer(), "skyblock");
            return;
        }
    }

    public void connect(Player player, String server) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("Connect");
        byteArrayDataOutput.writeUTF(server);

        player.sendPluginMessage(SuperLobby.getInstance(), "BungeeCord", byteArrayDataOutput.toByteArray());
    }
}
