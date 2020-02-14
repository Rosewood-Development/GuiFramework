package dev.esophose.guiframework.gui.listeners;

import dev.esophose.guiframework.GuiFramework;
import dev.esophose.guiframework.gui.ClickAction;
import dev.esophose.guiframework.gui.GuiButton;
import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.manager.GuiManager;
import dev.esophose.guiframework.gui.screen.GuiScreen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    private GuiManager guiManager;

    public InventoryListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        Player player = (Player) event.getWhoClicked();

        GuiScreen clickedScreen = null;
        for (GuiContainer container : this.guiManager.getActiveGuis()) {
            for (GuiScreen screen : container.getScreens()) {
                if (screen.containsInventory(inventory)) {
                    clickedScreen = screen;
                    break;
                }
            }
        }

        if (clickedScreen == null)
            return;

        event.setCancelled(true);

        if (inventory != event.getClickedInventory())
            return;

        GuiButton clickedButton = clickedScreen.getButtonOnInventoryPage(inventory, event.getSlot());
        if (clickedButton == null)
            return;

        ClickAction clickAction = clickedButton.click(event);

        switch (clickAction) {
            case CLOSE:
                player.closeInventory();
                break;
            case PAGE_BACKWARDS:
                clickedScreen.getParentContainer().pageBackwards(player);
                break;
            case PAGE_FORWARDS:
                clickedScreen.getParentContainer().pageForwards(player);
                break;
            case TRANSITION_BACKWARDS:
                break;
            case TRANSITION_FORWARDS:
                break;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Bukkit.getScheduler().runTask(GuiFramework.getInstance().getHookedPlugin(), () ->
                this.runClose((Player) event.getPlayer(), event.getInventory()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getOpenInventory().getType() == InventoryType.CRAFTING)
            return;

        this.runClose(event.getPlayer(), event.getPlayer().getOpenInventory().getTopInventory());
    }

    /**
     * Runs whenever a player is potentially exiting an inventory we are keeping track of
     *
     * @param player The player who potentially closed a gui container
     * @param inventory The inventory potentially contained
     */
    private void runClose(Player player, Inventory inventory) {
        GuiContainer eventContainer = this.getGuiContainer(inventory);
        GuiContainer playerContainer = this.getGuiContainer(player.getOpenInventory().getTopInventory());
        if (eventContainer == null || playerContainer != null)
            return;

        eventContainer.runCloseFor(player);

        if (!eventContainer.isPersistent() && !eventContainer.hasViewers())
            this.guiManager.unregisterGui(eventContainer);
    }

    /**
     * Gets the parent of the given Inventory
     *
     * @param inventory The Inventory to get the container of
     * @return The containing GuiContainer, or null if none found
     */
    private GuiContainer getGuiContainer(Inventory inventory) {
        for (GuiContainer container : this.guiManager.getActiveGuis())
            for (GuiScreen screen : container.getScreens())
                if (screen.containsInventory(inventory))
                    return container;
        return null;
    }

}
