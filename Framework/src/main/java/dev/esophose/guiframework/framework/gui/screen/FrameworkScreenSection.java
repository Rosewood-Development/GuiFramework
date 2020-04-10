package dev.esophose.guiframework.framework.gui.screen;

import dev.esophose.guiframework.gui.screen.GuiScreen;
import dev.esophose.guiframework.gui.screen.GuiScreenSection;
import dev.esophose.guiframework.gui.screen.Slotable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;

public class FrameworkScreenSection implements GuiScreenSection {

    private List<Integer> slots;

    public FrameworkScreenSection(List<Integer> slots) {
        this.slots = slots;
        this.slots.sort(Integer::compareTo);
    }

    public FrameworkScreenSection(int beginSlotIndex, int endSlotIndex) {
        this(IntStream.rangeClosed(beginSlotIndex, endSlotIndex).boxed().collect(Collectors.toList()));
    }

    @Override
    public List<Integer> getSlots() {
        return Collections.unmodifiableList(this.slots);
    }

    @Override
    public boolean containsSlot(int slot) {
        return this.slots.contains(slot);
    }

    @Override
    public int getSlotAmount() {
        return this.slots.size();
    }

    @Override
    public void forEach(Consumer<Integer> action) {
        for (int slot : this.slots)
            action.accept(slot);
    }

    @Override
    public void forEachSlot(GuiScreen screen, Consumer<Slotable> action) {
        for (int slot : this.slots)
            action.accept(screen.getSlot(slot));
    }

    @Override
    public void forEachNonEmptySlot(GuiScreen screen, Consumer<Slotable> action) {
        for (int slot : this.slots) {
            Slotable slotable = screen.getSlot(slot);
            if (slotable != null)
                action.accept(slotable);
        }
    }

}
