package com.multocraft.superlobby.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    public static List<NPC> npcList = new ArrayList<>();
    private Location location;
    private String name;
    private String texture;
    private String signature;
    private GameProfile gameProfile;
    private Object entityPlayer;
    public NPC(Location location, String name, String texture, String signature) {
        this.location = location;
        this.name = name;
        this.texture = texture;
        this.signature = signature;
    }

    public void spawn() {
        try {
            Object minecraftServer = ReflectionUtil.getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
            Object worldServer =  ReflectionUtil.getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(location.getWorld());

            this.gameProfile = new GameProfile(UUID.randomUUID(), ChatUtil.colorize(this.name));
            this.gameProfile.getProperties().put("texture", new Property("textures", texture, signature));

            Constructor<?> entityPlayerConstructor = ReflectionUtil.getNMSClass("EntityPlayer").getDeclaredConstructors()[0];
            Constructor<?> interactManagerConstructor = ReflectionUtil.getNMSClass("PlayerInteractManager").getDeclaredConstructors()[0];

            this.entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServer, this.gameProfile, interactManagerConstructor.newInstance(worldServer));
            this.entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
            .invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void showPlayer(Player player) {
        try {
            Object addPlayerEnum = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("ADD_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo").getConstructor(ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), Class.forName("[Lnet.minecarft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".EntityPlayer;"));

            Object array = Array.newInstance(ReflectionUtil.getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, this.entityPlayer);

            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);


            Constructor<?> packetPlayOutNamedEntitySpawnConstructor = ReflectionUtil.getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(ReflectionUtil.getNMSClass("EntityHuman"));
            Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(this.entityPlayer);

            Constructor<?> packetPlayOutEntityHeadRotationConstructor = ReflectionUtil.getNMSClass("PacketPlayOutEntityHeadRotationConstructor").getConstructor(ReflectionUtil.getNMSClass("Entity"), byte.class);
            float yaw = (float) this.entityPlayer.getClass().getField("yaw").get(this.entityPlayer);
            Object packetPlayOutEntityHeadRotation = packetPlayOutEntityHeadRotationConstructor.newInstance(this.entityPlayer, (byte)(yaw * 256 / 360));

            ReflectionUtil.sendPacket(player, packetPlayOutPlayerInfo);
            ReflectionUtil.sendPacket(player, packetPlayOutNamedEntitySpawn);
            ReflectionUtil.sendPacket(player, packetPlayOutEntityHeadRotation);

        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(Player player) {
        try {
            Object addPlayerEnum = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("REMOVE_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo").getConstructor(ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), Class.forName("[Lnet.minecarft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".EntityPlayer;"));

            Object array = Array.newInstance(ReflectionUtil.getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, this.entityPlayer);

            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);

            Constructor<?> packetPlayOutEntityDestroyConstructor = ReflectionUtil.getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int.class);
            int id = (int) entityPlayer.getClass().getMethod("getId").invoke(entityPlayer);
            Object packetPlayOutEntityDestroy = packetPlayOutEntityDestroyConstructor.newInstance(id);

            ReflectionUtil.sendPacket(player, packetPlayOutPlayerInfo);
            ReflectionUtil.sendPacket(player, packetPlayOutEntityDestroy);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation() {
        return location;
    }

    public int getId() {
        try {
            return (int) entityPlayer.getClass().getMethod("getId").invoke(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getDisplayName() {
        return name;
    }
}
