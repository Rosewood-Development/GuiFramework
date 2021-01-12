package dev.rosewood.guiframework.framework.util;

import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Contains useful methods for creating Guis
 */
public final class GuiUtil {

    private GuiUtil() {

    }

    public static final int ROW_1_START = 0;
    public static final int ROW_2_START = 9;
    public static final int ROW_3_START = 18;
    public static final int ROW_4_START = 27;
    public static final int ROW_5_START = 36;
    public static final int ROW_6_START = 45;
    public static final int ROW_1_END = 8;
    public static final int ROW_2_END = 17;
    public static final int ROW_3_END = 26;
    public static final int ROW_4_END = 35;
    public static final int ROW_5_END = 44;
    public static final int ROW_6_END = 53;

    public static final String PREVIOUS_PAGE_NUMBER_PLACEHOLDER = "%previousPage%";
    public static final String CURRENT_PAGE_NUMBER_PLACEHOLDER = "%currentPage%";
    public static final String NEXT_PAGE_NUMBER_PLACEHOLDER = "%nextPage%";
    public static final String MAX_PAGE_NUMBER_PLACEHOLDER = "%maxPage%";

    /**
     * Creates a list of max-size ItemStacks for the given Material totalling to the given size
     * @param material The Material of the ItemStacks
     * @param stackSize The total amount of the combined ItemStacks
     * @return A List of ItemStacks
     */
    public static List<ItemStack> getMaterialAmountAsItemStacks(Material material, int stackSize) {
        int maxStackSize = material.getMaxStackSize();
        if (maxStackSize <= 0)
            return Collections.emptyList();

        int remaining = stackSize;
        List<ItemStack> stackItems = new ArrayList<>();
        while (remaining - maxStackSize > 0) {
            remaining -= maxStackSize;
            stackItems.add(new ItemStack(material, maxStackSize));
        }
        if (remaining > 0)
            stackItems.add(new ItemStack(material, remaining));
        return stackItems;
    }

    /**
     * Fills an entire screen with an item
     *
     * @param screen The screen to fill
     * @param fillItem The item to fill with
     */
    public static void fillScreen(GuiScreen screen, ItemStack fillItem) {
        GuiSize size = screen.getCurrentSize();
        if (!size.name().startsWith("ROWS") && size != GuiSize.DYNAMIC)
            throw new IllegalArgumentException("Cannot fill borders of a non-chest GUI");

        for (int i = 0; i < size.getNumSlots(); i++)
            screen.addItemStackAt(i, fillItem);
    }

    /**
     * Fills the borders of the screen with an item
     *
     * @param screen The screen to fill
     * @param fillItem The item to fill borders with
     */
    public static void fillBorders(GuiScreen screen, ItemStack fillItem) {
        GuiSize size = screen.getCurrentSize();
        if (!size.name().startsWith("ROWS"))
            throw new IllegalArgumentException("Cannot fill borders of a non-chest GUI");

        fillRow(screen, 0, fillItem);
        fillRow(screen, size.getRows() - 1, fillItem);
        fillColumn(screen, 0, fillItem);
        fillColumn(screen, size.getCols() - 1, fillItem);
    }

    /**
     * Fills a row of the screen with an item
     *
     * @param screen The screen to fill
     * @param row The row number to fill
     * @param fillItem The item to fill the row with
     */
    public static void fillRow(GuiScreen screen, int row, ItemStack fillItem) {
        GuiSize size = screen.getCurrentSize();
        if (!size.name().startsWith("ROWS"))
            throw new IllegalArgumentException("Cannot fill borders of a non-chest GUI");

        if (row < 0 || row > size.getRows())
            throw new IllegalArgumentException(String.format("row must be between 0 and %d inclusive for this GUI", size.getRows() - 1));

        for (int i = row * 9; i < row * 9 + size.getCols(); i++)
            screen.addItemStackAt(i, fillItem);
    }

    /**
     * Fills a column of the screen with an item
     *
     * @param screen The screen to fill
     * @param column The column number to fill
     * @param fillItem The item to fill the column with
     */
    public static void fillColumn(GuiScreen screen, int column, ItemStack fillItem) {
        GuiSize size = screen.getCurrentSize();
        if (!size.name().startsWith("ROWS"))
            throw new IllegalArgumentException("Cannot fill borders of a non-chest GUI");

        if (column < 0 || column > size.getCols())
            throw new IllegalArgumentException(String.format("column must be between 0 and %d inclusive for this GUI", size.getCols() - 1));

        for (int i = column; i < size.getRows() * 9; i += 9)
            screen.addItemStackAt(i, fillItem);
    }

    /**
     * Gets a dynamic GuiSize from a collection of slot indices
     *
     * @param slots The slot indices
     * @return a dynamic GuiSize
     */
    public static GuiSize getGuiSizeFromSlots(Collection<Integer> slots) {
        return GuiSize.fromRows(Math.min((((int) (slots.stream().max(Integer::compareTo).orElse(0) / 9.0)) + 1) * 9, GuiUtil.ROW_6_END + 1));
    }

    /**
     * Gets a slot id from a row and column
     *
     * @param row The row
     * @param col The column
     * @return the slot id
     */
    public static int slotFromCoordinate(int row, int col) {
        return row * 9 + col;
    }

}
