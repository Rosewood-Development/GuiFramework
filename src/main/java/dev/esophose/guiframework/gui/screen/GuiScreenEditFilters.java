package dev.esophose.guiframework.gui.screen;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class GuiScreenEditFilters {

    private EnumSet<Material> whitelist;
    private EnumSet<Material> blacklist;

    public GuiScreenEditFilters() {
        this.whitelist = EnumSet.noneOf(Material.class);
        this.blacklist = EnumSet.noneOf(Material.class);
    }

    @NotNull
    public GuiScreenEditFilters setWhitelist(@NotNull Material... whitelist) {
        this.whitelist.clear();
        this.whitelist.addAll(Arrays.asList(whitelist));

        return this;
    }

    @NotNull
    public GuiScreenEditFilters setBlacklist(@NotNull Material... blacklist) {
        this.blacklist.clear();
        this.blacklist.addAll(Arrays.asList(blacklist));

        return this;
    }

    @NotNull
    public Set<Material> getWhitelist() {
        return this.whitelist;
    }

    @NotNull
    public Set<Material> getBlacklist() {
        return this.blacklist;
    }

    public boolean canInteractWith(Material material) {
        if (this.blacklist.contains(material))
            return false;

        return this.whitelist.isEmpty() || this.whitelist.contains(material);
    }

}
