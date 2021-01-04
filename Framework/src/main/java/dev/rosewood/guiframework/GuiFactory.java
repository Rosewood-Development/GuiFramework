package dev.rosewood.guiframework;

import dev.rosewood.guiframework.framework.gui.FrameworkButton;
import dev.rosewood.guiframework.framework.gui.FrameworkContainer;
import dev.rosewood.guiframework.framework.gui.FrameworkIcon;
import dev.rosewood.guiframework.framework.gui.FrameworkString;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkItem;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkPageContentsResult;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkScreen;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkScreenEditFilters;
import dev.rosewood.guiframework.framework.gui.screen.FrameworkScreenSection;
import dev.rosewood.guiframework.gui.GuiButton;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiIcon;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.GuiString;
import dev.rosewood.guiframework.gui.manager.GuiManager;
import dev.rosewood.guiframework.gui.screen.GuiItem;
import dev.rosewood.guiframework.gui.screen.GuiPageContentsResult;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import dev.rosewood.guiframework.gui.screen.GuiScreenEditFilters;
import dev.rosewood.guiframework.gui.screen.GuiScreenSection;
import dev.rosewood.guiframework.gui.screen.Slotable;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class GuiFactory {

    /**
     * Creates a new GuiItem from an ItemStack. This is simply to wrap an ItemStack in a Slotable interface.
     *
     * @param itemStack The ItemStack to wrap
     * @return a new GuiItem
     */
    @NotNull
    public static GuiItem createItem(@NotNull ItemStack itemStack) {
        return new FrameworkItem(itemStack);
    }

    /**
     * @return an empty GuiPageContentsResult
     */
    @NotNull
    public static GuiPageContentsResult createPageContentsResult() {
        return new FrameworkPageContentsResult();
    }

    /**
     * Creates a new GuiPageContentsResult with existing values
     *
     * @param pageContents a List of Slotable items (ex. GuiItem or GuiButton)
     * @return a new GuiPageContentsResult with existing values
     */
    @NotNull
    public static GuiPageContentsResult createPageContentsResult(@NotNull List<Slotable> pageContents) {
        return new FrameworkPageContentsResult(pageContents);
    }

    /**
     * Creates a new GuiScreen given a parent GuiContainer and a GuiSize
     *
     * @param parentContainer The parent container
     * @param size The size of the screen
     * @return a new GuiScreen
     */
    @NotNull
    public static GuiScreen createScreen(@NotNull GuiContainer parentContainer, @NotNull GuiSize size) {
        return new FrameworkScreen((FrameworkContainer) parentContainer, size);
    }

    /**
     * @return a new set of GuiScreenEditFilters for white/blacklisting certain Materials
     */
    @NotNull
    public static GuiScreenEditFilters createScreenEditFilters() {
        return new FrameworkScreenEditFilters();
    }

    /**
     * Creates a new GuiScreenSection given a List of Integer slot ids
     *
     * @param slots The slots
     * @return a new GuiScreenSection
     */
    @NotNull
    public static GuiScreenSection createScreenSection(List<Integer> slots) {
        return new FrameworkScreenSection(slots);
    }

    /**
     * Creates a new GuiScreenSection given a beginning and ending slot index
     *
     * @param beginSlotIndex The beginning slot index, inclusive
     * @param endSlotIndex The ending slot index, inclusive
     * @return a new GuiScreenSection
     */
    @NotNull
    public static GuiScreenSection createScreenSection(int beginSlotIndex, int endSlotIndex) {
        return new FrameworkScreenSection(beginSlotIndex, endSlotIndex);
    }

    /**
     * @return an empty GuiButton
     */
    @NotNull
    public static GuiButton createButton() {
        return new FrameworkButton();
    }

    /**
     * Creates a new GuiButton from an ItemStack. This GuiButton will throw an {@link IllegalStateException} if you try
     * to modify any properties that represent how the ItemStack appears.
     *
     * @param itemStack The ItemStack
     * @return a new GuiButton
     */
    @NotNull
    public static GuiButton createButton(ItemStack itemStack) {
        return new FrameworkButton(itemStack);
    }

    /**
     * Creates a new GuiContainer. Make sure to register it with {@link GuiManager#registerGui(GuiContainer)}
     *
     * @return an empty GuiContainer
     */
    @NotNull
    public static GuiContainer createContainer() {
        return new FrameworkContainer();
    }

    /**
     * @return an empty GuiIcon
     */
    @NotNull
    public static GuiIcon createIcon() {
        return new FrameworkIcon();
    }

    /**
     * Creates a new GuiIcon from a given Material
     *
     * @param material The Material
     * @return a new GuiIcon
     */
    @NotNull
    public static GuiIcon createIcon(Material material) {
        return new FrameworkIcon(material);
    }

    /**
     * Creates a new GuiIcon from a Material and an ItemMeta Consumer to provide custom ItemMeta
     *
     * @param material The Material
     * @param itemMetaApplier The ItemMeta supplier to modify the appearance of the icon
     * @return a new GuiIcon
     */
    @NotNull
    public static GuiIcon createIcon(Material material, Consumer<ItemMeta> itemMetaApplier) {
        return new FrameworkIcon(material, itemMetaApplier);
    }

    /**
     * @return an empty GuiString
     */
    @NotNull
    public static GuiString createString() {
        return new FrameworkString();
    }

    /**
     * Creates a new GuiString with a base value
     *
     * @param string The base String value
     * @return a new GuiString
     */
    @NotNull
    public static GuiString createString(String string) {
        return new FrameworkString(string);
    }

}
