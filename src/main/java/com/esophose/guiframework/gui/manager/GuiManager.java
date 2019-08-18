package com.esophose.guiframework.gui.manager;

import com.esophose.guiframework.gui.GuiContainer;
import com.esophose.guiframework.gui.ITickable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GuiManager implements ITickable {

    private Set<GuiContainer> activeGuis;

    public GuiManager(@NotNull Plugin plugin) {
        this.activeGuis = new HashSet<>();

        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0L, 1L);
    }

    @Override
    public void tick() {
        this.activeGuis.forEach(GuiContainer::tick);
    }

    /**
     * Registers a new gui
     *
     * @param gui The new gui to register
     */
    public void registerGui(@NotNull GuiContainer gui) {
        this.activeGuis.add(gui);
    }

    /**
     * Unregisters a gui
     *
     * @param gui The gui to unregister
     */
    public void unregisterGui(@NotNull GuiContainer gui) {
        this.activeGuis.remove(gui);
    }

    /**
     * Gets an unmodifiable set of the active gui containers
     *
     * @return A set of the active gui containers
     */
    @NotNull
    public Set<GuiContainer> getActiveGuis() {
        return Collections.unmodifiableSet(this.activeGuis);
    }

}
