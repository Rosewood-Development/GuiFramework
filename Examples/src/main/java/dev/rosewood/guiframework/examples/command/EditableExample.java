package dev.rosewood.guiframework.examples.command;

import dev.rosewood.guiframework.GuiFactory;
import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.examples.GuiFrameworkExamples;
import dev.rosewood.guiframework.examples.util.HexUtils;
import dev.rosewood.guiframework.framework.util.GuiUtil;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EditableExample extends SubCommand {

    public EditableExample(GuiFrameworkExamples plugin, GuiFramework guiFramework) {
        super(plugin, guiFramework, "editable");
    }

    @Override
    public void execute(Player player) {
        GuiContainer container = GuiFactory.createContainer()
                .setTickRate(1)
                .setPersistent(false);

        GuiScreen screen = GuiFactory.createScreen(container, GuiSize.ROWS_ONE)
                .setTitle("Editable Example")
                .setEditableSection(0, 8, Collections.singletonList(new ItemStack(Material.AIR)), (p, item) -> {});

        for (int i = 0; i < 9; i++) {
            int index = i;
            screen.addSlotListener(index, item -> HexUtils.sendMessage(player, "&eSlot " + index + " was changed to " + item));
        }

        // Add the screen, register the container, and show to the player
        container.addScreen(screen);
        this.guiFramework.getGuiManager().registerGui(container);
        container.openFor(player);
    }

}
