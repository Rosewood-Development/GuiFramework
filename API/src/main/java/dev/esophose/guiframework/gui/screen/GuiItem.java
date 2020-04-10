package dev.esophose.guiframework.gui.screen;

import org.bukkit.inventory.ItemStack;

public interface GuiItem extends Slotable {

    /**
     * Gets the ItemStack for this item
     *
     * @param isVisible If the ItemStack will be visible or not
     * @return The ItemStack for this item
     */
    ItemStack getItemStack(boolean isVisible);

}
