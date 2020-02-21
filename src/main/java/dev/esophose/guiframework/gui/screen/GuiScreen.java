package dev.esophose.guiframework.gui.screen;

import dev.esophose.guiframework.gui.GuiButton;
import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.GuiSize;
import dev.esophose.guiframework.gui.GuiView;
import dev.esophose.guiframework.gui.Tickable;
import dev.esophose.guiframework.util.GuiUtil;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiScreen implements Tickable {

    private GuiContainer parentContainer;
    private GuiSize size;
    private String title;
    private GuiScreenSection paginatedSection;
    private int maximumPageNumber;
    private GuiScreenSection editableSection;
    private PageContentsRequester pageContentsRequester;
    private Map<Integer, GuiPageContentsResult> paginatedSlotCache;
    private Map<Integer, Slotable> slots;
    private Map<Integer, Inventory> inventories;

    public GuiScreen(@NotNull GuiContainer parentContainer, @NotNull GuiSize size) {
        this.parentContainer = parentContainer;
        this.size = size;
        this.title = "";
        this.paginatedSection = null;
        this.maximumPageNumber = 1;
        this.editableSection = null;
        this.pageContentsRequester = null;
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
    public GuiScreen setPaginatedSection(int beginIndex, int endIndex, int totalItems, @NotNull PageContentsRequester pageContentsRequester) {
        return this.setPaginatedSection(new GuiScreenSection(beginIndex, endIndex), totalItems, pageContentsRequester);
    }

    @NotNull
    public GuiScreen setPaginatedSection(@NotNull GuiScreenSection guiScreenSection, int totalItems, @NotNull PageContentsRequester pageContentsRequester) {
        this.paginatedSection = guiScreenSection;
        this.pageContentsRequester = pageContentsRequester;
        this.maximumPageNumber = (int) Math.ceil((double) totalItems / this.paginatedSection.getSlotAmount());

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
        this.slots.values().forEach(Slotable::tick);
        this.paginatedSlotCache.values().forEach(x -> x.getPageContents().forEach(Slotable::tick));
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
    public Slotable getSlot(int index) {
        return this.slots.get(index);
    }

    @NotNull
    public Map<Integer, Slotable> getSlots() {
        return this.slots;
    }

    public int getMaximumPageNumber() {
        return this.maximumPageNumber;
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

    public boolean isButtonOnInventoryPageVisible(@NotNull Inventory inventory, int slot) {
        for (int page : this.inventories.keySet())
            if (this.inventories.get(page) == inventory)
                return this.getButtons(page).get(slot).isVisible(page, this.maximumPageNumber);
        return true;
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

        GuiPageContentsResult contentsResult = this.paginatedSlotCache.get(pageNumber);
        if (contentsResult == null)
            return pageButtons;

        List<Slotable> pageContents = contentsResult.getPageContents();
        List<Integer> slots = this.paginatedSection.getSlots();

        for (int i = 0; i < slots.size() && i < pageContents.size(); i++) {
            Slotable slot = pageContents.get(i);
            if (slot instanceof GuiButton)
                pageButtons.put(slots.get(i), (GuiButton) slot);
        }

        return pageButtons;
    }

    @Nullable
    public Map<Integer, ItemStack> getEditableContent() {
        if (this.editableSection == null)
            return null;

        Map<Integer, ItemStack> content = new HashMap<>();

        this.editableSection.forEach(i -> {
            Slotable slotable = this.getSlot(i);
            if (slotable != null)
                content.put(i, slotable.getItemStack());
        });

        return content;
    }

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
        return this.inventories.containsValue(inventory);
    }

    /**
     * Gets the Inventory for a page on this screen
     *
     * @param pageNumber The page number to get
     * @return The Inventory with the page's content
     */
    @NotNull
    public Inventory getInventory(int pageNumber) {
        return this.getInventory(pageNumber, false);
    }

    /**
     * Gets the Inventory for a page on this screen
     *
     * @param pageNumber The page number to get
     * @param forceRefresh Whether or not to forcefully refresh the inventory
     * @return The Inventory with the page's content
     */
    public Inventory getInventory(int pageNumber, boolean forceRefresh) {
        if (this.inventories.containsKey(pageNumber)) {
            Inventory inventory = this.inventories.get(pageNumber);
            if (forceRefresh)
                this.populateInventory(pageNumber, inventory);
            return inventory;
        }

        Inventory inventory = this.createInventory(pageNumber);
        this.populateInventory(pageNumber, inventory);

        return inventory;
    }

    public boolean hasNextPage(int pageNumber) {
        return pageNumber < this.maximumPageNumber;
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

            GuiPageContentsResult result;
            if (!this.paginatedSlotCache.containsKey(pageNumber)) {
                result = this.pageContentsRequester.request(pageNumber, startIndex, endIndex);
                this.paginatedSlotCache.put(pageNumber, result);
            } else {
                result = this.paginatedSlotCache.get(pageNumber);
            }

            List<Slotable> pageContents = result.getPageContents();
            List<Integer> slots = this.paginatedSection.getSlots();

            for (int i = 0; i < slots.size() && i < pageContents.size(); i++) {
                int slot = slots.get(i);
                Slotable slotable = pageContents.get(i);
                if (slotable.isVisible(pageNumber, this.maximumPageNumber))
                    inventory.setItem(slot, this.applyPageNumberReplacements(slotable.getItemStack(), pageNumber, this.maximumPageNumber));
            }
        }

        for (int slot : this.slots.keySet()) {
            Slotable slotable = this.slots.get(slot);
            if (slotable.isVisible(pageNumber, this.maximumPageNumber))
                inventory.setItem(slot, this.applyPageNumberReplacements(slotable.getItemStack(), pageNumber, this.maximumPageNumber));
        }
    }

    private ItemStack applyPageNumberReplacements(ItemStack itemStack, int currentPageNumber, int maxPageNumber) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        itemMeta.setDisplayName(itemMeta.getDisplayName()
                .replaceAll(Pattern.quote(GuiUtil.PREVIOUS_PAGE_NUMBER_PLACEHOLDER), String.valueOf(currentPageNumber - 1))
                .replaceAll(Pattern.quote(GuiUtil.CURRENT_PAGE_NUMBER_PLACEHOLDER), String.valueOf(currentPageNumber))
                .replaceAll(Pattern.quote(GuiUtil.NEXT_PAGE_NUMBER_PLACEHOLDER), String.valueOf(currentPageNumber + 1))
                .replaceAll(Pattern.quote(GuiUtil.MAX_PAGE_NUMBER_PLACEHOLDER), String.valueOf(maxPageNumber)));

        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            lore.replaceAll(x -> x.replaceAll(Pattern.quote(GuiUtil.PREVIOUS_PAGE_NUMBER_PLACEHOLDER), String.valueOf(currentPageNumber - 1))
                    .replaceAll(Pattern.quote(GuiUtil.CURRENT_PAGE_NUMBER_PLACEHOLDER), String.valueOf(currentPageNumber))
                    .replaceAll(Pattern.quote(GuiUtil.NEXT_PAGE_NUMBER_PLACEHOLDER), String.valueOf(currentPageNumber + 1))
                    .replaceAll(Pattern.quote(GuiUtil.MAX_PAGE_NUMBER_PLACEHOLDER), String.valueOf(maxPageNumber)));
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
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
