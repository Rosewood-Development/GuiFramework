package dev.esophose.guiframework.gui.screen;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ISlotable {

    @NotNull
    ItemStack getItemStack();

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

}
