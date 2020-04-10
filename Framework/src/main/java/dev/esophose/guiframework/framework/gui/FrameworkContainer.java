package dev.esophose.guiframework.framework.gui;

import dev.esophose.guiframework.framework.gui.screen.FrameworkScreen;
import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.GuiView;
import dev.esophose.guiframework.gui.screen.GuiScreen;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FrameworkContainer implements GuiContainer {

    private Map<UUID, FrameworkView> currentViewers;
    private Map<Integer, GuiScreen> screens;
    private boolean persistent;
    private int tickRate, currentTick;

    public FrameworkContainer() {
        this.currentViewers = new HashMap<>();
        this.screens = new HashMap<>();
        this.persistent = false;
        this.tickRate = -1;
        this.currentTick = 0;
    }

    @Override
    public FrameworkContainer setPersistent(boolean persistent) {
        this.persistent = persistent;

        return this;
    }

    @Override
    public FrameworkContainer setTickRate(int tickRate) {
        this.tickRate = tickRate;

        return this;
    }

    /**
     * Adds a screen at the highest screen number plus one
     *
     * @param screen The screen to add
     */
    @Override
    public GuiContainer addScreen(GuiScreen screen) {
        this.screens.put(this.screens.size(), screen);

        return this;
    }

    /**
     * Opens the Gui for a player at a specific page number
     *
     * @param player The player to open the Gui for
     * @param screenNumber The screen number to open at
     */
    @Override
    public void openFor(Player player, int screenNumber) {
        FrameworkScreen screen = (FrameworkScreen) this.screens.get(screenNumber);
        if (screen == null)
            throw new IndexOutOfBoundsException("A screen with the id " + screenNumber + " does not exist.");

        this.currentViewers.put(player.getUniqueId(), new FrameworkView(player.getUniqueId(), screen, 1));
        player.openInventory(screen.getInventory(1));
    }

    /**
     * Opens the Gui for a player at the first screen
     *
     * @param player The player to open the Gui for
     */
    @Override
    public void openFor(Player player) {
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
        if (this.currentViewers.isEmpty())
            this.screens.values().stream().map(x -> (FrameworkScreen) x).forEach(x -> x.onViewersLeave(player));
    }

    public void firstPage(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (view.getViewingPage() == 1)
            return;

        view.setViewingPage(1);
        player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void pageBackwards(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (view.getViewingPage() == 1)
            return;

        view.setViewingPage(view.getViewingPage() - 1);
        player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void pageForwards(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (!view.getViewingScreen().hasNextPage(view.getViewingPage()))
            return;

        view.setViewingPage(view.getViewingPage() + 1);
        player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void lastPage(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (view.getViewingPage() == 1)
            return;

        view.setViewingPage(view.getViewingScreen().getMaximumPageNumber());
        player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void transitionForwards(@NotNull Player player) {
        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        for (int key : this.screens.keySet()) {
            FrameworkScreen screen = (FrameworkScreen) this.screens.get(key);
            if (screen == view.getViewingScreen()) {
                FrameworkScreen nextScreen = (FrameworkScreen) this.screens.get(key + 1);
                if (nextScreen != null) {
                    view.setViewingScreen(nextScreen);
                    view.setViewingPage(1);
                    player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
                }
                return;
            }
        }
    }

    public void transitionBackwards(@NotNull Player player) {
        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        for (int key : this.screens.keySet()) {
            FrameworkScreen screen = (FrameworkScreen) this.screens.get(key);
            if (screen == view.getViewingScreen()) {
                FrameworkScreen nextScreen = (FrameworkScreen) this.screens.get(key - 1);
                if (nextScreen != null) {
                    view.setViewingScreen(nextScreen);
                    view.setViewingPage(1);
                    player.openInventory(view.getViewingScreen().getInventory(view.getViewingPage()));
                }
                return;
            }
        }
    }

    @Override
    public void tick() {
        if (this.tickRate == -1)
            return;

        this.currentTick++;
        if (this.currentTick >= this.tickRate) {
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
    @Override
    public Map<Integer, GuiScreen> getNumberedScreens() {
        return Collections.unmodifiableMap(this.screens);
    }

    /**
     * Gets an unmodifyable collection of all screens in the container
     *
     * @return A collection of all screens in the container
     */
    @Override
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

    public int getTickRate() {
        return this.tickRate;
    }

    /**
     * @return the current viewers of this container
     */
    @Override
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

    /**
     * Closes the open inventory of all viewers
     */
    public void closeViewers() {
        this.currentViewers.values().stream().map(FrameworkView::getViewer).forEach(Player::closeInventory);
    }

}
