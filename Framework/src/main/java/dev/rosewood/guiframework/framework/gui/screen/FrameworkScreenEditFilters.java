package dev.rosewood.guiframework.framework.gui.screen;

import dev.rosewood.guiframework.gui.screen.GuiScreenEditFilters;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;

public class FrameworkScreenEditFilters implements GuiScreenEditFilters {

    private EnumSet<Material> whitelist;
    private EnumSet<Material> blacklist;

    public FrameworkScreenEditFilters() {
        this.whitelist = EnumSet.noneOf(Material.class);
        this.blacklist = EnumSet.noneOf(Material.class);
    }

    @Override
    public FrameworkScreenEditFilters setWhitelist(Material... whitelist) {
        this.whitelist.clear();
        this.whitelist.addAll(Arrays.asList(whitelist));

        return this;
    }

    @Override
    public FrameworkScreenEditFilters setBlacklist(Material... blacklist) {
        this.blacklist.clear();
        this.blacklist.addAll(Arrays.asList(blacklist));

        return this;
    }

    @Override
    public Set<Material> getWhitelist() {
        return this.whitelist;
    }

    @Override
    public Set<Material> getBlacklist() {
        return this.blacklist;
    }

    @Override
    public boolean canInteractWith(Material material) {
        if (this.blacklist.contains(material))
            return false;

        return this.whitelist.isEmpty() || this.whitelist.contains(material);
    }

}
