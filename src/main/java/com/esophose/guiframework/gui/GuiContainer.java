package com.esophose.guiframework.gui;

import com.esophose.guiframework.gui.screen.GuiScreen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiContainer implements ITickable {

    private Map<UUID, GuiView> currentViewers;
    private Map<Integer, GuiScreen> screens;
    private boolean persistent;
    private int tickRate, currentTick;

    public GuiContainer() {
        this.currentViewers = new HashMap<>();
        this.screens = new HashMap<>();
        this.persistent = false;
        this.tickRate = -1;
        this.currentTick = 0;
    }

    @NotNull
    public GuiContainer setPersistent(boolean persistent) {
        this.persistent = persistent;

        return this;
    }

    @NotNull
    public GuiContainer setTickRate(int tickRate) {
        this.tickRate = tickRate;

        return this;
    }

    /**
     * Opens the Gui for a player at a specific page number
     *
     * @param player The player to open the Gui for
     * @param screenNumber The screen number to open at
     */
    public void openFor(@NotNull Player player, int screenNumber) {
        GuiScreen screen = this.screens.get(screenNumber);
        if (screen == null)
            throw new IndexOutOfBoundsException("A screen with the id " + screenNumber + " does not exist.");

        this.currentViewers.put(player.getUniqueId(), new GuiView(player.getUniqueId(), screen, 1));
        player.openInventory(screen.getInventory(1));
    }

    /**
     * Opens the Gui for a player at the first screen
     *
     * @param player The player to open the Gui for
     */
    public void openFor(@NotNull Player player) {
        this.openFor(player, 0);
    }

    /**
     * Marks the Gui as closed for a player.
     * Note: Does not close the inventory. Close it yourself.
     *
     * @param player The player to close the Gui for
     */
    public void runCloseFor(@NotNull Player player) {
        this.currentViewers.remove(player.getUniqueId());
    }

    public void pageBackwards(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        GuiView view = this.currentViewers.get(player.getUniqueId());
        if (view.getViewingPage() == 1)
            return;

        view.setViewingPage(view.getViewingPage() - 1);
        player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void pageForwards(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        GuiView view = this.currentViewers.get(player.getUniqueId());
        if (!view.getViewingScreen().hasNextPage(view.getViewingPage()))
            return;

        view.setViewingPage(view.getViewingPage() + 1);
        player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    /**
     * Adds a screen at the highest screen number plus one
     *
     * @param screen The screen to add
     */
    public void addScreen(@NotNull GuiScreen screen) {
        this.screens.put(this.screens.size(), screen);
    }

    @Override
    public void tick() {
        if (this.tickRate == -1)
            return;

        this.currentTick++;
        if (this.currentTick == this.tickRate) {
            this.currentTick = 0;
            this.screens.values().forEach(GuiScreen::tick);
        }
    }

    /**
     * Gets an unmodifyable map of all screens in the container
     * The key is the screen number
     *
     * @return A map of all screens in the container
     */
    @NotNull
    public Map<Integer, GuiScreen> getNumberedScreens() {
        return Collections.unmodifiableMap(this.screens);
    }

    /**
     * Gets an unmodifyable collection of all screens in the container
     *
     * @return A collection of all screens in the container
     */
    @NotNull
    public Collection<GuiScreen> getScreens() {
        return Collections.unmodifiableCollection(this.screens.values());
    }

    /**
     * Checks if the container is persistent and will not be destroyed when all players have stopped viewing it
     *
     * @return true if the container is persistent, otherwise false
     */
    public boolean isPersistent() {
        return this.persistent;
    }

    /**
     * @return the current viewers of this container
     */
    @NotNull
    public Map<UUID, GuiView> getCurrentViewers() {
        return Collections.unmodifiableMap(this.currentViewers);
    }

    /**
     * @return the number of players currently viewing this GUI
     */
    public int getTotalViewers() {
        return this.currentViewers.size();
    }

    /**
     * @return true if any players are viewing this gui, otherwise false
     */
    public boolean hasViewers() {
        return !this.currentViewers.isEmpty();
    }

}
