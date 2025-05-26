package dev.rosewood.guiframework.framework.gui.listeners;

import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.adapter.InventoryViewAdapter;
import dev.rosewood.guiframework.adapter.InventoryViewHandler;
import dev.rosewood.guiframework.adapter.InventoryViewWrapper;
import dev.rosewood.guiframework.framework.gui.FrameworkButton;
import dev.rosewood.guiframework.framework.gui.FrameworkContainer;
import dev.rosewood.guiframework.framework.gui.manager.FrameworkManager;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkScreen;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkScreenEditFilters;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkScreenSection;
import dev.rosewood.guiframework.gui.ClickAction;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final FrameworkManager guiManager;
    private final List<ClickType> validEditClickTypes, validButtonClickTypes;
    private final List<InventoryAction> validEditInventoryActions, validButtonInventoryActions;
    private final InventoryViewHandler inventoryViewAdapter;

    public InventoryListener(FrameworkManager guiManager) {
        this.guiManager = guiManager;
        this.validEditClickTypes = Arrays.asList(ClickType.CONTROL_DROP, ClickType.CREATIVE, ClickType.DOUBLE_CLICK, ClickType.DROP, ClickType.LEFT, ClickType.MIDDLE, ClickType.NUMBER_KEY, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
        this.validButtonClickTypes = Arrays.asList(ClickType.LEFT, ClickType.MIDDLE, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
        this.validEditInventoryActions = Arrays.asList(InventoryAction.CLONE_STACK, InventoryAction.DROP_ALL_CURSOR, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ONE_SLOT, InventoryAction.MOVE_TO_OTHER_INVENTORY, InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.SWAP_WITH_CURSOR);
        this.validButtonInventoryActions = Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.MOVE_TO_OTHER_INVENTORY);
        this.inventoryViewAdapter = InventoryViewAdapter.getHandler();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryItemDrag(InventoryDragEvent event) {
        // Make sure drags are only a part of either the player's inventory or the editable section of the gui
        Inventory inventory = this.inventoryViewAdapter.getView(event).getTopInventory();
        FrameworkContainer clickedContainer = this.getGuiContainer(inventory);
        FrameworkScreen clickedScreen = this.getGuiScreen(inventory);
        if (clickedContainer == null || clickedScreen == null || !(event.getWhoClicked() instanceof Player))
            return;

        FrameworkScreenSection editableSection = clickedScreen.getEditableSection();
        if (editableSection != null) {
            // Check filters
            Material draggedType = event.getOldCursor().getType();
            FrameworkScreenEditFilters filters = clickedScreen.getEditFilters();
            if (filters != null && !filters.canInteractWith(event.getOldCursor()))
                event.setCancelled(true);

            // Check if we dragged into non-editable slots
            InventoryViewWrapper view = this.inventoryViewAdapter.getView(event);
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
            boolean draggedIntoTop = event.getInventorySlots().stream().anyMatch(x -> this.inventoryViewAdapter.getView(event).getSlotType(x) == SlotType.CONTAINER);
            if (draggedIntoTop)
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryViewWrapper view = this.inventoryViewAdapter.getView(event);
        Inventory topInventory = view.getTopInventory();
        Inventory bottomInventory = view.getBottomInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(event.getWhoClicked() instanceof Player))
            return;

        FrameworkContainer clickedContainer = this.getGuiContainer(topInventory);
        FrameworkScreen clickedScreen = this.getGuiScreen(topInventory);
        if (clickedContainer == null || clickedScreen == null)
            return;

        if (clickedContainer.preventsItemDropping() && (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP)) {
            event.setCancelled(true);
            return;
        }

        // Handle shift clicking from the bottom inventory to the top
        FrameworkScreenSection editableSection = clickedScreen.getEditableSection();
        if (clickedInventory == bottomInventory && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack movingItemStack = bottomInventory.getItem(event.getSlot());
            if (editableSection == null || movingItemStack == null || movingItemStack.getType() == Material.AIR) {
                event.setCancelled(true);
                return;
            }

            // Don't allow moving if it gets filtered
            FrameworkScreenEditFilters editFilters = clickedScreen.getEditFilters();
            if (editFilters != null && !editFilters.canInteractWith(movingItemStack)) {
                event.setCancelled(true);
                return;
            }

            // Move the items to the top inventory
            int maxStackSize = movingItemStack.getMaxStackSize();
            int totalRemaining = movingItemStack.getAmount();
            for (int slot : editableSection.getSlots()) {
                ItemStack slotItemStack = topInventory.getItem(slot);
                if (slotItemStack == null || slotItemStack.getType() == Material.AIR) {
                    slotItemStack = movingItemStack.clone();
                    slotItemStack.setAmount(totalRemaining);
                    totalRemaining = 0;
                } else if (slotItemStack.isSimilar(movingItemStack)) {
                    int amountToMove = Math.min(totalRemaining, maxStackSize - slotItemStack.getAmount());
                    slotItemStack.setAmount(slotItemStack.getAmount() + amountToMove);
                    totalRemaining -= amountToMove;
                } else continue;
                topInventory.setItem(slot, slotItemStack);

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

        if (clickedInventory != topInventory)
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
                    if (type != Material.AIR && !editFilters.canInteractWith(cursor))
                        filtered = true;
                }

                if (!filtered && current != null) {
                    Material type = current.getType();
                    if (type != Material.AIR && !editFilters.canInteractWith(current))
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

        FrameworkButton clickedButton = clickedScreen.getButtonOnInventoryPage(topInventory, event.getSlot());
        if (clickedButton == null || !clickedScreen.isButtonOnInventoryPageVisible(topInventory, event.getSlot()))
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
        this.forceCloseActiveGuis(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.forceCloseActiveGuis(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Inventory inventory = this.inventoryViewAdapter.getOpenInventory(event.getPlayer()).getTopInventory();
        FrameworkContainer openContainer = this.getGuiContainer(inventory);
        if (openContainer != null && openContainer.preventsItemDropping())
            event.setCancelled(true);
    }

    /**
     * Remove the player from any containers they may be viewing
     *
     * @param player The player to remove
     */
    private void forceCloseActiveGuis(Player player) {
        List<FrameworkContainer> orphanedContainers = this.guiManager.getActiveGuis().stream()
                .filter(x -> x instanceof FrameworkContainer)
                .map(x -> (FrameworkContainer) x)
                .filter(x -> x.getCurrentViewers().containsKey(player.getUniqueId()))
                .collect(Collectors.toList());

        for (FrameworkContainer container : orphanedContainers) {
            container.runCloseFor(player);
            if (!container.isPersistent() && !container.hasViewers())
                this.guiManager.unregisterGui(container);
        }

        if (!orphanedContainers.isEmpty())
            player.closeInventory();
    }

    /**
     * Runs whenever a player is potentially exiting an inventory we are keeping track of
     *
     * @param player The player who potentially closed a gui container
     * @param inventory The inventory potentially contained
     */
    private void runClose(Player player, Inventory inventory) {
        FrameworkContainer eventContainer = this.getGuiContainer(inventory);
        FrameworkContainer playerContainer = this.getGuiContainer(this.inventoryViewAdapter.getOpenInventory(player).getTopInventory());
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
