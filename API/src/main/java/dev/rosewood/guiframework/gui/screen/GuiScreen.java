package dev.rosewood.guiframework.gui.screen;

import dev.rosewood.guiframework.gui.GuiButton;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.Tickable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuiScreen extends Tickable {

    @NotNull
    GuiScreen setTitle(@NotNull String title);

    @NotNull
    GuiScreen setPaginatedSection(int beginIndex, int endIndex, int totalItems, @NotNull PageContentsRequester pageContentsRequester);

    @NotNull
    GuiScreen setPaginatedSection(@NotNull GuiScreenSection guiScreenSection, int totalItems, @NotNull PageContentsRequester pageContentsRequester);

    @NotNull
    GuiScreen setEditableSection(int beginIndex, int endIndex, @NotNull Collection<ItemStack> items, @NotNull BiConsumer<Player, List<ItemStack>> editFinalizationCallback);

    @NotNull
    GuiScreen setEditableSection(@NotNull GuiScreenSection guiScreenSection, @NotNull Collection<ItemStack> items, @NotNull BiConsumer<Player, List<ItemStack>> editFinalizationCallback);

    @NotNull
    GuiScreen setEditFilters(@NotNull GuiScreenEditFilters editFilters);

    @NotNull
    GuiScreen addItemStackAt(int slot, @NotNull ItemStack content);

    @NotNull
    GuiScreen addItemStack(@NotNull ItemStack content);

    @NotNull
    GuiScreen addButtonAt(int slot, @NotNull GuiButton button);

    @NotNull
    GuiScreen addButton(@NotNull GuiButton button);

    /**
     * Executes a Runnable every time this screen ticks.
     * Calls before updating the contents and buttons.
     *
     * @param handler The Runnable
     * @return this
     */
    @NotNull
    GuiScreen setTickHandler(@NotNull Runnable handler);

    /**
     * Adds a slot listener which will be called whenever the slot contents changes.
     * The slot listener will be called based on the GuiScreen's tick rate.
     *
     * @param slot The slot to listen to
     * @param callback The callback to run when the slot changes
     * @return this
     */
    @NotNull
    GuiScreen addSlotListener(int slot, @NotNull Consumer<ItemStack> callback);

    @NotNull
    GuiContainer getParentContainer();

    @NotNull
    String getTitle();

    @Nullable
    Slotable getSlot(int index);

    @NotNull
    Map<Integer, Slotable> getSlots();

    int getMaximumPageNumber();

    @Nullable
    GuiScreenSection getEditableSection();

    @Nullable
    GuiScreenEditFilters getEditFilters();

    @NotNull
    GuiSize getSize();

    @NotNull
    GuiSize getCurrentSize();

    /**
     * Destroys all inventories and rebuilds them from scratch.
     * Opens the newly created inventories for existing viewers.
     * Capable of updating titles.
     * Not recommended to call often.
     */
    void rebuild();

}
