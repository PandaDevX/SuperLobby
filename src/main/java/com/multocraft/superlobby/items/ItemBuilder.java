package com.multocraft.superlobby.items;

import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {


    private ItemStack itemStack;
    private ItemMeta meta;
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.meta = itemStack.getItemMeta();
    }

    public void setName(String name) {
        meta.setDisplayName(ChatUtil.colorize(name));
    }

    public void setEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
    }

    public void setLore(String... lore) {
        List<String> list = new ArrayList<>();
        for(String l : lore) {
            list.add(ChatUtil.colorize(l));
        }
        meta.setLore(list);
        list = null;
    }

    public void setFlag(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack createPlaceHolder() {
        meta.setDisplayName(" ");
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}