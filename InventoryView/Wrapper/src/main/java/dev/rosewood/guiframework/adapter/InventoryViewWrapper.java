package dev.rosewood.guiframework.adapter;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface InventoryViewWrapper {

    Inventory getTopInventory();

    Inventory getBottomInventory();

    Inventory getInventory(int rawSlot);

    ItemStack getItem(int rawSlot);

    void setItem(int rawSlot, ItemStack item);

    InventoryType.SlotType getSlotType(int rawSlot);

}
