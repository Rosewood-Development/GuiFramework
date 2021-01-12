package dev.rosewood.guiframework.framework.gui.screen;

import dev.rosewood.guiframework.gui.screen.GuiScreen;
import dev.rosewood.guiframework.gui.screen.GuiScreenSection;
import dev.rosewood.guiframework.gui.screen.Slotable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FrameworkScreenSection implements GuiScreenSection {

    private final Set<Integer> slots;

    public FrameworkScreenSection(Collection<Integer> slots) {
        this.slots = new TreeSet<>(slots);
    }

    public FrameworkScreenSection(int beginSlotIndex, int endSlotIndex) {
        this(IntStream.rangeClosed(beginSlotIndex, endSlotIndex).boxed().collect(Collectors.toList()));
    }

    @Override
    public GuiScreenSection addSlots(int... slots) {
        this.slots.addAll(IntStream.of(slots).boxed().collect(Collectors.toList()));

        return this;
    }

    @Override
    public GuiScreenSection addSlotRange(int from, int to) {
        this.slots.addAll(IntStream.rangeClosed(from, to).boxed().collect(Collectors.toList()));

        return this;
    }

    @Override
    public List<Integer> getSlots() {
        return new ArrayList<>(this.slots);
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
