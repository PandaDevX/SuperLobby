package com.multocraft.superlobby.player;

import com.multocraft.superlobby.SuperLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class TabUtil {

    private static void setPlayerListHeaderAndFooter(Player player, String header, String footer) {

        try {
            Object packet = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor().newInstance();

            Field a = packet.getClass().getDeclaredField("header");
            a.setAccessible(true);
            Field b = packet.getClass().getDeclaredField("footer");
            b.setAccessible(true);
            Object aObject = getNMSClass("IChatBaseComponent").getClass().getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
            Object bObject = getNMSClass("IChatBaseComponent").getClass().getMethod("b", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");

            a.set(packet, aObject);
            b.set(packet, bObject);

            if(player != null)
                sendPacket(player, packet);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void setTab(Player player, String header, String footer) {
        try {
            player.setPlayerListHeaderFooter(header, footer);
        }catch (NoSuchMethodError e) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(SuperLobby.getInstance(), () -> setPlayerListHeaderAndFooter(player, header, footer));
        }
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
