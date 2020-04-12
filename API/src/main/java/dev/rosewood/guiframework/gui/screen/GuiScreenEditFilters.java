package dev.rosewood.guiframework.gui.screen;

import java.util.Set;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface GuiScreenEditFilters {

    @NotNull
    GuiScreenEditFilters setWhitelist(@NotNull Material... whitelist);

    @NotNull
    GuiScreenEditFilters setBlacklist(@NotNull Material... blacklist);

    @NotNull
    Set<Material> getWhitelist();

    @NotNull
    Set<Material> getBlacklist();

    boolean canInteractWith(@NotNull Material material);

}
