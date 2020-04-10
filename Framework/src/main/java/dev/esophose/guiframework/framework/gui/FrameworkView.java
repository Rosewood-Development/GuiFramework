package dev.esophose.guiframework.framework.gui;

import dev.esophose.guiframework.framework.gui.screen.FrameworkScreen;
import dev.esophose.guiframework.gui.GuiView;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FrameworkView implements GuiView {

    private UUID viewerUniqueId;
    private FrameworkScreen viewingScreen;
    private int viewingPage;

    public FrameworkView(@NotNull UUID viewerUniqueId, @NotNull FrameworkScreen viewingScreen, int viewingPage) {
        this.viewerUniqueId = viewerUniqueId;
        this.viewingScreen = viewingScreen;
        this.viewingPage = viewingPage;
    }

    /**
     * Sets the screen of this view
     *
     * @param viewingScreen The new screen of this view
     */
    public void setViewingScreen(@NotNull FrameworkScreen viewingScreen) {
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

    @Override
    public Player getViewer() {
        Player player = Bukkit.getPlayer(this.viewerUniqueId);
        if (player == null)
            throw new IllegalStateException("Player unexpectedly went offline.");

        return player;
    }

    @Override
    public UUID getViewerUniqueId() {
        return this.viewerUniqueId;
    }

    @Override
    public FrameworkScreen getViewingScreen() {
        return this.viewingScreen;
    }

    @Override
    public int getViewingPage() {
        return this.viewingPage;
    }

}
