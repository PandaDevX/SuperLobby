package com.multocraft.superlobby.items;

import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomItems {


    public static void setup(Player player) {

        ItemBuilder navigation = new ItemBuilder(Material.COMPASS);

        navigation.setName("&6&lS&ferver &6&lS&felector");

        ItemBuilder teleportBow = new ItemBuilder(Material.BOW);

        teleportBow.setName("&6&lT&feleport &6&lB&fow");

        ItemBuilder playersHider = new ItemBuilder(getSkull(player, "&6&lP&flayer &6&lH&fider"));

        ItemBuilder cosmetics = new ItemBuilder(Material.CHEST);
        cosmetics.setName("&6&lC&fosmetics");

        player.getInventory().setItem(3, navigation.build());
        player.getInventory().setItem(4, cosmetics.build());
        player.getInventory().setItem(0, teleportBow.build());
        player.getInventory().setItem(8, playersHider.build());
        player.updateInventory();

        cosmetics = null;
        playersHider = null;
        teleportBow = null;
        navigation = null;
    }

    public static ItemStack getSkull(OfflinePlayer player, String displayName) {

        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name)
                .collect(Collectors.toList())
                .contains("PLAYER_HEAD");

        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");

        ItemStack item = new ItemStack(type);

        if(!isNewVersion) {
            item.setDurability((short) 3);
        }

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if(isNewVersion) {
            meta.setOwningPlayer(player);
        }else {
            meta.setOwner(player.getName());
        }
        meta.setDisplayName(ChatUtil.colorize(displayName));

        item.setItemMeta(meta);

        return item;
    }
}
