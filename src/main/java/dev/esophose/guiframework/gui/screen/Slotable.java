package dev.esophose.guiframework.gui.screen;

import dev.esophose.guiframework.gui.Tickable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Slotable {

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

    /**
     * If this Slotable is an instance of an Tickable, tick it
     */
    default void tick() {
        if (this instanceof Tickable)
            ((Tickable) this).tick();
    }

}
