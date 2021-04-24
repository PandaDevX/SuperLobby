package com.multocraft.superlobby.npcapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NMSHelper {

    @Getter private static final NMSHelper instance = new NMSHelper();

    @Getter private final ServerVersion serverVersion;
    @Getter private final String serverVersionString;

    private NMSHelper() {
        serverVersionString = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        ServerVersion version = ServerVersion.UNKNOWN;

        for(ServerVersion option : ServerVersion.values()) {
            if(option.name().equalsIgnoreCase(serverVersionString)) {
                version = option;
            }
        }
        this.serverVersion = version;
    }

    @SneakyThrows
    public void sendPacket(Player player, Object packet) {
        if(player == null) return;
        Object handle = getHandle(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    @SneakyThrows
    public Object getHandle(Player player) {
        return player.getClass().getMethod("getHandle").invoke(player);
    }

    @SneakyThrows
    public Class<?> getNMSClass(String name) {
        return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
    }

    @SneakyThrows
    public Class<?> getCraftBukkitClass(String name) {
        return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + name);
    }

    public String[] getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[]{texture, signature};
        }catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
            return null;
        }
    }
}
