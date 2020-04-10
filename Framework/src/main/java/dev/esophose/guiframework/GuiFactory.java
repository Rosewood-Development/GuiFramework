package dev.esophose.guiframework;

import dev.esophose.guiframework.framework.gui.FrameworkButton;
import dev.esophose.guiframework.framework.gui.FrameworkContainer;
import dev.esophose.guiframework.framework.gui.FrameworkIcon;
import dev.esophose.guiframework.framework.gui.FrameworkString;
import dev.esophose.guiframework.framework.gui.screen.FrameworkItem;
import dev.esophose.guiframework.framework.gui.screen.FrameworkPageContentsResult;
import dev.esophose.guiframework.framework.gui.screen.FrameworkScreen;
import dev.esophose.guiframework.framework.gui.screen.FrameworkScreenEditFilters;
import dev.esophose.guiframework.framework.gui.screen.FrameworkScreenSection;
import dev.esophose.guiframework.gui.GuiButton;
import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.GuiIcon;
import dev.esophose.guiframework.gui.GuiSize;
import dev.esophose.guiframework.gui.GuiString;
import dev.esophose.guiframework.gui.screen.GuiItem;
import dev.esophose.guiframework.gui.screen.GuiPageContentsResult;
import dev.esophose.guiframework.gui.screen.GuiScreen;
import dev.esophose.guiframework.gui.screen.GuiScreenEditFilters;
import dev.esophose.guiframework.gui.screen.GuiScreenSection;
import dev.esophose.guiframework.gui.screen.Slotable;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class GuiFactory {

    @NotNull
    public static GuiItem createItem(@NotNull ItemStack itemStack) {
        return new FrameworkItem(itemStack);
    }

    @NotNull
    public static GuiPageContentsResult createPageContentsResult() {
        return new FrameworkPageContentsResult();
    }

    @NotNull
    public static GuiPageContentsResult createPageContentsResult(@NotNull List<Slotable> pageContents) {
        return new FrameworkPageContentsResult(pageContents);
    }

    @NotNull
    public static GuiScreen createScreen(@NotNull GuiContainer parentContainer, @NotNull GuiSize size) {
        return new FrameworkScreen((FrameworkContainer) parentContainer, size);
    }

    @NotNull
    public static GuiScreenEditFilters createScreenEditFilters() {
        return new FrameworkScreenEditFilters();
    }

    @NotNull
    public static GuiScreenSection createScreenSection(List<Integer> slots) {
        return new FrameworkScreenSection(slots);
    }

    @NotNull
    public static GuiScreenSection createScreenSection(int beginSlotIndex, int endSlotIndex) {
        return new FrameworkScreenSection(beginSlotIndex, endSlotIndex);
    }

    @NotNull
    public static GuiButton createButton() {
        return new FrameworkButton();
    }

    @NotNull
    public static GuiContainer createContainer() {
        return new FrameworkContainer();
    }

    @NotNull
    public static GuiIcon createIcon() {
        return new FrameworkIcon();
    }

    @NotNull
    public static GuiIcon createIcon(Material material) {
        return new FrameworkIcon(material);
    }

    @NotNull
    public static GuiIcon createIcon(Material material, Consumer<ItemMeta> itemMetaApplier) {
        return new FrameworkIcon(material, itemMetaApplier);
    }

    @NotNull
    public static GuiString createString() {
        return new FrameworkString();
    }

    @NotNull
    public static GuiString createString(String string) {
        return new FrameworkString(string);
    }

}
