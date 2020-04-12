package dev.rosewood.guiframework.framework.gui.screen;

import dev.rosewood.guiframework.gui.screen.GuiItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FrameworkItem implements GuiItem {

    private ItemStack itemStack;

    public FrameworkItem(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItemStack(boolean isVisible) {
        return isVisible ? this.itemStack : null;
    }

}
