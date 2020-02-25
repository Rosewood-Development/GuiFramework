package dev.esophose.guiframework.gui.screen;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuiItemStack implements Slotable {

    private ItemStack itemStack;

    public GuiItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItemStack(boolean isVisible) {
        return isVisible ? this.itemStack : null;
    }

}
