package dev.rosewood.guiframework.examples.command;

import dev.rosewood.guiframework.GuiFactory;
import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.examples.GuiFrameworkExamples;
import dev.rosewood.guiframework.examples.util.HexUtils;
import dev.rosewood.guiframework.framework.util.GuiUtil;
import dev.rosewood.guiframework.gui.ClickAction;
import dev.rosewood.guiframework.gui.GuiButtonFlag;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.screen.GuiPageContentsResult;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import dev.rosewood.guiframework.gui.screen.GuiScreenSection;
import dev.rosewood.guiframework.gui.screen.PageContentsRequester;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PaginatedExample extends SubCommand {

    private static final List<Material> BLOCK_MATERIALS, ITEM_MATERIALS;

    static {
        BLOCK_MATERIALS = new ArrayList<>();
        ITEM_MATERIALS = new ArrayList<>();

        for (Material material : Material.values()) {
            if (!material.isItem() || material.isAir())
                continue;

            if (material.isBlock()) {
                BLOCK_MATERIALS.add(material);
            } else {
                ITEM_MATERIALS.add(material);
            }
        }

        BLOCK_MATERIALS.sort(Comparator.comparing(Enum::name));
        ITEM_MATERIALS.sort(Comparator.comparing(Enum::name));
    }

    public PaginatedExample(GuiFrameworkExamples plugin, GuiFramework guiFramework) {
        super(plugin, guiFramework, "paginated");
    }

    @Override
    public void execute(Player player) {
        GuiContainer container = GuiFactory.createContainer()
                .setTickRate(1)
                .setPersistent(false);

        GuiScreen selectMaterialTypeScreen = GuiFactory.createScreen(container, GuiSize.ROWS_THREE)
                .setTitle("View Material Type");

        GuiScreen browseScreen = GuiFactory.createScreen(container, GuiSize.ROWS_SIX);

        // Fill item
        ItemStack borderItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderItemMeta = borderItem.getItemMeta();
        if (borderItemMeta != null) {
            borderItemMeta.setDisplayName(" ");
            borderItem.setItemMeta(borderItemMeta);
        }

        GuiUtil.fillScreen(selectMaterialTypeScreen, borderItem);
        GuiUtil.fillBorders(browseScreen, borderItem);

        // Selectable options of either block or item
        AtomicReference<List<Material>> targetList = new AtomicReference<>(null);
        GuiScreenSection paginatedSection = GuiFactory.createScreenSection(10, 16)
                .addSlotRange(19, 25)
                .addSlotRange(28, 34)
                .addSlotRange(37, 43);

        PageContentsRequester pageContentsRequester = (pageNumber, startIndex, endIndex) -> {
            GuiPageContentsResult results = GuiFactory.createPageContentsResult();
            List<Material> materials = targetList.get();

            for (int i = startIndex; i <= Math.min(endIndex, materials.size() - 1); i++) {
                Material material = materials.get(i);
                results.addPageContent(GuiFactory.createButton()
                        .setIcon(material)
                        .setName(ChatColor.AQUA + WordUtils.capitalizeFully(material.name().replace('_', ' '))));
            }

            return results;
        };

        selectMaterialTypeScreen.addButtonAt(12, GuiFactory.createButton()
                .setIcon(Material.GRASS_BLOCK)
                .setNameSupplier(() -> GuiFactory.createString(HexUtils.colorify("<g#50:#C96:#5A3825>View Blocks")))
                .setLore(ChatColor.AQUA + "Click to view all blocks")
                .setClickAction(event -> {
                    targetList.set(BLOCK_MATERIALS);
                    browseScreen.setPaginatedSection(paginatedSection, BLOCK_MATERIALS.size(), pageContentsRequester);
                    browseScreen.setTitle("Viewing Blocks");
                    browseScreen.rebuild();
                    return ClickAction.TRANSITION_FORWARDS;
                }));

        selectMaterialTypeScreen.addButtonAt(14, GuiFactory.createButton()
                .setIcon(Material.GOLD_INGOT)
                .setNameSupplier(() -> GuiFactory.createString(HexUtils.colorify("<g#50:#FFF77A:#FFB404>View Items")))
                .setLore(ChatColor.AQUA + "Click to view all items")
                .setClickAction(event -> {
                    targetList.set(ITEM_MATERIALS);
                    browseScreen.setPaginatedSection(paginatedSection, ITEM_MATERIALS.size(), pageContentsRequester);
                    browseScreen.setTitle("Viewing Items");
                    browseScreen.rebuild();
                    return ClickAction.TRANSITION_FORWARDS;
                }));

        browseScreen.addButtonAt(45, GuiFactory.createButton()
                .setIcon(Material.ARROW)
                .setName(ChatColor.YELLOW + "Go Back")
                .setClickAction(event -> ClickAction.TRANSITION_BACKWARDS))
                .addButtonAt(47, GuiFactory.createButton()
                        .setIcon(Material.PAPER)
                        .setName(ChatColor.YELLOW + "Previous Page (" + GuiUtil.PREVIOUS_PAGE_NUMBER_PLACEHOLDER + "/" + GuiUtil.MAX_PAGE_NUMBER_PLACEHOLDER + ")")
                        .setClickAction(event -> ClickAction.PAGE_BACKWARDS)
                        .setFlags(GuiButtonFlag.HIDE_IF_FIRST_PAGE)
                        .setHiddenReplacement(borderItem))
                .addButtonAt(51, GuiFactory.createButton()
                        .setIcon(Material.PAPER)
                        .setName(ChatColor.YELLOW + "Next Page (" + GuiUtil.NEXT_PAGE_NUMBER_PLACEHOLDER + "/" + GuiUtil.MAX_PAGE_NUMBER_PLACEHOLDER + ")")
                        .setClickAction(event -> ClickAction.PAGE_FORWARDS)
                        .setFlags(GuiButtonFlag.HIDE_IF_LAST_PAGE)
                        .setHiddenReplacement(borderItem));

        // Add the screens, register the container, and show to the player
        container.addScreen(selectMaterialTypeScreen);
        container.addScreen(browseScreen);
        this.guiFramework.getGuiManager().registerGui(container);
        container.openFor(player);
    }

}
