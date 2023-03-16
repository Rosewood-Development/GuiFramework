package dev.rosewood.guiframework.gui.screen;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Predicate;

public interface GuiScreenEditFilters {

    @NotNull
    GuiScreenEditFilters setWhitelist(@NotNull Material... whitelist);

    @NotNull
    GuiScreenEditFilters setWhitelist(@NotNull Predicate<ItemStack> whitelist);

    @NotNull
    GuiScreenEditFilters setBlacklist(@NotNull Material... blacklist);

    @NotNull
    GuiScreenEditFilters setBlacklist(@NotNull Predicate<ItemStack> blacklist);

    /**
     * Should modified items (those with lore, display name, glow, etc) be allowed?
     *
     * @param allowModified true to allow, false to disallow
     */
    GuiScreenEditFilters setAllowModified(boolean allowModified);

    @NotNull
    Predicate<ItemStack> getWhitelist();

    @NotNull
    Predicate<ItemStack> getBlacklist();

    boolean canInteractWith(ItemStack itemStack);

}
