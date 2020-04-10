package dev.esophose.guiframework.framework.util;

import java.util.ArrayList;
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

}
