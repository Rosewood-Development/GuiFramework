package dev.esophose.guiframework.gui.screen;

import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface GuiScreenSection {

    /**
     * @return a list of ordered slot indices for this section
     */
    @NotNull
    List<Integer> getSlots();

    /**
     * Checks if a slot is in this GuiScreenSection
     *
     * @param slot The slot to check
     * @return true if the slot is in this section, otherwise false
     */
    boolean containsSlot(int slot);

    /**
     * @return the number of slots in this section
     */
    int getSlotAmount();

    /**
     * Executes the action for each slot index in this section
     *
     * @param action The action to execute
     */
    void forEach(@NotNull Consumer<Integer> action);

    /**
     * Executes the action for each slot in this section
     *
     * @param screen The screen to iterate through
     * @param action The action to execute
     */
    void forEachSlot(@NotNull GuiScreen screen, @NotNull Consumer<Slotable> action);

    /**
     * Executes the action for each non-empty slot in this section
     *
     * @param screen The screen to iterate through
     * @param action The action to execute
     */
    void forEachNonEmptySlot(@NotNull GuiScreen screen, @NotNull Consumer<Slotable> action);

}
