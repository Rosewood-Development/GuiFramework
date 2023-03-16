package dev.rosewood.guiframework.framework.gui.screen;

import dev.rosewood.guiframework.gui.screen.GuiScreenEditFilters;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

public class FrameworkScreenEditFilters implements GuiScreenEditFilters {

    private Predicate<ItemStack> whitelist;
    private Predicate<ItemStack> blacklist;
    private boolean allowModified;

    public FrameworkScreenEditFilters() {
        this.whitelist = null;
        this.blacklist = null;
        this.allowModified = true;
    }

    @NotNull
    @Override
    public FrameworkScreenEditFilters setWhitelist(@NotNull Material... whitelist) {
        this.whitelist = stack -> Arrays.stream(whitelist).allMatch(stack.getType()::equals);
        return this;
    }

    @NotNull
    @Override
    public GuiScreenEditFilters setWhitelist(@NotNull Predicate<ItemStack> whitelist) {
        this.whitelist = whitelist;
        return this;
    }

    @NotNull
    @Override
    public FrameworkScreenEditFilters setBlacklist(@NotNull Material... blacklist) {
        this.blacklist = stack -> Arrays.stream(blacklist).noneMatch(stack.getType()::equals);
        return this;
    }

    @NotNull
    @Override
    public GuiScreenEditFilters setBlacklist(@NotNull Predicate<ItemStack> blacklist) {
        this.blacklist = blacklist;
        return this;
    }

    @Override
    public FrameworkScreenEditFilters setAllowModified(boolean allowModified) {
        this.allowModified = allowModified;

        return this;
    }

    @NotNull
    @Override
    public Predicate<ItemStack> getWhitelist() {
        return whitelist;
    }

    @NotNull
    @Override
    public Predicate<ItemStack> getBlacklist() {
        return blacklist;
    }
    
    @Override
    public boolean canInteractWith(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!this.allowModified && itemMeta != null) {
            if (itemMeta.hasDisplayName() || itemMeta.hasLore() || itemMeta.hasEnchants() || itemMeta.hasAttributeModifiers())
                return false;
        }

        if (this.blacklist != null && this.blacklist.test(itemStack))
            return false;

        return this.whitelist == null || this.whitelist.test(itemStack);
    }

}
