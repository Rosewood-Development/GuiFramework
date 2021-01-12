package dev.rosewood.guiframework.examples.command;

import dev.rosewood.guiframework.GuiFactory;
import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.examples.GuiFrameworkExamples;
import dev.rosewood.guiframework.examples.util.HexUtils;
import dev.rosewood.guiframework.framework.util.GuiUtil;
import dev.rosewood.guiframework.gui.GuiButton;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class AnimatedExample extends SubCommand {

    public AnimatedExample(GuiFrameworkExamples plugin, GuiFramework guiFramework) {
        super(plugin, guiFramework, "animated");
    }

    @Override
    public void execute(Player player) {
        GuiContainer container = GuiFactory.createContainer()
                .setTickRate(1)
                .setPersistent(false);

        GuiScreen screen = GuiFactory.createScreen(container, GuiSize.ROWS_SIX)
                .setTitle("Animated Example");

        // Fill borders
        ItemStack borderItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderItemMeta = borderItem.getItemMeta();
        if (borderItemMeta != null) {
            borderItemMeta.setDisplayName(" ");
            borderItem.setItemMeta(borderItemMeta);
        }

        GuiUtil.fillBorders(screen, borderItem);

        // Animated part
        for (int row = 1; row <= 4; row++) {
            for (int column = 1; column <= 7; column++) {
                AtomicInteger hue = new AtomicInteger((column + row) * 30);
                GuiButton button = GuiFactory.createButton()
                        .setNameSupplier(() -> GuiFactory.createString(HexUtils.colorify("<r#5>Animated Example")))
                        .setLoreSupplier(() -> Collections.singletonList(GuiFactory.createString(HexUtils.colorify("<g#50:#8A2387:#E94057:#F27121>Rosewood Development"))))
                        .setIconSupplier(() -> {
                            int hueValue = hue.getAndSet(hue.get() + 5);
                            return GuiFactory.createIcon(Material.LEATHER_CHESTPLATE, meta -> {
                                LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
                                java.awt.Color color = java.awt.Color.getHSBColor(hueValue / 360F, 1.0F, 1.0F);
                                armorMeta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
                            });
                        });
                screen.addButtonAt(GuiUtil.slotFromCoordinate(row, column), button);
            }
        }

        // Add the screen, register the container, and show to the player
        container.addScreen(screen);
        this.guiFramework.getGuiManager().registerGui(container);
        container.openFor(player);
    }

}
