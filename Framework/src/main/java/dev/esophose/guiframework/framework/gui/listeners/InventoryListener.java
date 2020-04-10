package dev.esophose.guiframework.framework.gui.listeners;

import dev.esophose.guiframework.GuiFramework;
import dev.esophose.guiframework.framework.gui.FrameworkButton;
import dev.esophose.guiframework.framework.gui.FrameworkContainer;
import dev.esophose.guiframework.framework.gui.manager.FrameworkManager;
import dev.esophose.guiframework.framework.gui.screen.FrameworkScreen;
import dev.esophose.guiframework.framework.gui.screen.FrameworkScreenEditFilters;
import dev.esophose.guiframework.framework.gui.screen.FrameworkScreenSection;
import dev.esophose.guiframework.gui.ClickAction;
import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.screen.GuiScreen;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private FrameworkManager guiManager;
    private List<ClickType> validEditClickTypes, validButtonClickTypes;
    private List<InventoryAction> validEditInventoryActions, validButtonInventoryActions;

    public InventoryListener(FrameworkManager guiManager) {
        this.guiManager = guiManager;
        this.validEditClickTypes = Arrays.asList(ClickType.CONTROL_DROP, ClickType.CREATIVE, ClickType.DOUBLE_CLICK, ClickType.DROP, ClickType.LEFT, ClickType.MIDDLE, ClickType.NUMBER_KEY, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
        this.validButtonClickTypes = Arrays.asList(ClickType.LEFT, ClickType.MIDDLE, ClickType.RIGHT);
        this.validEditInventoryActions = Arrays.asList(InventoryAction.CLONE_STACK, InventoryAction.DROP_ALL_CURSOR, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ONE_SLOT, InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.SWAP_WITH_CURSOR);
        this.validButtonInventoryActions = Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME);
    }

    @EventHandler
    public void onInventoryItemDrag(InventoryDragEvent event) {
        // Make sure drags are only a part of either the player's inventory or the editable section of the gui
        Inventory inventory = event.getView().getTopInventory();
        FrameworkContainer clickedContainer = this.getGuiContainer(inventory);
        FrameworkScreen clickedScreen = this.getGuiScreen(inventory);
        if (clickedContainer == null || clickedScreen == null || !(event.getWhoClicked() instanceof Player))
            return;

        FrameworkScreenSection editableSection = clickedScreen.getEditableSection();
        if (editableSection != null) {
            // Check filters
            Material draggedType = event.getOldCursor().getType();
            FrameworkScreenEditFilters filters = clickedScreen.getEditFilters();
            if (filters != null && !filters.canInteractWith(draggedType))
                event.setCancelled(true);

            // Check if we dragged into non-editable slots
            InventoryView view = event.getView();
            Map<Integer, ItemStack> newItems = event.getNewItems();
            Map<Integer, ItemStack> rejectedItems = new HashMap<>();
            for (int slot : newItems.keySet()) {
                if (view.getInventory(slot) == view.getTopInventory() && !editableSection.containsSlot(slot)) {
                    ItemStack oldItem = view.getItem(slot);
                    if (oldItem == null)
                        oldItem = new ItemStack(draggedType, 0);
                    ItemStack newItem = newItems.get(slot);
                    ItemStack adjustedItem = newItem.clone();
                    adjustedItem.setAmount(newItem.getAmount() - oldItem.getAmount());
                    rejectedItems.put(slot, adjustedItem);
                }
            }

            int totalRejected = rejectedItems.values().stream().mapToInt(ItemStack::getAmount).sum();
            if (totalRejected > 0) {
                // Recreate the event ourselves, except exclude the non-editable slots
                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();

                // Put rejected items back onto the cursor
                Bukkit.getScheduler().runTask(GuiFramework.getInstance().getHookedPlugin(), () -> {
                    ItemStack newCursor = event.getCursor();
                    if (newCursor == null || newCursor.getType() == Material.AIR)
                        newCursor = new ItemStack(draggedType, 0);
                    player.setItemOnCursor(new ItemStack(draggedType, newCursor.getAmount() + totalRejected));
                });

                // Adjust the edit slot amounts
                newItems.keySet().stream().filter(x -> !rejectedItems.containsKey(x)).forEach(slot -> {
                    ItemStack additive = newItems.get(slot);
                    ItemStack original = view.getItem(slot);
                    if (original == null || original.getType() == Material.AIR) {
                        view.setItem(slot, new ItemStack(draggedType, additive.getAmount()));
                    } else {
                        original = original.clone();
                        original.setAmount(original.getAmount() + additive.getAmount());
                        view.setItem(slot, original);
                    }
                });
            }
        } else {
            boolean draggedIntoTop = event.getInventorySlots().stream().anyMatch(x -> event.getView().getSlotType(x) == SlotType.CONTAINER);
            if (draggedIntoTop)
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        Inventory bottomInventory = event.getView().getBottomInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(event.getWhoClicked() instanceof Player))
            return;

        FrameworkContainer clickedContainer = this.getGuiContainer(inventory);
        FrameworkScreen clickedScreen = this.getGuiScreen(inventory);
        if (clickedContainer == null || clickedScreen == null)
            return;

        // Handle shift clicking from the bottom inventory to the top
        FrameworkScreenSection editableSection = clickedScreen.getEditableSection();
        if (clickedInventory == bottomInventory && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack movingItemStack = bottomInventory.getItem(event.getSlot());
            if (editableSection == null || movingItemStack == null || movingItemStack.getType() == Material.AIR) {
                event.setCancelled(true);
                return;
            }

            // Don't allow moving if it gets filtered
            Material type = movingItemStack.getType();
            FrameworkScreenEditFilters editFilters = clickedScreen.getEditFilters();
            if (editFilters != null && !editFilters.canInteractWith(type)) {
                event.setCancelled(true);
                return;
            }

            // Move the items to the top inventory
            int maxStackSize = movingItemStack.getMaxStackSize();
            int totalRemaining = movingItemStack.getAmount();
            for (int slot : editableSection.getSlots()) {
                ItemStack slotItemStack = inventory.getItem(slot);
                if (slotItemStack == null || slotItemStack.getType() == Material.AIR) {
                    slotItemStack = new ItemStack(type, totalRemaining);
                    totalRemaining = 0;
                } else if (slotItemStack.getType() == type) {
                    int amountToMove = Math.min(totalRemaining, maxStackSize - slotItemStack.getAmount());
                    slotItemStack.setAmount(slotItemStack.getAmount() + amountToMove);
                    totalRemaining -= amountToMove;
                } else continue;
                inventory.setItem(slot, slotItemStack);

                if (totalRemaining == 0)
                    break;
            }

            // Adjust the ItemStack that actually got moved
            if (totalRemaining > 0) {
                movingItemStack.setAmount(totalRemaining);
                bottomInventory.setItem(event.getSlot(), movingItemStack);
            } else {
                bottomInventory.setItem(event.getSlot(), null);
            }

            event.setCancelled(true);
            return;
        }

        if (clickedInventory != inventory)
            return;

        if (editableSection != null && editableSection.containsSlot(event.getSlot())) {
            // Validate the click action used was valid
            if (!this.validEditClickTypes.contains(event.getClick()) || !this.validEditInventoryActions.contains(event.getAction())) {
                event.setCancelled(true);
                return;
            }

            // Validate they can actually move the item type they tried to interact with
            FrameworkScreenEditFilters editFilters = clickedScreen.getEditFilters();
            if (editFilters != null) {
                ItemStack cursor = event.getCursor();
                ItemStack current = event.getCurrentItem();

                boolean filtered = false;
                if (cursor != null) {
                    Material type = cursor.getType();
                    if (type != Material.AIR && !editFilters.canInteractWith(type))
                        filtered = true;
                }

                if (!filtered && current != null) {
                    Material type = current.getType();
                    if (type != Material.AIR && !editFilters.canInteractWith(type))
                        filtered = true;
                }

                if (filtered) {
                    event.setCancelled(true);
                    return;
                }
            }

            return;
        } else {
            event.setCancelled(true);
            if (!this.validButtonClickTypes.contains(event.getClick()) || !this.validButtonInventoryActions.contains(event.getAction()))
                return;
        }

        FrameworkButton clickedButton = clickedScreen.getButtonOnInventoryPage(inventory, event.getSlot());
        if (clickedButton == null || !clickedScreen.isButtonOnInventoryPageVisible(inventory, event.getSlot()))
            return;

        Player player = (Player) event.getWhoClicked();
        ClickAction clickAction = clickedButton.click(event);
        switch (clickAction) {
            case REFRESH:
                clickedScreen.updateInventories();
                break;
            case CLOSE:
                player.closeInventory();
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
        FrameworkContainer eventContainer = this.getGuiContainer(inventory);
        FrameworkContainer playerContainer = this.getGuiContainer(player.getOpenInventory().getTopInventory());
        if (eventContainer == null || playerContainer != null)
            return;

        eventContainer.runCloseFor(player);

        if (!eventContainer.isPersistent() && !eventContainer.hasViewers())
            this.guiManager.unregisterGui(eventContainer);
    }

    /**
     * Gets the parent container of the given Inventory
     *
     * @param inventory The Inventory to get the container of
     * @return The containing GuiContainer, or null if none found
     */
    private FrameworkContainer getGuiContainer(Inventory inventory) {
        for (GuiContainer container : this.guiManager.getActiveGuis())
            for (GuiScreen screen : container.getScreens())
                if (((FrameworkScreen) screen).containsInventory(inventory))
                    return (FrameworkContainer) container;
        return null;
    }

    /**
     * Gets the parent screen of the given Inventory
     *
     * @param inventory The Inventory to get the container of
     * @return The containing GuiScreen, or null if none found
     */
    private FrameworkScreen getGuiScreen(Inventory inventory) {
        for (GuiContainer container : this.guiManager.getActiveGuis())
            for (GuiScreen screen : container.getScreens())
                if (((FrameworkScreen) screen).containsInventory(inventory))
                    return (FrameworkScreen) screen;
        return null;
    }

}
