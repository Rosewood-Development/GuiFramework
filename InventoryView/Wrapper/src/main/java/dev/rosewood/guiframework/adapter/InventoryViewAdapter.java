package dev.rosewood.guiframework.adapter;

public class InventoryViewAdapter {

    private static InventoryViewHandler inventoryViewHandler;

    static {
        try {
            Class<?> inventoryViewClass = Class.forName("org.bukkit.inventory.InventoryView");
            if (inventoryViewClass.isInterface()) {
                inventoryViewHandler = (InventoryViewHandler) Class.forName("dev.rosewood.guiframework.adapter.NewInventoryViewHandler").getConstructor().newInstance();
            } else {
                inventoryViewHandler = (InventoryViewHandler) Class.forName("dev.rosewood.guiframework.adapter.OldInventoryViewHandler").getConstructor().newInstance();
            }
        } catch (Exception ignored) { }
    }

    public static InventoryViewHandler getHandler() {
        return inventoryViewHandler;
    }

}
