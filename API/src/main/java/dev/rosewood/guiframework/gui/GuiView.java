package dev.rosewood.guiframework.gui;

import dev.rosewood.guiframework.gui.screen.GuiScreen;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface GuiView {

    /**
     * @return the Player viewing
     */
    @NotNull
    Player getViewer();

    /**
     * @return the viewer's UUID
     */
    @NotNull
    UUID getViewerUniqueId();

    /**
     * @return the screen of this view
     */
    @NotNull
    GuiScreen getViewingScreen();

    /**
     * @return the page of the screen being viewed
     */
    int getViewingPage();

}
