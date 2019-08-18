package com.esophose.guiframework.gui.screen;

import com.esophose.guiframework.gui.GuiButton;
import com.esophose.guiframework.gui.GuiContainer;
import com.esophose.guiframework.gui.GuiSize;
import com.esophose.guiframework.gui.GuiView;
import com.esophose.guiframework.gui.ITickable;
import com.esophose.guiframework.util.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiScreen implements ITickable {

    private GuiContainer parentContainer;
    private GuiSize size;
    private String title;
    private GuiScreenSection paginatedSection;
    private GuiScreenSection editableSection;
    private PageContentsRequester pageContentsRequester;
    private Map<Integer, GuiPageContentsResult> paginatedSlotCache;
    private Map<Integer, ISlotable> slots;
    private Map<Integer, Inventory> inventories;

    public GuiScreen(@NotNull GuiContainer parentContainer, @NotNull GuiSize size) {
        this.parentContainer = parentContainer;
        this.size = size;
        this.paginatedSlotCache = new HashMap<>();
        this.slots = new HashMap<>();
        this.inventories = new HashMap<>();
    }

    @NotNull
    public GuiScreen setTitle(@NotNull String title) {
        this.title = title;

        return this;
    }

    @NotNull
    public GuiScreen setPaginatedSection(int beginIndex, int endIndex, @NotNull PageContentsRequester pageContentsRequester) {
        this.paginatedSection = new GuiScreenSection(beginIndex, endIndex);
        this.pageContentsRequester = pageContentsRequester;

        return this;
    }

    @NotNull
    public GuiScreen setEditableSection(int beginIndex, int endIndex) {
        this.editableSection = new GuiScreenSection(beginIndex, endIndex);

        return this;
    }

    @NotNull
    public GuiScreen addItemStackAt(int slot, @NotNull ItemStack content) {
        this.slots.put(slot, new GuiItemStack(content));

        return this;
    }

    @NotNull
    public GuiScreen addItemStack(@NotNull ItemStack content) {
        int slot = this.slots.keySet().stream().max(Integer::compareTo).orElse(-1) + 1;
        this.addItemStackAt(slot, content);

        return this;
    }

    @NotNull
    public GuiScreen addButtonAt(int slot, @NotNull GuiButton button) {
        this.slots.put(slot, button);

        return this;
    }

    @NotNull
    public GuiScreen addButton(@NotNull GuiButton button) {
        int slot = this.slots.keySet().stream().max(Integer::compareTo).orElse(-1) + 1;
        this.addButtonAt(slot, button);

        return this;
    }

    @Override
    public void tick() {
        this.getButtons().values().forEach(GuiButton::tick);
        this.inventories.keySet().forEach(x -> this.getPageButtons(x).values().forEach(GuiButton::tick));
        this.updateInventories();
    }

    @NotNull
    public GuiContainer getParentContainer() {
        return this.parentContainer;
    }

    /**
     * Gets the title of the page
     *
     * @return The title of the page
     */
    @NotNull
    public String getTitle() {
        return this.title;
    }

    @Nullable
    public ISlotable getSlot(int index) {
        return this.slots.get(index);
    }

    @NotNull
    public Map<Integer, ISlotable> getSlots() {
        return this.slots;
    }

    /**
     * Gets a button on the screen given a target page and slot number
     *
     * @param inventory The Inventory to try to get the button on
     * @param slot The slot of the target button
     * @return a GuiButton in the given slot on the given inventory if one exists, otherwise null
     */
    @Nullable
    public GuiButton getButtonOnInventoryPage(@NotNull Inventory inventory, int slot) {
        for (int page : this.inventories.keySet())
            if (this.inventories.get(page) == inventory)
                return this.getButtons(page).get(slot);
        return null;
    }

    /**
     * Gets an unmodifyable map of the buttons on a specific page.
     * The key is the slot index.
     *
     * @param pageNumber The page to get buttons for
     * @return The buttons on the page
     */
    @NotNull
    public Map<Integer, GuiButton> getButtons(int pageNumber) {
        Map<Integer, GuiButton> buttons = this.getButtons();
        buttons.putAll(this.getPageButtons(pageNumber));
        return buttons;
    }

    /**
     * Gets an unmodifyable map of buttons that are part of the paginated section on a specific page.
     * The key is the slot index.
     *
     * @param pageNumber The page to get buttons for
     * @return The paginated section buttons on the page
     */
    @NotNull
    public Map<Integer, GuiButton> getPageButtons(int pageNumber) {
        Map<Integer, GuiButton> pageButtons = new HashMap<>();

        List<ISlotable> pageContents = this.paginatedSlotCache.get(pageNumber).getPageContents();
        List<Integer> slots = this.paginatedSection.getSlots();

        for (int i = 0; i < slots.size() && i < pageContents.size(); i++) {
            ISlotable slot = pageContents.get(i);
            if (slot instanceof GuiButton)
                pageButtons.put(slots.get(i), (GuiButton) slot);
        }

        return pageButtons;
    }

    @Nullable
    public 

    /**
     * Gets an unmodifyable map of the buttons on the screen.
     * Does not contain paginated buttons.
     * The key is the slot index.
     *
     * @return The buttons on the screen
     */
    @NotNull
    public Map<Integer, GuiButton> getButtons() {
        return this.slots.entrySet()
                .stream()
                .filter(x -> x.getValue() instanceof GuiButton)
                .map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), (GuiButton) x.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Checks if an inventory is contained within this screen
     *
     * @param inventory The inventory to check
     * @return true if the inventory is contained within this screen, otherwise false
     */
    public boolean containsInventory(Inventory inventory) {
        return this.inventories.values().contains(inventory);
    }

    /**
     * Gets the Inventory for a page on this screen
     *
     * @return The Inventory with the page's content
     */
    @NotNull
    public Inventory getInventory(int pageNumber) {
        if (this.inventories.containsKey(pageNumber))
            return this.inventories.get(pageNumber);

        Inventory inventory = this.createInventory(pageNumber);
        this.populateInventory(pageNumber, inventory);

        return inventory;
    }

    @NotNull
    public Inventory getFirstInventory() {
        return this.getInventory(1);
    }

    public boolean hasNextPage(int pageNumber) {
        return !this.paginatedSlotCache.get(pageNumber).isFinished();
    }

    private void updateInventories() {
        Set<Integer> pagesBeingViewed = this.parentContainer.getCurrentViewers()
                .values()
                .stream()
                .filter(x -> x.getViewingScreen() == this)
                .map(GuiView::getViewingPage)
                .collect(Collectors.toSet());

        for (int page : pagesBeingViewed) {
            Inventory inventory = this.inventories.get(page);
            if (inventory == null)
                inventory = this.createInventory(page);

            this.populateInventory(page, inventory);
        }
    }

    private void populateInventory(int pageNumber, Inventory inventory) {
        inventory.clear();

        if (this.paginatedSection != null) {
            int amount = this.paginatedSection.getSlotAmount();
            int startIndex = (amount * (pageNumber - 1));
            int endIndex = (amount * (pageNumber - 1)) + amount;

            GuiPageContentsResult result = this.pageContentsRequester.request(pageNumber, startIndex, endIndex);
            List<ISlotable> pageContents = result.getPageContents();
            List<Integer> slots = this.paginatedSection.getSlots();

            for (int i = 0; i < slots.size() && i < pageContents.size(); i++)
                inventory.setItem(slots.get(i), pageContents.get(i).getItemStack());

            this.paginatedSlotCache.put(pageNumber, result);
        }

        for (int slot : this.slots.keySet())
            inventory.setItem(slot, this.slots.get(slot).getItemStack());
    }

    private Inventory createInventory(int pageNumber) {
        Inventory inventory;
        switch (this.size) {
            case DYNAMIC:
                int slots = Math.min((((int) (this.slots.keySet().stream().max(Integer::compareTo).orElse(0) / 9.0)) + 1) * 9, GuiUtil.ROW_6_END + 1);
                inventory = Bukkit.createInventory(null, slots, this.title);
                break;
            case ROWS_ONE:
            case ROWS_TWO:
            case ROWS_THREE:
            case ROWS_FOUR:
            case ROWS_FIVE:
            case ROWS_SIX:
                inventory = Bukkit.createInventory(null, this.size.getNumSlots(), this.title);
                break;
            case HOPPER:
                inventory = Bukkit.createInventory(null, InventoryType.HOPPER, this.title);
                break;
            case DISPENSER:
                inventory = Bukkit.createInventory(null, InventoryType.DISPENSER, this.title);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.size);
        }

        this.inventories.put(pageNumber, inventory);

        return inventory;
    }

    @FunctionalInterface
    public interface PageContentsRequester {
        GuiPageContentsResult request(int pageNumber, int startIndex, int endIndex);
    }

}
