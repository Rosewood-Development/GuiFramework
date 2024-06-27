package dev.rosewood.guiframework.adapter;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

public interface InventoryViewHandler {

    InventoryViewWrapper openInventory(Player player, Inventory inventory);

    <T extends InventoryEvent> InventoryViewWrapper getView(T event);

    InventoryViewWrapper getOpenInventory(Player player);

}
