package com.multocraft.superlobby.npcapi;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final NPC clicked;

    @Getter
    private final Player player;

    @Getter
    private final NPCClickAction action;

    public NPCInteractEvent(NPC clicked, Player player, NPCClickAction action) {
        this.clicked = clicked;
        this.player = player;
        this.action = action;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
