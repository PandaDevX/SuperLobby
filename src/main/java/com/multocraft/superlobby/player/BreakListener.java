package com.multocraft.superlobby.player;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import com.multocraft.superlobby.file.FileHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import java.util.HashSet;
import java.util.Set;

public class BreakListener implements Listener {

    Set<String> shooters = new HashSet<>();

    public BreakListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    boolean maintenance = SuperLobby.getInstance().getConfig().getBoolean("lobby.maintenance");

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(maintenance) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(maintenance) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(maintenance) return;
        if(e.getAction() == Action.PHYSICAL) {
            if(e.getClickedBlock().getType().name().endsWith("PRESSURE_PLATE")) {
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().clone().multiply(5).setY(2));
                e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().clone().getX(), 1.0D, e.getPlayer().getVelocity().clone().getZ()));
                return;
            }
            return;
        }
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getItem() != null && e.getItem().hasItemMeta() && ChatUtil.strip(e.getItem().getItemMeta().getDisplayName()).equals("Teleport Bow")) {
                shooters.add(e.getPlayer().getUniqueId().toString());
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onShoot(ProjectileHitEvent e) {
        if(e.getEntity() instanceof Arrow) {
            Player player = (Player) e.getEntity();

            if(shooters.contains(player.getUniqueId().toString())) {
                player.teleport(e.getEntity().getLocation());
                player.playSound(player.getLocation(), getSound("enderman scream"), 2.0f, 1.0f);
                shooters.remove(player.getUniqueId().toString());
            }
        }
    }

    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent e) {
        if(maintenance) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(PlayerItemDamageEvent e) {
        if(maintenance) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChangeWeather(WeatherChangeEvent e) {
        if(maintenance) return;
        if(SuperLobby.getInstance().getConfig().getBoolean("lobby.rain")) return;
        if(!e.toWeatherState())
            return;
        e.setCancelled(true);
        e.getWorld().setWeatherDuration(0);
        e.getWorld().setThundering(false);
    }

    @EventHandler
    public void onChangeFood(FoodLevelChangeEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBurnBlock(BlockBurnEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onMoveItem(PlayerItemConsumeEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(maintenance) return;
        if(e.getClickedInventory() == null) return;
        if(e.getClickedInventory() != e.getWhoClicked().getInventory()) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPick(EntityPickupItemEvent e) {
        if(maintenance) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onExplodeEntity(EntityExplodeEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(maintenance)return;
        e.setDeathMessage(null);
        e.getEntity().spigot().respawn();
        e.getDrops().clear();
    }

    @EventHandler
    public void onEntityDamage (EntityDamageEvent e) {
        if(maintenance)return;
        if (e.getEntity() instanceof Player) {
            //If the entity is a player
            Player player = (Player) e.getEntity();
            //Create a new variable for the player
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                //If the cause of the event is the void
                e.setCancelled(true);
                FileHandler.teleportToSpawn(player);
                player = null;
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if(maintenance)return;
        if(SuperLobby.getInstance().getConfig().getBoolean("lobby.spawnEntity")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if(maintenance)return;
        if(SuperLobby.getInstance().getConfig().getBoolean("lobby.spawnEntity")) return;
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent e) {
        if(maintenance)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onRemovePaint(HangingBreakByEntityEvent e) {
        if(maintenance) return;
        e.setCancelled(true);
    }

    public Sound getSound(String endsWith) {
        for(Sound sound : Sound.values()) {
            if(sound.name().endsWith(endsWith.toUpperCase().replace(" ", "_"))) {
                return sound;
            }
        }
        return null;
    }


}
