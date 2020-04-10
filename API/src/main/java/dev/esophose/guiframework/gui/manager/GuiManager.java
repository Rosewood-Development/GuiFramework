package dev.esophose.guiframework.gui.manager;

import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.Tickable;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public interface GuiManager extends Tickable {

    /**
     * Registers a new gui
     *
     * @param gui The new gui to register
     */
    void registerGui(@NotNull GuiContainer gui);

    /**
     * Unregisters a gui
     *
     * @param gui The gui to unregister
     */
    void unregisterGui(@NotNull GuiContainer gui);

    /**
     * Gets an unmodifiable set of the active gui containers
     *
     * @return A set of the active gui containers
     */
    @NotNull
    Set<GuiContainer> getActiveGuis();

}
