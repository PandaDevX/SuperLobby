package com.multocraft.superlobby.npc;

import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {


    private NPC npc;
    private Player player;
    private boolean isCancelled = false;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    public NPCInteractEvent(Player player, NPC npc) {
        this.player = player;
        this.npc = npc;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public NPC getNPC() {
        return npc;
    }

    public String getRawName() {
        return ChatUtil.strip(npc.getDisplayName());
    }

    public Player getPlayer() {
        return player;
    }
}
