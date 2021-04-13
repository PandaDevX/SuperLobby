package com.multocraft.superlobby.items;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.multocraft.superlobby.SuperLobby;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ServerSelector implements PluginMessageListener, Listener {
    HashMap<String, Integer> playersCount = new HashMap<>();
    HashMap<String, String> serverAddress = new HashMap<>();
    HashMap<String, Integer> serverPort = new HashMap<>();

    Inventory inventory = null;

    public void setup() {
        InventoryBuilder builder = new InventoryBuilder("&7&lSERVER SELECTOR", 3);

        ItemBuilder lobby = new ItemBuilder(getEnderEye());
        lobby.setName("&6&lLobby");
        sendInfo("lobby");
        sendServerConnectionInfo("lobby");
        int lobbyPlayers = playersCount.getOrDefault("lobby", 0);
        lobby.setLore("&7Click to connect to:", "&cLobby", "", "&7Players - &f" + lobbyPlayers, "&7Status - " + onLineStatus("lobby"));
        builder.setItem(12, lobby);

        ItemBuilder prison = new ItemBuilder(getIronBars());
        prison.setName("&6&lPrison");
        sendInfo("prison");
        sendServerConnectionInfo("prison");
        int prisonPlayers = playersCount.getOrDefault("prison", 0);
        prison.setLore("&7Click to connect to:", "&cPrison", "", "&7Players - &f" + prisonPlayers + "&7Status - " + onLineStatus("prison"));
        builder.setItem(14, prison);

        ItemBuilder skyBlock = new ItemBuilder(Material.GRASS_BLOCK);
        skyBlock.setName("&6&lSky Block");
        sendInfo("skyblock");
        sendServerConnectionInfo("skyblock");
        int skyPlayers = playersCount.getOrDefault("skyblock", 0);
        skyBlock.setLore("&7Click to connect to:", "&cLobby", "", "&7Players - &f" + skyPlayers, "&7Status - " + onLineStatus("skyblock"));
        builder.setItem(16, skyBlock);

        inventory = builder.build();

        builder = null;
        lobby = null;
        prison = null;
        skyBlock = null;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    public Material getEnderEye() {
        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name)
                .collect(Collectors.toList())
                .contains("ENDER_EYE");
        return Material.matchMaterial(isNewVersion ? "EYE_OF_ENDER" : "ENDER_EYE");
    }

    public Material getIronBars() {
        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name)
                .collect(Collectors.toList())
                .contains("IRON_FENCE");
        return Material.matchMaterial(isNewVersion ? "IRON_FENCE" : "IRON_BARS");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals("BungeeCord"))
            return;
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        if(input.readUTF().equals("PlayerCount")) {
            playersCount.put(input.readUTF(), input.readInt());
        }
        if(input.readUTF().equals("ServerIP")) {
            String serverName = input.readUTF();
            String ip = input.readUTF();
            int port = input.readInt();

            serverAddress.put(serverName, ip);
            serverPort.put(serverName, port);
        }
    }

    public void sendInfo(String server) {
        ByteArrayDataOutput output =  ByteStreams.newDataOutput();
        output.writeUTF("PlayerCount");
        output.writeUTF(server);

        SuperLobby.getInstance().getServer().sendPluginMessage(SuperLobby.getInstance(), "BungeeCord", output.toByteArray());
    }

    public void sendServerConnectionInfo(String server) {
        ByteArrayDataOutput output =  ByteStreams.newDataOutput();
        output.writeUTF("ServerIP");
        output.writeUTF(server);

        SuperLobby.getInstance().getServer().sendPluginMessage(SuperLobby.getInstance(), "BungeeCord", output.toByteArray());
    }

    public void connect(Player player, String server) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);

        player.sendPluginMessage(SuperLobby.getInstance(), "BungeeCord", output.toByteArray());
    }

    public boolean isOnline(String address, int port) {
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(address, port), 10); //good timeout is 10-20
            s.close();
        }catch (IOException e) {
            return false;
        }
        return true;
    }

    public String onLineStatus(String server) {
        if(isOnline(serverAddress.get(server), serverPort.get(server))) {
            return "&aOnline";
        }
        return "&cOffline";
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(inventory == null) return;
        if(!e.getInventory().equals(inventory)) return;
        e.setCancelled(true);
        if(e.getSlot() == 11) {
            return;
        }
        if(e.getSlot() == 13) {
            connect((Player) e.getWhoClicked(), "prison");
            return;
        }
        if(e.getSlot() == 15) {
            connect((Player) e.getWhoClicked(), "skyblock");
            return;
        }
    }
}
