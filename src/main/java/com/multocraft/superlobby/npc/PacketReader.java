package com.multocraft.superlobby.npc;

import com.multocraft.superlobby.SuperLobby;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_16_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketReader {
    
    Channel channel;
    public static Map<String, Channel> channels = new HashMap<>();

    public void inject(Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
            channel = (Channel) networkManager.getClass().getField("channel").get(networkManager);
            channels.put(player.getUniqueId().toString(), channel);
            if(channel.pipeline().get("PacketInjector") != null)
                return;

            channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Object>() {
                @Override
                protected void decode(ChannelHandlerContext channel, Object packet, List<Object> list) throws Exception {
                    list.add(packet);
                    readPacket(player, packet);
                }
            });
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public void unInject(Player player) {
        channel = channels.get(player.getUniqueId().toString());
        if(channel.pipeline().get("PacketInjector") != null)
            channel.pipeline().remove("PacketInjector");
    }

    public void readPacket(Player player, Object packet) {
        if(!packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            return;
        }
        if(getValue(packet, "action").toString().equalsIgnoreCase("ATTACK"))
            return;
        if(getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND"))
            return;
        if(getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
            return;
        int id = (int) getValue(packet, "a");
        if(getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) {
            for(NPC npc : NPC.npcList) {
                if(npc.getId() == id) {
                    NPCInteractEvent npcInteractEvent = new NPCInteractEvent(player, npc);
                    if(!Bukkit.isPrimaryThread()) {
                        Bukkit.getScheduler().runTaskAsynchronously(SuperLobby.getInstance(), () -> Bukkit.getPluginManager().callEvent(npcInteractEvent));
                    } else {
                        Bukkit.getPluginManager().callEvent(npcInteractEvent);
                    }
                }
            }
        }
    }

    private Object getValue(Object instance, String name) {
        Object result = null;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(instance);

            field.setAccessible(false);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(NPC.npcList.isEmpty()) return;
        for(NPC npc : NPC.npcList) {
            npc.showPlayer(e.getPlayer());
        }

        PacketReader packetReader = new PacketReader();
        packetReader.inject(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(NPC.npcList.isEmpty()) return;
        for(NPC npc : NPC.npcList) {
            npc.removePlayer(e.getPlayer());
        }

        PacketReader packetReader = new PacketReader();
        packetReader.unInject(e.getPlayer());
    }
}
