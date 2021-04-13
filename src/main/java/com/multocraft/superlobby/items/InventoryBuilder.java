package com.multocraft.superlobby.items;

import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryBuilder {

    private final int row;
    private final String name;
    private Inventory inventory = null;

    public InventoryBuilder(String name, int row) {
        this.name = name;
        this.row = row;
        this.inventory = Bukkit.createInventory(null, row * 9, ChatUtil.colorize(name));
    }

    public InventoryBuilder(String name, InventoryType type) {
        this.inventory = Bukkit.createInventory(null, type, ChatUtil.colorize(name));
        this.name = name;
        this.row = 0;
    }

    public InventoryBuilder(Inventory inventory) {
        this.inventory = inventory;
        this.row = inventory.getSize() / 9 + (Math.min(inventory.getSize() % 9, 1));
        this.name = "";
    }

    public void placePlaceHolders(int start, int end, Material type) {
        start -= 1;
        if(start <= 0) {
            start = 0;
        }
        if(end >= inventory.getSize()) {
            end = inventory.getSize();
        }
        ItemBuilder builder = new ItemBuilder(type);
        builder.setName(" ");
        for (int i = start; i < end; i++) {
            inventory.setItem(i, builder.createPlaceHolder());
        }
    }

    public void placePlaceHolders(int start, int end) {
        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name)
                .collect(Collectors.toList())
                .contains("BLACK_STAINED_GLASS_PANE");

        Material type = Material.matchMaterial(isNewVersion ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE");

        ItemStack item = new ItemStack(type);
        if(!isNewVersion) {
            item = new ItemStack(type, 1, DyeColor.BLACK.getDyeData());
        }
        start -= 1;
        if(start <= 0) {
            start = 0;
        }
        if(end >= inventory.getSize()) {
            end = inventory.getSize();
        }
        ItemBuilder builder = new ItemBuilder(item);
        builder.setName(" ");
        for (int i = start; i < end; i++) {
            inventory.setItem(i, builder.createPlaceHolder());
        }
    }

    public void placePlaceHolders(int start, int end, ItemBuilder builder) {
        start -= 1;
        if(start <= 0) {
            start = 0;
        }
        if(end >= inventory.getSize()) {
            end = inventory.getSize();
        }
        for (int i = start; i < end; i++) {
            inventory.setItem(i, builder.createPlaceHolder());
        }
    }

    public void placePlaceHolders(Material type) {
        ItemBuilder builder = new ItemBuilder(type);
        builder.setName(" ");
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, builder.createPlaceHolder());
        }
    }

    public void placePlaceHolders(ItemBuilder builder) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, builder.createPlaceHolder());
        }
    }

    public void listItems(int start, int end, boolean addPlaceHolders, List<ItemBuilder> itemBuilders) {
        start -= 1;
        if(addPlaceHolders)
            placePlaceHolders(Material.BLACK_STAINED_GLASS);

        if(start > end || start > inventory.getSize()) {
            start = 1;
        }
        if(end >= inventory.getSize()) {
            end = inventory.getSize();
        }
        Iterator<ItemBuilder> iterator = itemBuilders.iterator();
        while(iterator.hasNext()) {
            if((end - start) == 1)  {
                break;
            }
            inventory.setItem(start, iterator.next().build());
            start++;
        }
    }

    public void setItem(int slot, ItemBuilder builder) {
        slot -= 1;
        if(slot >= inventory.getSize()) {
            slot = inventory.getSize() - 1;
        }
        inventory.setItem(slot, builder.build());
    }

    public void setItem(int slot, ItemStack itemStack) {
        slot -= 1;
        if(slot >= inventory.getSize()) {
            slot = inventory.getSize() - 1;
        }
        inventory.setItem(slot, itemStack);
    }

    public int getSize() {
        return inventory.getSize();
    }

    public Inventory build() {
        return this.inventory;
    }
}
