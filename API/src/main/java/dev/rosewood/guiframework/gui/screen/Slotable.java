package dev.rosewood.guiframework.gui.screen;

import dev.rosewood.guiframework.gui.Tickable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Slotable {

    @Nullable
    ItemStack getItemStack(boolean isVisible);

    /**
     * Gets if the item is visible
     *
     * @param pageNumber The current page number
     * @param maxPageNumber The maximum page number
     * @return true if the item is visible, otherwise false
     */
    default boolean isVisible(int pageNumber, int maxPageNumber) {
        return true;
    }

    default boolean isTickable() {
        return this instanceof Tickable;
    }

}
