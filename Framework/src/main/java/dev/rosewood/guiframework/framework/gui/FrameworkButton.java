package dev.rosewood.guiframework.framework.gui;

import dev.rosewood.guiframework.gui.ClickAction;
import dev.rosewood.guiframework.gui.ClickActionType;
import dev.rosewood.guiframework.gui.GuiButton;
import dev.rosewood.guiframework.gui.GuiButtonFlag;
import dev.rosewood.guiframework.gui.GuiIcon;
import dev.rosewood.guiframework.gui.GuiString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrameworkButton implements GuiButton {

    private GuiIcon icon;
    private int amount;
    private GuiString name;
    private List<GuiString> lore;
    private boolean glowing;
    private Sound clickSound;
    private float clickVolume, clickPitch;
    private ItemStack hiddenReplacement;
    private ItemFlag[] itemFlags;

    private Supplier<GuiIcon> iconSupplier;
    private Supplier<Integer> amountSupplier;
    private Supplier<GuiString> nameSupplier;
    private Supplier<List<GuiString>> loreSupplier;
    private Supplier<Boolean> glowingSupplier;
    private Supplier<Sound> clickSoundSupplier;
    private Supplier<Boolean> visibilitySupplier;
    private Supplier<List<ItemFlag>> itemFlagsSupplier;

    private Map<ClickActionType, Function<InventoryClickEvent, ClickAction>> clickActions;
    private Set<GuiButtonFlag> flags;

    private ItemStack itemStack;
    private boolean forcedItemStack;

    public FrameworkButton() {
        this.icon = new FrameworkIcon();
        this.amount = 1;
        this.name = new FrameworkString();
        this.lore = new ArrayList<>();
        this.glowing = false;
        this.clickSound = null;
        this.clickVolume = 0.5F;
        this.clickPitch = 1.0F;
        this.hiddenReplacement = null;
        this.itemFlags = ItemFlag.values();

        this.iconSupplier = null;
        this.amountSupplier = null;
        this.nameSupplier = null;
        this.loreSupplier = null;
        this.glowingSupplier = null;
        this.clickSoundSupplier = null;
        this.visibilitySupplier = null;
        this.itemFlagsSupplier = null;

        this.clickActions = new HashMap<>();
        this.flags = new HashSet<>();

        this.itemStack = null;
        this.forcedItemStack = false;
    }

    public FrameworkButton(@NotNull ItemStack itemStack) {
        this();
        this.itemStack = itemStack;
        this.forcedItemStack = true;
    }

    //region Normal Setters

    @Override
    public FrameworkButton setIcon(Material iconMaterial) {
        this.icon = new FrameworkIcon(iconMaterial);

        return this;
    }

    @Override
    public FrameworkButton setIcon(Material iconMaterial, Consumer<ItemMeta> itemMetaApplier) {
        this.icon = new FrameworkIcon(iconMaterial, itemMetaApplier);

        return this;
    }

    @Override
    public FrameworkButton setIcon(GuiIcon icon) {
        this.icon = icon;

        return this;
    }

    @Override
    public FrameworkButton setAmount(int amount) {
        this.amount = amount;

        return this;
    }

    @Override
    public FrameworkButton setName(GuiString name) {
        this.name = name;

        return this;
    }

    @Override
    public FrameworkButton setName(String name) {
        this.name = new FrameworkString(name);

        return this;
    }

    @Override
    public FrameworkButton setLore(GuiString... lore) {
        this.lore = Arrays.asList(lore);

        return this;
    }

    @Override
    public FrameworkButton setLore(String... lore) {
        this.lore = Arrays.stream(lore).map(FrameworkString::new).collect(Collectors.toList());

        return this;
    }

    @Override
    public FrameworkButton setLore(List<String> lore) {
        this.lore = lore.stream().map(FrameworkString::new).collect(Collectors.toList());

        return this;
    }

    @Override
    public FrameworkButton setGlowing(boolean glowing) {
        this.glowing = glowing;

        return this;
    }

    @Override
    public FrameworkButton setClickSound(Sound sound) {
        this.clickSound = sound;

        return this;
    }

    @Override
    public FrameworkButton setClickSound(Sound sound, float volume, float pitch) {
        this.clickSound = sound;
        this.clickVolume = volume;
        this.clickPitch = pitch;

        return this;
    }

    @Override
    public FrameworkButton setClickAction(Function<InventoryClickEvent, ClickAction> onClick, ClickActionType... clickActionTypes) {
        if (clickActionTypes.length == 0) {
            this.clickActions.put(ClickActionType.ALL, onClick);
        } else {
            for (ClickActionType clickActionType : clickActionTypes)
                this.clickActions.put(clickActionType, onClick);
        }

        return this;
    }

    @Override
    public FrameworkButton setFlags(GuiButtonFlag... flags) {
        this.flags = new HashSet<>(Arrays.asList(flags));

        return this;
    }

    @Override
    public FrameworkButton setHiddenReplacement(ItemStack itemStack) {
        this.hiddenReplacement = itemStack;

        return this;
    }

    @Override
    public FrameworkButton setItemFlags(ItemFlag... itemFlags) {
        this.itemFlags = itemFlags;

        return this;
    }

    //endregion

    //region Supplier Setters

    @Override
    public FrameworkButton setIconSupplier(Supplier<GuiIcon> iconSupplier) {
        this.iconSupplier = iconSupplier;

        return this;
    }

    @Override
    public FrameworkButton setAmountSupplier(Supplier<Integer> amountSupplier) {
        this.amountSupplier = amountSupplier;

        return this;
    }

    @Override
    public FrameworkButton setNameSupplier(Supplier<GuiString> nameSupplier) {
        this.nameSupplier = nameSupplier;

        return this;
    }

    @Override
    public FrameworkButton setLoreSupplier(Supplier<List<GuiString>> loreSupplier) {
        this.loreSupplier = loreSupplier;

        return this;
    }

    @Override
    public FrameworkButton setGlowingSupplier(Supplier<Boolean> glowingSupplier) {
        this.glowingSupplier = glowingSupplier;

        return this;
    }

    @Override
    public FrameworkButton setClickSoundSupplier(Supplier<Sound> clickSoundSupplier) {
        this.clickSoundSupplier = clickSoundSupplier;

        return this;
    }

    @Override
    public FrameworkButton setClickSoundSupplier(Supplier<Sound> clickSoundSupplier, float volume, float pitch) {
        this.clickSoundSupplier = clickSoundSupplier;
        this.clickVolume = volume;
        this.clickPitch = pitch;

        return this;
    }

    @Override
    public FrameworkButton setVisibilitySupplier(Supplier<Boolean> visibilitySupplier) {
        this.visibilitySupplier = visibilitySupplier;

        return this;
    }

    @Override
    public FrameworkButton setItemFlagsSupplier(Supplier<List<ItemFlag>> itemFlagsSupplier) {
        this.itemFlagsSupplier = itemFlagsSupplier;

        return this;
    }

    //endregion

    //region Property Getters

    @NotNull
    private GuiIcon getIcon() {
        return this.iconSupplier != null ? this.iconSupplier.get() : this.icon;
    }

    private int getAmount() {
        return this.amountSupplier != null ? this.amountSupplier.get() : this.amount;
    }

    @NotNull
    private GuiString getName() {
        return this.nameSupplier != null ? this.nameSupplier.get() : this.name;
    }

    @NotNull
    private List<GuiString> getLore() {
        return this.loreSupplier != null ? this.loreSupplier.get() : this.lore;
    }

    private boolean isGlowing() {
        return this.glowingSupplier != null ? this.glowingSupplier.get() : this.glowing;
    }

    @Nullable
    private Sound getClickSound() {
        return this.clickSoundSupplier != null ? this.clickSoundSupplier.get() : this.clickSound;
    }

    private float getClickVolume() {
        return this.clickVolume;
    }

    private float getClickPitch() {
        return this.clickPitch;
    }

    @Override
    public boolean isVisible(int pageNumber, int maxPageNumber) {
        if (this.visibilitySupplier != null)
            return this.visibilitySupplier.get();

        if (pageNumber == 1 && this.flags.contains(GuiButtonFlag.HIDE_IF_FIRST_PAGE))
            return false;

        return pageNumber != maxPageNumber || !this.flags.contains(GuiButtonFlag.HIDE_IF_LAST_PAGE);
    }

    private ItemFlag[] getItemFlags() {
        return this.itemFlagsSupplier != null ? this.itemFlagsSupplier.get().toArray(new ItemFlag[0]) : this.itemFlags;
    }

    //endregion

    /**
     * Gets the ItemStack that represents this button
     *
     * @return An ItemStack with all this button's settings applied
     */
    @Override
    public ItemStack getItemStack(boolean isVisible) {
        if (!isVisible)
            return this.hiddenReplacement;

        ItemStack itemStack = this.itemStack;
        FrameworkIcon icon = (FrameworkIcon) this.getIcon();
        if (!this.forcedItemStack) {
            if (this.itemStack == null)
                this.itemStack = new ItemStack(icon.getMaterial(), this.getAmount());
        } else {
            itemStack = itemStack.clone();
            if (!icon.isEmpty())
                this.itemStack.setType(icon.getMaterial());

            int amount = this.getAmount();
            if (this.itemStack.getAmount() == 1 && amount != 1)
                this.itemStack.setAmount(this.getAmount());
        }

        ItemMeta itemMeta;
        if (!icon.isEmpty()) {
            itemMeta = icon.getItemMeta().clone();
        } else {
            itemMeta = itemStack.getItemMeta();
        }

        if (itemMeta != null) {
            itemMeta.setDisplayName(this.getName().toString());
            itemMeta.setLore(this.getLore().stream().map(GuiString::toString).collect(Collectors.toList()));

            if (itemMeta.getItemFlags().isEmpty())
                itemMeta.addItemFlags(this.getItemFlags());

            if (this.isGlowing())
                itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

            this.itemStack.setItemMeta(itemMeta);
        }

        return this.itemStack;
    }

    /**
     * Executes the click function stored on this button
     *
     * @param event The InventoryClickEvent that triggered this button click
     */
    @NotNull
    public ClickAction click(@NotNull InventoryClickEvent event) {
        Function<InventoryClickEvent, ClickAction> onClick = null;

        if (this.clickActions.containsKey(ClickActionType.ALL)) {
            onClick = this.clickActions.get(ClickActionType.ALL);
        } else if (event.isLeftClick()) {
            if (event.isShiftClick()) {
                onClick = this.clickActions.get(ClickActionType.SHIFT_LEFT_CLICK);
            } else {
                onClick = this.clickActions.get(ClickActionType.LEFT_CLICK);
            }
        } else if (event.isRightClick()) {
            if (event.isShiftClick()) {
                onClick = this.clickActions.get(ClickActionType.SHIFT_RIGHT_CLICK);
            } else {
                onClick = this.clickActions.get(ClickActionType.RIGHT_CLICK);
            }
        }

        Sound clickSound = this.getClickSound();
        if (clickSound != null && event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(), clickSound, this.getClickVolume(), this.getClickPitch());
        }

        if (onClick != null)
            return onClick.apply(event);

        return ClickAction.NOTHING;
    }

    @Override
    public void tick() {
        this.icon.tick();
        this.name.tick();
        this.lore.forEach(GuiString::tick);
    }

}
