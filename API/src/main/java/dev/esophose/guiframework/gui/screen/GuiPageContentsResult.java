package dev.esophose.guiframework.gui.screen;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface GuiPageContentsResult {

    /**
     * Sets the exact contents of the result
     *
     * @param pageContents The contents
     */
    void setPageContents(@NotNull List<Slotable> pageContents);

    /**
     * Adds a Slotable to the contents of the result
     *
     * @param content The Slotable to add
     */
    void addPageContent(@NotNull Slotable content);

    /**
     * Adds an ItemStack to the contents of the result
     *
     * @param content The ItemStack to add
     */
    void addPageContent(@NotNull ItemStack content);

    /**
     * @return An unmodifiable list of the page contents
     */
    @NotNull
    List<Slotable> getPageContents();

}
