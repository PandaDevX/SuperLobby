package com.multocraft.superlobby.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

public class ReflectionUtil {

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();



            return new String[] {texture, signature};
        } catch (IOException e) {
            return getSkin("RedSpeaks");
        }
    }

}
