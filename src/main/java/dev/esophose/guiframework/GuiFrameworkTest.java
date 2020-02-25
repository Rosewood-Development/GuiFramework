package dev.esophose.guiframework;

import dev.esophose.guiframework.gui.ClickAction;
import dev.esophose.guiframework.gui.GuiButton;
import dev.esophose.guiframework.gui.GuiButtonFlag;
import dev.esophose.guiframework.gui.GuiContainer;
import dev.esophose.guiframework.gui.GuiIcon;
import dev.esophose.guiframework.gui.GuiSize;
import dev.esophose.guiframework.gui.GuiString;
import dev.esophose.guiframework.gui.screen.GuiPageContentsResult;
import dev.esophose.guiframework.gui.screen.GuiScreen;
import dev.esophose.guiframework.gui.screen.GuiScreenEditFilters;
import dev.esophose.guiframework.gui.screen.GuiScreenSection;
import dev.esophose.guiframework.util.GuiUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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

        GuiContainer container = new GuiContainer()
                .setTickRate(5);

        List<Integer> paginatedSlots = new ArrayList<>();
        for (int i = 10; i <= 16; i++) paginatedSlots.add(i);
        for (int i = 19; i <= 25; i++) paginatedSlots.add(i);
        for (int i = 28; i <= 34; i++) paginatedSlots.add(i);
        for (int i = 37; i <= 43; i++) paginatedSlots.add(i);

        GuiScreenSection paginatedSection = new GuiScreenSection(paginatedSlots);

        AtomicBoolean coinFlip = new AtomicBoolean();
        AtomicInteger numberFlipped = new AtomicInteger();

        GuiScreen screen = new GuiScreen(container, GuiSize.ROWS_SIX)
                .setTitle("All Items/Blocks")
                .setPaginatedSection(paginatedSection, this.materials.size(), (pageNumber, startIndex, endIndex) -> {
                    GuiPageContentsResult result = new GuiPageContentsResult();
                    for (int i = startIndex; i <= Math.min(endIndex, this.materials.size() - 1); i++) {
                        int buttonIndex = i;
                        GuiIcon guiIcon = new GuiIcon();
                        GuiString guiString = new GuiString();
                        for (int n = i; n < this.materials.size(); n++) {
                            Material material = this.materials.get(n);
                            guiIcon.addAnimationFrame(material);
                            guiString.addAnimationFrame(material.name());
                        }
                        for (int n = 0; n < i; n++) {
                            Material material = this.materials.get(n);
                            guiIcon.addAnimationFrame(material);
                            guiString.addAnimationFrame(material.name());
                        }

                        GuiButton button = new GuiButton()
                                .setName(guiString)
                                .setLore("Index: #" + i)
                                .setIcon(guiIcon)
                                .setClickAction((event) -> {
                                    player.sendMessage("You clicked on button #" + buttonIndex + " on page #" + pageNumber);
                                    return ClickAction.NOTHING;
                                })
                                .setClickSound(Sound.UI_BUTTON_CLICK);
                        result.addPageContent(button);
                    }
                    return result;
                })
                .addButtonAt(GuiUtil.ROW_6_START, new GuiButton()
                    .setName("Previous Page (" + GuiUtil.PREVIOUS_PAGE_NUMBER_PLACEHOLDER + "/" + GuiUtil.MAX_PAGE_NUMBER_PLACEHOLDER + ")")
                    .setLore("Go back a page")
                    .setIcon(Material.PAPER)
                    .setClickAction(event -> ClickAction.PAGE_BACKWARDS)
                    .setClickSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                    .setFlags(GuiButtonFlag.HIDE_IF_FIRST_PAGE))
                .addButtonAt(GuiUtil.ROW_6_END, new GuiButton()
                    .setName("Next Page (" + GuiUtil.NEXT_PAGE_NUMBER_PLACEHOLDER + "/" + GuiUtil.MAX_PAGE_NUMBER_PLACEHOLDER + ")")
                    .setLore("Go forward a page")
                    .setIcon(Material.PAPER)
                    .setClickAction(event -> ClickAction.PAGE_FORWARDS)
                    .setClickSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                    .setFlags(GuiButtonFlag.HIDE_IF_LAST_PAGE))
                .addButtonAt(GuiUtil.ROW_6_START + 4, new GuiButton()
                    .setName("Exit")
                    .setLore("Closes the GUI")
                    .setIcon(Material.BARRIER)
                    .setClickAction(event -> ClickAction.CLOSE)
                    .setClickSound(Sound.ENTITY_VILLAGER_NO))
                .addButtonAt(GuiUtil.ROW_6_START + 2, new GuiButton()
                    .setName("Decrease Update Speed")
                    .setLore("Slow and steady")
                    .setIcon(Material.SCUTE)
                    .setClickAction(event -> {
                        container.setTickRate(container.getTickRate() + 1);
                        return ClickAction.NOTHING;
                    })
                    .setClickSound(Sound.ENTITY_TURTLE_HURT_BABY))
                .addButtonAt(GuiUtil.ROW_6_END - 2, new GuiButton()
                    .setName("Increase Update Speed")
                    .setLore("Gotta go fast")
                    .setIcon(Material.RABBIT_FOOT)
                    .setClickAction(event -> {
                        container.setTickRate(Math.max(container.getTickRate() - 1, 1));
                        return ClickAction.NOTHING;
                    })
                    .setClickSound(Sound.ENTITY_TURTLE_HURT_BABY))
                .addButtonAt(GuiUtil.ROW_1_START + 4, new GuiButton()
                    .setName("&eForwards")
                    .setIcon(Material.ARROW)
                    .setClickAction(event -> ClickAction.TRANSITION_FORWARDS));

        GuiScreen screen2 = new GuiScreen(container, GuiSize.DYNAMIC)
                .setTitle("Coin flip game")
                .addButtonAt(GuiUtil.ROW_1_START + 4, new GuiButton()
                    .setIconSupplier(() -> {
                        if (numberFlipped.get() == 0) {
                            return new GuiIcon(Material.LAPIS_LAZULI);
                        } else {
                            if (coinFlip.get()) {
                                return new GuiIcon(Material.GOLD_INGOT);
                            } else {
                                return new GuiIcon(Material.IRON_INGOT);
                            }
                        }
                    })
                    .setNameSupplier(() -> {
                        if (numberFlipped.get() == 0) {
                            return new GuiString("&bClick to flip a coin!");
                        } else {
                            return new GuiString("&eClick to flip coin #" + (numberFlipped.get() + 1) + "!");
                        }
                    })
                    .setLoreSupplier(() -> {
                        if (numberFlipped.get() == 0) {
                            return Collections.emptyList();
                        } else {
                            if (coinFlip.get()) {
                                return Collections.singletonList(new GuiString("&aHeads!"));
                            } else {
                                return Collections.singletonList(new GuiString("&cTails!"));
                            }
                        }
                    })
                    .setGlowingSupplier(() -> numberFlipped.get() != 0)
                    .setClickAction(event -> {
                        numberFlipped.incrementAndGet();
                        coinFlip.set(Math.random() > 0.5);
                        return ClickAction.NOTHING;
                    }))
                .addButtonAt(GuiUtil.ROW_1_START, new GuiButton()
                    .setIcon(Material.ARROW)
                    .setName("&eBack")
                    .setClickAction(event -> ClickAction.TRANSITION_BACKWARDS))
                .addButtonAt(GuiUtil.ROW_1_END, new GuiButton()
                        .setName("&eForwards")
                        .setIcon(Material.ARROW)
                        .setClickAction(event -> ClickAction.TRANSITION_FORWARDS));

        Material stackType = Material.DIAMOND_BLOCK;
        int stackSize = 500;
        List<ItemStack> stackItems = GuiUtil.getMaterialAmountAsItemStacks(stackType, 500);
        GuiScreenEditFilters filters = new GuiScreenEditFilters();
        filters.setWhitelist(Material.DIAMOND_BLOCK);

        GuiScreen screen3 = new GuiScreen(container, GuiSize.ROWS_SIX)
                .setTitle("Edit Diamond Block Stack")
                .setEditableSection(paginatedSection, stackItems, editedItems -> {
                    int total = editedItems.stream().mapToInt(ItemStack::getAmount).sum();
                    int change = total - stackSize;
                    player.sendMessage("Stack size offset: " + change);
                })
                .setEditFilters(filters)
                .addButtonAt(GuiUtil.ROW_6_START + 4, new GuiButton()
                        .setName("Exit")
                        .setLore("Closes the GUI")
                        .setIcon(Material.BARRIER)
                        .setClickAction(event -> ClickAction.CLOSE)
                        .setClickSound(Sound.ENTITY_VILLAGER_NO));

        //container.addScreen(screen);
        //container.addScreen(screen2);
        container.addScreen(screen3);

        guiFramework.getGuiManager().registerGui(container);

        container.openFor(player);

        return true;
    }
}
