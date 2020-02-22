package dev.esophose.guiframework.gui.listeners;

import dev.esophose.guiframework.GuiFramework;
import dev.esophose.guiframework.gui.ClickAction;
import dev.esophose.guiframework.gui.GuiButton;
import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.GuiView;
import dev.esophose.guiframework.gui.manager.GuiManager;
import dev.esophose.guiframework.gui.screen.GuiScreen;
import dev.esophose.guiframework.gui.screen.GuiScreenSection;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    private GuiManager guiManager;
    private List<ClickType> validEditClickTypes = Arrays.asList(ClickType.CONTROL_DROP, ClickType.DOUBLE_CLICK, ClickType.DROP, ClickType.LEFT, ClickType.MIDDLE, ClickType.NUMBER_KEY, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
    private List<ClickType> validButtonClickTypes = Arrays.asList(ClickType.LEFT, ClickType.MIDDLE, ClickType.RIGHT);
    private List<InventoryAction> validEditInventoryActions = Arrays.asList(InventoryAction.DROP_ALL_CURSOR, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ONE_SLOT, InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.SWAP_WITH_CURSOR);
    private List<InventoryAction> validButtonInventoryActions = Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME);

    public InventoryListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        Player player = (Player) event.getWhoClicked();

        GuiScreen clickedScreen = null;
        GuiContainer clickedContainer = null;
        for (GuiContainer container : this.guiManager.getActiveGuis()) {
            for (GuiScreen screen : container.getScreens()) {
                if (screen.containsInventory(inventory)) {
                    clickedScreen = screen;
                    clickedContainer = container;
                    break;
                }
            }
        }

        if (clickedScreen == null)
            return;

        GuiScreenSection editableSection = clickedScreen.getEditableSection();
        if (editableSection != null && editableSection.getSlots().contains(event.getSlot())) {
            // TODO: Validate only moving specific items
            if (!this.validEditClickTypes.contains(event.getClick()) || !this.validEditInventoryActions.contains(event.getAction())) {
                event.setCancelled(true);
                return;
            }
        } else {
            event.setCancelled(true);
            if (inventory != event.getClickedInventory() || !this.validButtonClickTypes.contains(event.getClick()) || !this.validButtonInventoryActions.contains(event.getAction()))
                return;
        }

        GuiButton clickedButton = clickedScreen.getButtonOnInventoryPage(inventory, event.getSlot());
        if (clickedButton == null || !clickedScreen.isButtonOnInventoryPageVisible(inventory, event.getSlot()))
            return;

        ClickAction clickAction = clickedButton.click(event);

        switch (clickAction) {
            case CLOSE:
                player.closeInventory();
                break;
            case REFRESH:
                clickedContainer.getCurrentViewers().values().forEach(GuiView::refresh);
                break;
            case PAGE_FIRST:
                clickedContainer.firstPage(player);
                break;
            case PAGE_BACKWARDS:
                clickedContainer.pageBackwards(player);
                break;
            case PAGE_FORWARDS:
                clickedContainer.pageForwards(player);
                break;
            case PAGE_LAST:
                clickedContainer.lastPage(player);
                break;
            case TRANSITION_BACKWARDS:
                clickedContainer.transitionBackwards(player);
                break;
            case TRANSITION_FORWARDS:
                clickedContainer.transitionForwards(player);
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
