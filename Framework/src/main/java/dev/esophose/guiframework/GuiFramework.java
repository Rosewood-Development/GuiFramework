package dev.esophose.guiframework;

import com.google.common.base.Preconditions;
import dev.esophose.guiframework.framework.gui.listeners.InventoryListener;
import dev.esophose.guiframework.framework.gui.manager.FrameworkManager;
import dev.esophose.guiframework.gui.manager.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class GuiFramework {

    private static GuiFramework INSTANCE;

    private FrameworkManager guiManager;
    private Plugin hookedPlugin;

    private GuiFramework(@NotNull Plugin plugin) {
        this.guiManager = new FrameworkManager(plugin);
        this.hookedPlugin = plugin;

        Bukkit.getPluginManager().registerEvents(new InventoryListener(this.guiManager), plugin);
    }

    /**
     * Gets the GuiManager instance
     *
     * @return The GuiManager instance
     */
    @NotNull
    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    /**
     * @return the Plugin that the GuiFramework has hooked into
     */
    @NotNull
    public Plugin getHookedPlugin() {
        return this.hookedPlugin;
    }

    /**
     * Creates and gets the instance of the GuiFramework.
     * Requires passing a Plugin instance so it can hook into the Bukkit API properly
     *
     * @param plugin The Plugin instance to hook with
     * @return The GuiFramework instance
     */
    @NotNull
    public static GuiFramework instantiate(@NotNull Plugin plugin) {
        if (INSTANCE == null)
            INSTANCE = new GuiFramework(plugin);

        return INSTANCE;
    }

    /**
     * Gets the instance of the GuiFramework.
     * Requires passing a Plugin instance so it can hook into the Bukkit API properly
     *
     * @return The GuiFramework instance
     */
    @NotNull
    public static GuiFramework getInstance() {
        Preconditions.checkNotNull(INSTANCE, "GuiFramework not yet instantiated. Must use GuiFramework.instantiate(Plugin) first.");

        return INSTANCE;
    }

}
