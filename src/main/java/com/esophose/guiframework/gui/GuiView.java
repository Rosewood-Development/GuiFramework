package com.esophose.guiframework.gui;

import com.esophose.guiframework.gui.screen.GuiScreen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GuiView {

    private UUID viewerUniqueId;
    private GuiScreen viewingScreen;
    private int viewingPage;

    public GuiView(@NotNull UUID viewerUniqueId, @NotNull GuiScreen viewingScreen, int viewingPage) {
        this.viewerUniqueId = viewerUniqueId;
        this.viewingScreen = viewingScreen;
        this.viewingPage = viewingPage;
    }

    /**
     * Sets the screen of this view
     *
     * @param viewingScreen The new screen of this view
     */
    public void setViewingScreen(@NotNull GuiScreen viewingScreen) {
        this.viewingScreen = viewingScreen;
    }

    /**
     * Sets the page of this view
     *
     * @param viewingPage The new page of this view
     */
    public void setViewingPage(int viewingPage) {
        this.viewingPage = viewingPage;
    }

    /**
     * @return the Player viewing
     */
    @NotNull
    public Player getViewer() {
        Player player = Bukkit.getPlayer(this.viewerUniqueId);
        if (player == null)
            throw new IllegalStateException("Player unexpectedly went offline.");

        return player;
    }

    /**
     * @return the viewer's UUID
     */
    @NotNull
    public UUID getViewerUniqueId() {
        return this.viewerUniqueId;
    }

    /**
     * @return the screen of this view
     */
    @NotNull
    public GuiScreen getViewingScreen() {
        return this.viewingScreen;
    }

    /**
     * @return the page of the screen being viewed
     */
    public int getViewingPage() {
        return this.viewingPage;
    }

}
