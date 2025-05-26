package dev.rosewood.guiframework.framework.gui;

import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.adapter.InventoryViewAdapter;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkScreen;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiView;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FrameworkContainer implements GuiContainer {

    private final Map<UUID, FrameworkView> currentViewers;
    private final Map<Integer, GuiScreen> screens;
    private boolean persistent, preventItemDropping;
    private int tickRate, currentTick;

    public FrameworkContainer() {
        this.currentViewers = new HashMap<>();
        this.screens = new HashMap<>();
        this.persistent = false;
        this.preventItemDropping = false;
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

    @Override
    public GuiContainer preventItemDropping(boolean disable) {
        this.preventItemDropping = disable;

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

        // Remove the player from any other containers they may already be viewing
        GuiFramework.getInstance().getGuiManager().getActiveGuis().stream()
                .filter(x -> x instanceof FrameworkContainer)
                .map(x -> (FrameworkContainer) x)
                .filter(x -> x != this)
                .filter(x -> x.getCurrentViewers().containsKey(player.getUniqueId()))
                .forEach(x -> x.runCloseFor(player));

        this.currentViewers.put(player.getUniqueId(), new FrameworkView(player.getUniqueId(), screen, 1));
        InventoryViewAdapter.getHandler().openInventory(player, screen.getInventory(1));
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
        if (this.currentViewers.remove(player.getUniqueId()) != null && this.currentViewers.isEmpty())
            this.screens.values().stream().map(x -> (FrameworkScreen) x).forEach(x -> x.onViewersLeave(player));
    }

    public void firstPage(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (view.getViewingPage() == 1)
            return;

        view.setViewingPage(1);
        InventoryViewAdapter.getHandler().openInventory(player, view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void pageBackwards(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (view.getViewingPage() == 1)
            return;

        view.setViewingPage(view.getViewingPage() - 1);
        InventoryViewAdapter.getHandler().openInventory(player, view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void pageForwards(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (!view.getViewingScreen().hasNextPage(view.getViewingPage()))
            return;

        view.setViewingPage(view.getViewingPage() + 1);
        InventoryViewAdapter.getHandler().openInventory(player, view.getViewingScreen().getInventory(view.getViewingPage()));
    }

    public void lastPage(@NotNull Player player) {
        if (!this.currentViewers.containsKey(player.getUniqueId()))
            return;

        FrameworkView view = this.currentViewers.get(player.getUniqueId());
        if (view.getViewingPage() == view.getViewingScreen().getMaximumPageNumber())
            return;

        view.setViewingPage(view.getViewingScreen().getMaximumPageNumber());
        InventoryViewAdapter.getHandler().openInventory(player, view.getViewingScreen().getInventory(view.getViewingPage()));
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
                    InventoryViewAdapter.getHandler().openInventory(player, view.getViewingScreen().getInventory(view.getViewingPage()));
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
                    InventoryViewAdapter.getHandler().openInventory(player, view.getViewingScreen().getInventory(view.getViewingPage()));
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

    @Override
    public boolean preventsItemDropping() {
        return this.preventItemDropping;
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
        List<Player> viewers = this.currentViewers.values().stream().map(GuiView::getViewer).collect(Collectors.toList());
        viewers.forEach(viewer -> {
            viewer.closeInventory();
            this.runCloseFor(viewer);
        });
    }

}
