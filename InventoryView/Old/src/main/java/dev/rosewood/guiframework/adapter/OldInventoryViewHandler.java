package dev.rosewood.guiframework.adapter;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class OldInventoryViewHandler implements InventoryViewHandler {

    @Override
    public InventoryViewWrapper openInventory(Player player, Inventory inventory) {
        return wrap(player.openInventory(inventory));
    }

    @Override
    public <T extends InventoryEvent> InventoryViewWrapper getView(T event) {
        return wrap(event.getView());
    }

    @Override
    public InventoryViewWrapper getOpenInventory(Player player) {
        return wrap(player.getOpenInventory());
    }

    private static InventoryViewWrapper wrap(InventoryView view) {
        return new OldInventoryViewWrapper(view);
    }

}
