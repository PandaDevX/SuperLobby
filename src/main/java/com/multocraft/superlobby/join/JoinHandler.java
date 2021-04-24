package com.multocraft.superlobby.join;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import com.multocraft.superlobby.file.FileHandler;
import com.multocraft.superlobby.items.CustomItems;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class JoinHandler implements Runnable{

    private PlayerJoinEvent e;
    public JoinHandler(PlayerJoinEvent e) {
        this.e = e;
    }

    @Override
    public void run() {
        e.setJoinMessage(null);
        clear(e.getPlayer());

        sendMOTD(e.getPlayer());

        // teleport to spawn if available
        teleportToSpawn(e.getPlayer());

        sendTitle(e.getPlayer());

        setGameMode(e.getPlayer());

        setGod(e.getPlayer());

        giveCustomItems(e.getPlayer());

        if(SuperLobby.getInstance().getConfig().getBoolean("join.firework.spawn")) {
            spawnFireworks(e.getPlayer(), SuperLobby.getInstance().getConfig().getInt("join.firework.amount"));
        }
    }

    public void sendMOTD(Player player) {
        for(String message : ChatUtil.colorizeList(SuperLobby.getInstance().getConfig().getStringList("join.motd"))) {
            message = message.replace("{player}", player.getName());
            message = message.replace("{displayname}", player.getDisplayName());
            message = message.replace("{online}", Bukkit.getServer().getOnlinePlayers().size() + "");
            player.sendMessage(message);
        }
    }

    public void teleportToSpawn(Player player) {
        FileHandler.teleportToSpawn(player);
    }

    public void sendTitle(Player player) {
        if(!SuperLobby.getInstance().getConfig().getBoolean("join.title.enable")) return;

        ChatUtil.sendTitle(player, FileHandler.getConfigContent("join.title.title"), FileHandler.getConfigContent("join.title.subTitle"));
    }

    public void setGameMode(Player player) {
        player.setGameMode(GameMode.valueOf(FileHandler.getConfigContent("join.gamemode").replace(" ", "_").toUpperCase()));
        player.setAllowFlight(true);
    }

    public void setGod(Player player) {
        if(SuperLobby.getInstance().getConfig().getBoolean("lobby.pvp")) return;
        player.setInvulnerable(true);
    }

    public void clear(Player player) {
        player.getInventory().clear();
        for(int i = 0; i < 250; i++) {
            player.sendMessage(" ");
        }
    }

    public void spawnFireworks(Player player, int amount){
        Location loc = player.getLocation();
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).with(FireworkEffect.Type.BURST).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);

            fw2 = null;
        }

        loc = null;
        fw = null;
        fwm = null;
    }

    public void giveCustomItems(Player player) {
        CustomItems.setup(player);
    }
}
