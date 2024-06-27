package dev.rosewood.guiframework.adapter;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class OldInventoryViewWrapper implements InventoryViewWrapper {

    private final InventoryView view;

    public OldInventoryViewWrapper(InventoryView view) {
        this.view = view;
    }

    @Override
    public Inventory getTopInventory() {
        return this.view.getTopInventory();
    }

    @Override
    public Inventory getBottomInventory() {
        return this.view.getBottomInventory();
    }

    @Override
    public Inventory getInventory(int rawSlot) {
        return this.view.getInventory(rawSlot);
    }

    @Override
    public ItemStack getItem(int rawSlot) {
        return this.view.getItem(rawSlot);
    }

    @Override
    public void setItem(int rawSlot, ItemStack item) {
        this.view.setItem(rawSlot, item);
    }

    @Override
    public InventoryType.SlotType getSlotType(int rawSlot) {
        return this.view.getSlotType(rawSlot);
    }

}
