package dev.rosewood.guiframework.framework.gui.screen;

import dev.rosewood.guiframework.framework.gui.FrameworkButton;
import dev.rosewood.guiframework.framework.gui.FrameworkContainer;
import dev.rosewood.guiframework.framework.util.GuiUtil;
import dev.rosewood.guiframework.gui.GuiButton;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.GuiView;
import dev.rosewood.guiframework.gui.Tickable;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import dev.rosewood.guiframework.gui.screen.GuiScreenEditFilters;
import dev.rosewood.guiframework.gui.screen.GuiScreenSection;
import dev.rosewood.guiframework.gui.screen.PageContentsRequester;
import dev.rosewood.guiframework.gui.screen.Slotable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrameworkScreen implements GuiScreen {

    private FrameworkContainer parentContainer;
    private GuiSize size;
    private String title;
    private FrameworkScreenSection paginatedSection;
    private FrameworkScreenSection editableSection;
    private List<ItemStack> editableItems;
    private FrameworkScreenEditFilters editFilters;
    private BiConsumer<Player, List<ItemStack>> editFinalizationCallback;
    private int maximumPageNumber;
    private PageContentsRequester paginatedContentsRequester;
    private Map<Integer, FrameworkPageContentsResult> paginatedSlotCache;
    private Map<Integer, Slotable> permanentSlots;
    private Map<Integer, Inventory> inventories;

    public FrameworkScreen(@NotNull FrameworkContainer parentContainer, @NotNull GuiSize size) {
        this.parentContainer = parentContainer;
        this.size = size;
        this.title = "";
        this.paginatedSection = null;
        this.editableSection = null;
        this.editableItems = null;
        this.editFinalizationCallback = null;
        this.maximumPageNumber = 1;
        this.paginatedContentsRequester = null;
        this.paginatedSlotCache = new HashMap<>();
        this.permanentSlots = new HashMap<>();
        this.inventories = new HashMap<>();
    }

    @Override
    public FrameworkScreen setTitle(String title) {
        this.title = title;

        return this;
    }

    @Override
    public FrameworkScreen setPaginatedSection(int beginIndex, int endIndex, int totalItems, PageContentsRequester pageContentsRequester) {
        return this.setPaginatedSection(new FrameworkScreenSection(beginIndex, endIndex), totalItems, pageContentsRequester);
    }

    @Override
    public FrameworkScreen setPaginatedSection(GuiScreenSection guiScreenSection, int totalItems, PageContentsRequester pageContentsRequester) {
        if (this.editableSection != null) {
            Bukkit.getLogger().warning("[GuiFramework] Developer error: Tried to create a paginated section when an editable section was already set. The paginated section has been removed.");
            this.editableSection = null;
            this.editableItems = null;
        }

        this.paginatedSection = (FrameworkScreenSection) guiScreenSection;
        this.paginatedContentsRequester = pageContentsRequester;
        this.maximumPageNumber = (int) Math.ceil((double) totalItems / this.paginatedSection.getSlotAmount());

        return this;
    }

    @Override
    public FrameworkScreen setEditableSection(int beginIndex, int endIndex, Collection<ItemStack> items, BiConsumer<Player, List<ItemStack>> editFinalizationCallback) {
        return this.setEditableSection(new FrameworkScreenSection(beginIndex, endIndex), items, editFinalizationCallback);
    }

    @Override
    public FrameworkScreen setEditableSection(GuiScreenSection guiScreenSection, Collection<ItemStack> items, BiConsumer<Player, List<ItemStack>> editFinalizationCallback) {
        if (this.paginatedSection != null) {
            Bukkit.getLogger().warning("[GuiFramework] Developer error: Tried to create an editable section when a paginated section was already set. The paginated section has been removed.");
            this.paginatedSection = null;
            this.paginatedContentsRequester = null;
        }

        this.editableSection = (FrameworkScreenSection) guiScreenSection;
        this.editableItems = new ArrayList<>(items);
        this.editFinalizationCallback = editFinalizationCallback;
        this.maximumPageNumber = (int) Math.ceil((double) items.size() / this.editableSection.getSlotAmount());

        return this;
    }

    @Override
    public FrameworkScreen setEditFilters(GuiScreenEditFilters editFilters) {
        this.editFilters = (FrameworkScreenEditFilters) editFilters;

        return this;
    }

    @Override
    public FrameworkScreen addItemStackAt(int slot, ItemStack content) {
        this.permanentSlots.put(slot, new FrameworkItem(content));

        return this;
    }

    @Override
    public FrameworkScreen addItemStack(ItemStack content) {
        int slot = this.permanentSlots.keySet().stream().max(Integer::compareTo).orElse(-1) + 1;
        this.addItemStackAt(slot, content);

        return this;
    }

    @Override
    public FrameworkScreen addButtonAt(int slot, GuiButton button) {
        this.permanentSlots.put(slot, button);

        return this;
    }

    @Override
    public FrameworkScreen addButton(GuiButton button) {
        int slot = this.permanentSlots.keySet().stream().max(Integer::compareTo).orElse(-1) + 1;
        this.addButtonAt(slot, button);

        return this;
    }

    @Override
    public FrameworkContainer getParentContainer() {
        return this.parentContainer;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public Slotable getSlot(int index) {
        return this.permanentSlots.get(index);
    }

    @Override
    public Map<Integer, Slotable> getSlots() {
        return this.permanentSlots;
    }

    @Override
    public int getMaximumPageNumber() {
        return this.maximumPageNumber;
    }

    @Override
    public FrameworkScreenSection getEditableSection() {
        return this.editableSection;
    }

    @Override
    public FrameworkScreenEditFilters getEditFilters() {
        return this.editFilters;
    }

    @Override
    public GuiSize getSize() {
        return this.size;
    }

    @Override
    public GuiSize getCurrentSize() {
        GuiSize size = this.size;
        if (size == GuiSize.DYNAMIC)
            size = GuiUtil.getGuiSizeFromSlots(this.permanentSlots.keySet());
        return size;
    }

    @Override
    public void rebuild() {
        this.inventories.clear();
        this.updateInventories();
    }

    @Override
    public void tick() {
        this.permanentSlots.values().stream().filter(Slotable::isTickable).forEach(x -> ((Tickable) x).tick());
        this.paginatedSlotCache.values().forEach(x -> x.getPageContents().stream().filter(Slotable::isTickable).forEach(y -> ((Tickable) y).tick()));
        this.updateInventories();
    }

    public void onViewersLeave(Player lastViewer) {
        if (this.editFinalizationCallback != null) {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 1; i <= this.maximumPageNumber; i++) {
                Inventory inventory;
                if (this.inventories.containsKey(i)) {
                    inventory = this.inventories.get(i);
                } else {
                    inventory = this.createInventory(i);
                    this.populateInventory(i, inventory);
                }

                for (int slot : this.editableSection.getSlots()) {
                    ItemStack itemStack = inventory.getItem(slot);
                    if (itemStack != null && itemStack.getType() != Material.AIR)
                        items.add(itemStack);
                }
            }

            this.editFinalizationCallback.accept(lastViewer, items);
        }
    }

    /**
     * Gets a button on the screen given a target page and slot number
     *
     * @param inventory The Inventory to try to get the button on
     * @param slot The slot of the target button
     * @return a GuiButton in the given slot on the given inventory if one exists, otherwise null
     */
    @Nullable
    public FrameworkButton getButtonOnInventoryPage(@NotNull Inventory inventory, int slot) {
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
    public Map<Integer, FrameworkButton> getButtons(int pageNumber) {
        Map<Integer, FrameworkButton> buttons = this.getButtons();
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
    public Map<Integer, FrameworkButton> getPageButtons(int pageNumber) {
        if (this.paginatedSection == null)
            return new HashMap<>();

        Map<Integer, FrameworkButton> pageButtons = new HashMap<>();

        FrameworkPageContentsResult contentsResult = this.paginatedSlotCache.get(pageNumber);
        if (contentsResult == null)
            return pageButtons;

        List<Slotable> pageContents = contentsResult.getPageContents();
        List<Integer> slots = this.paginatedSection.getSlots();

        for (int i = 0; i < slots.size() && i < pageContents.size(); i++) {
            Slotable slot = pageContents.get(i);
            if (slot instanceof FrameworkButton)
                pageButtons.put(slots.get(i), (FrameworkButton) slot);
        }

        return pageButtons;
    }

    /**
     * Gets an unmodifyable map of the buttons on the screen.
     * Does not contain paginated buttons.
     * The key is the slot index.
     *
     * @return The buttons on the screen
     */
    @NotNull
    public Map<Integer, FrameworkButton> getButtons() {
        return this.permanentSlots.entrySet()
                .stream()
                .filter(x -> x.getValue() instanceof FrameworkButton)
                .map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), (FrameworkButton) x.getValue()))
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
    public Inventory getInventory(int pageNumber) {
        if (this.inventories.containsKey(pageNumber)) {
            return this.inventories.get(pageNumber);
        }

        Inventory inventory = this.createInventory(pageNumber);
        this.populateInventory(pageNumber, inventory);

        return inventory;
    }

    public boolean hasNextPage(int pageNumber) {
        return pageNumber < this.maximumPageNumber;
    }

    public void updateInventories() {
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
        boolean newInventory = Arrays.stream(inventory.getStorageContents()).anyMatch(x -> x != null && x.getType() != Material.AIR);

        if (!newInventory)
            inventory.clear();

        if (this.paginatedSection != null || this.editableSection != null) {
            List<Slotable> pageContents = null;
            List<Integer> slots = null;

            if (this.paginatedSection != null) {
                int amount = this.paginatedSection.getSlotAmount();
                int startIndex = (amount * (pageNumber - 1));
                int endIndex = startIndex + amount;

                FrameworkPageContentsResult result;
                if (!this.paginatedSlotCache.containsKey(pageNumber)) {
                    result = (FrameworkPageContentsResult) this.paginatedContentsRequester.request(pageNumber, startIndex, endIndex);
                    this.paginatedSlotCache.put(pageNumber, result);
                } else {
                    result = this.paginatedSlotCache.get(pageNumber);
                }

                pageContents = result.getPageContents();
                slots = this.paginatedSection.getSlots();
            } else if (!newInventory) { // Only fill the editable slots once so we don't end up with duplicated items
                if (this.paginatedSlotCache.isEmpty()) {
                    int editableItemIndex = 0;
                    for (int i = 1; i <= this.maximumPageNumber; i++) {
                        FrameworkPageContentsResult result = new FrameworkPageContentsResult();
                        for (int n = 0; n < this.editableSection.getSlotAmount(); n++) {
                            result.addPageContent(this.editableItems.get(editableItemIndex));

                            if (++editableItemIndex >= this.editableItems.size())
                                break;
                        }

                        this.paginatedSlotCache.put(i, result);

                        if (editableItemIndex >= this.editableItems.size())
                            break;
                    }
                }

                pageContents = this.paginatedSlotCache.get(pageNumber).getPageContents();
                slots = this.editableSection.getSlots();
            }

            if (slots != null && pageContents != null) {
                for (int i = 0; i < slots.size() && i < pageContents.size(); i++) {
                    int slot = slots.get(i);
                    Slotable slotable = pageContents.get(i);
                    boolean isVisible = slotable.isVisible(pageNumber, this.maximumPageNumber);
                    ItemStack itemStack = slotable.getItemStack(isVisible);
                    if (itemStack == null)
                        continue;
                    inventory.setItem(slot, this.applyPageNumberReplacements(itemStack, pageNumber, this.maximumPageNumber));
                }
            }
        }

        for (int slot : this.permanentSlots.keySet()) {
            Slotable slotable = this.permanentSlots.get(slot);
            boolean isVisible = slotable.isVisible(pageNumber, this.maximumPageNumber);
            ItemStack itemStack = slotable.getItemStack(isVisible);
            if (itemStack == null)
                continue;
            inventory.setItem(slot, this.applyPageNumberReplacements(itemStack, pageNumber, this.maximumPageNumber));
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

        switch (this.getCurrentSize()) {
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

}
