package dev.rosewood.guiframework.gui.screen;

import dev.rosewood.guiframework.gui.GuiButton;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.Tickable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
     * Opens the newly created inventories for existinv viewers.
     * Capable of updating titles.
     * Not recommended to call often.
     */
    void rebuild();

}
