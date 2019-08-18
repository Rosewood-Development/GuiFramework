package com.esophose.guiframework;

import com.esophose.guiframework.gui.ClickActionType;
import com.esophose.guiframework.gui.GuiButton;
import com.esophose.guiframework.gui.ClickAction;
import com.esophose.guiframework.gui.GuiContainer;
import com.esophose.guiframework.gui.GuiSize;
import com.esophose.guiframework.gui.screen.GuiPageContentsResult;
import com.esophose.guiframework.gui.screen.GuiScreen;
import com.esophose.guiframework.gui.screen.ISlotable;
import com.esophose.guiframework.util.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class GuiFrameworkTest extends JavaPlugin {

    private List<Material> materials;

    @Override
    public void onEnable() {
        this.materials = new ArrayList<>();

        Inventory testInventory = Bukkit.createInventory(null, 9);
        for (Material material : Material.values()) {
            testInventory.clear(0);
            testInventory.setItem(0, new ItemStack(material, 1));
            if (testInventory.getItem(0) != null)
                this.materials.add(material);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("gui") || !(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        GuiFramework guiFramework = GuiFramework.instantiate(this);

        GuiContainer container = new GuiContainer();

        GuiScreen screen = new GuiScreen(container, GuiSize.ROWS_SIX)
                .setTitle("Test GUI")
                .setPaginatedSection(GuiUtil.ROW_1_START, GuiUtil.ROW_5_END, (pageNumber, startIndex, endIndex) -> {
                    List<ISlotable> contents = new ArrayList<>();
                    for (int i = startIndex; i <= Math.min(endIndex, this.materials.size() - 1); i++) {
                        int buttonIndex = i;
                        Material material = this.materials.get(i);
                        GuiButton button = new GuiButton()
                                .setName(material.name())
                                .setLore("Index: #" + i)
                                .setIcon(material)
                                .setClickAction((event) -> {
                                    player.sendMessage("You clicked on button #" + buttonIndex + " on page #" + pageNumber);
                                    return ClickAction.NOTHING;
                                });
                        contents.add(button);
                    }

                    return new GuiPageContentsResult(contents, endIndex >= this.materials.size());
                })
                .addButtonAt(GuiUtil.ROW_6_START, new GuiButton()
                    .setName("Backwards")
                    .setLore("Go back a page")
                    .setIcon(Material.PAPER)
                    .setClickAction(event -> ClickAction.PAGE_BACKWARDS))
                .addButtonAt(GuiUtil.ROW_6_END, new GuiButton()
                    .setName("Forwards")
                    .setLore("Go forward a page")
                    .setIcon(Material.PAPER)
                    .setClickAction(event -> ClickAction.PAGE_FORWARDS))
                .addButtonAt(GuiUtil.ROW_6_START + 4, new GuiButton()
                    .setName("Exit")
                    .setLore("Closes the GUI")
                    .setIcon(Material.BARRIER)
                    .setClickAction(event -> ClickAction.CLOSE));
        container.addScreen(screen);

        guiFramework.getGuiManager().registerGui(container);

        container.openFor(player);

        return true;
    }
}
