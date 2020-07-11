package dev.rosewood.guiframework.gui.screen;

import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface GuiScreenEditFilters {

    @NotNull
    GuiScreenEditFilters setWhitelist(@NotNull Material... whitelist);

    @NotNull
    GuiScreenEditFilters setBlacklist(@NotNull Material... blacklist);

    /**
     * Should modified items (those with lore, display name, glow, etc) be allowed?
     *
     * @param allowModified true to allow, false to disallow
     */
    GuiScreenEditFilters setAllowModified(boolean allowModified);

    @NotNull
    Set<Material> getWhitelist();

    @NotNull
    Set<Material> getBlacklist();

    boolean canInteractWith(ItemStack itemStack);

}
