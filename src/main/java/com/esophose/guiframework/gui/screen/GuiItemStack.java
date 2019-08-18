package com.esophose.guiframework.gui.screen;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuiItemStack implements ISlotable {

    private ItemStack itemStack;

    public GuiItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

}
