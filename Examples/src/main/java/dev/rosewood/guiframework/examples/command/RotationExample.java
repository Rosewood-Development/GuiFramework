package dev.rosewood.guiframework.examples.command;

import dev.rosewood.guiframework.GuiFactory;
import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.examples.GuiFrameworkExamples;
import dev.rosewood.guiframework.framework.util.GuiUtil;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RotationExample extends SubCommand {

    public RotationExample(GuiFrameworkExamples plugin, GuiFramework guiFramework) {
        super(plugin, guiFramework, "rotation");
    }

    @Override
    public void execute(Player player) {
        GuiContainer container = GuiFactory.createContainer()
                .setTickRate(3)
                .setPersistent(false);

        GuiScreen screen = GuiFactory.createScreen(container, GuiSize.ROWS_FIVE)
                .setTitle("Rotation Example");

        // Fill borders
        ItemStack fillItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderItemMeta = fillItem.getItemMeta();
        if (borderItemMeta != null) {
            borderItemMeta.setDisplayName(" ");
            fillItem.setItemMeta(borderItemMeta);
        }

        GuiUtil.fillScreen(screen, fillItem);

        screen.addItemStackAt(4, new ItemStack(Material.HOPPER));

        // Animated part
        int[] slots = { 12, 13, 14, 23, 32, 31, 30, 21 };
        AtomicInteger startIndex = new AtomicInteger();
        List<ItemStack> items = Arrays.asList(
                new ItemStack(Material.COAL), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.GOLD_INGOT),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE), new ItemStack(Material.LAPIS_LAZULI),
                new ItemStack(Material.EMERALD), new ItemStack(Material.NETHERITE_INGOT)
        );

        screen.setTickHandler(() -> {
            for (int i = 0; i < items.size(); i++) {
                int index = (startIndex.get() + i) % slots.length;
                int slot = slots[i];
                screen.addItemStackAt(slot, items.get(index));
            }
            startIndex.decrementAndGet();
            if (startIndex.get() < 0)
                startIndex.set(slots.length - 1);
        });

        // Add the screen, register the container, and show to the player
        container.addScreen(screen);
        this.guiFramework.getGuiManager().registerGui(container);
        container.openFor(player);
    }

}
