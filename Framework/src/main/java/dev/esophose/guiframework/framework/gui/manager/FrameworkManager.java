package dev.esophose.guiframework.framework.gui.manager;

import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.manager.GuiManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class FrameworkManager implements GuiManager {

    private Set<GuiContainer> activeGuis;

    public FrameworkManager(Plugin plugin) {
        this.activeGuis = new HashSet<>();

        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0L, 1L);
    }

    @Override
    public void tick() {
        this.activeGuis.forEach(GuiContainer::tick);
    }

    @Override
    public void registerGui(GuiContainer gui) {
        this.activeGuis.add(gui);
    }

    @Override
    public void unregisterGui(GuiContainer gui) {
        this.activeGuis.remove(gui);
    }

    @Override
    public Set<GuiContainer> getActiveGuis() {
        return Collections.unmodifiableSet(this.activeGuis);
    }

}
