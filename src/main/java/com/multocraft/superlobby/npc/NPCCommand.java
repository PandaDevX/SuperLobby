package com.multocraft.superlobby.npc;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import com.multocraft.superlobby.npcapi.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCCommand implements CommandExecutor, Listener {

    private SuperLobby plugin;

    public NPCCommand(SuperLobby plugin) {
        plugin.getCommand("npc").setExecutor(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            String[] skin = NMSHelper.getInstance().getSkin(args[0]);
            NPC npc = plugin.getNpcManager().newNPC(NPCOptions.builder()
                    .name(ChatUtil.colorize(" &fServer"))
                    .hideNametag(false)
                    .texture(skin[0])
                    .signature(skin[1])
                    .location(((Player)sender).getLocation())
                    .build());

            npc.showTo((Player) sender);
        });

        return false;
    }

    @EventHandler
    public void onClick(NPCInteractEvent e) {

    }
}
