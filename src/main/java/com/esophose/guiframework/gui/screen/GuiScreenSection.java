package com.esophose.guiframework.gui.screen;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiScreenSection {

    private List<Integer> slots;

    public GuiScreenSection(List<Integer> slots) {
        this.slots = slots;
        this.slots.sort(Integer::compareTo);
    }

    public GuiScreenSection(int beginSlotIndex, int endSlotIndex) {
        this(IntStream.rangeClosed(beginSlotIndex, endSlotIndex).boxed().collect(Collectors.toList()));
    }

    public GuiScreenSection(int... slots) {
        this(Arrays.stream(slots).boxed().collect(Collectors.toList()));
    }

    /**
     * @return a list of ordered slot indices for this section
     */
    public List<Integer> getSlots() {
        return Collections.unmodifiableList(this.slots);
    }

    /**
     * @return the number of slots in this section
     */
    public int getSlotAmount() {
        return this.slots.size();
    }

    /**
     * Executes the action for each slot index in this section
     *
     * @param action The action to execute
     */
    public void forEach(@NotNull Consumer<Integer> action) {
        for (int slot : this.slots)
            action.accept(slot);
    }

    /**
     * Executes the action for each slot in this section
     *
     * @param screen The screen to iterate through
     * @param action The action to execute
     */
    public void forEachSlot(@NotNull GuiScreen screen, @NotNull Consumer<ISlotable> action) {
        for (int slot : this.slots)
            action.accept(screen.getSlot(slot));
    }

    /**
     * Executes the action for each non-empty slot in this section
     *
     * @param screen The screen to iterate through
     * @param action The action to execute
     */
    public void forEachNonEmptySlot(@NotNull GuiScreen screen, @NotNull Consumer<ISlotable> action) {
        for (int slot : this.slots) {
            ISlotable slotable = screen.getSlot(slot);
            if (slotable != null)
                action.accept(slotable);
        }
    }

}
