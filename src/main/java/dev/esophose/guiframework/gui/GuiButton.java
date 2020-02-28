package dev.esophose.guiframework.gui;

import dev.esophose.guiframework.gui.screen.Slotable;
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

public class GuiButton implements Tickable, Slotable {

    private GuiIcon icon;
    private int amount;
    private GuiString name;
    private List<GuiString> lore;
    private boolean glowing;
    private Sound clickSound;
    private float clickVolume, clickPitch;
    private ItemStack hiddenReplacement;

    private Supplier<GuiIcon> iconSupplier;
    private Supplier<Integer> amountSupplier;
    private Supplier<GuiString> nameSupplier;
    private Supplier<List<GuiString>> loreSupplier;
    private Supplier<Boolean> glowingSupplier;
    private Supplier<Sound> clickSoundSupplier;
    private Supplier<Boolean> visibilitySupplier;

    private Map<ClickActionType, Function<InventoryClickEvent, ClickAction>> clickActions;
    private Set<GuiButtonFlag> flags;

    private ItemStack itemStack;

    public GuiButton() {
        this.icon = new GuiIcon();
        this.amount = 1;
        this.name = new GuiString();
        this.lore = new ArrayList<>();
        this.glowing = false;
        this.clickSound = null;
        this.clickVolume = 0.5F;
        this.clickPitch = 1.0F;
        this.hiddenReplacement = null;

        this.iconSupplier = null;
        this.amountSupplier = null;
        this.nameSupplier = null;
        this.loreSupplier = null;
        this.glowingSupplier = null;
        this.clickSoundSupplier = null;
        this.visibilitySupplier = null;

        this.clickActions = new HashMap<>();
        this.flags = new HashSet<>();

        this.itemStack = null;
    }

    public GuiButton(@NotNull ItemStack itemStack) {
        this();
        this.itemStack = itemStack;
    }

    //region Normal Setters

    @NotNull
    public GuiButton setIcon(@NotNull Material iconMaterial) {
        this.icon = new GuiIcon(iconMaterial);

        return this;
    }

    @NotNull
    public GuiButton setIcon(@NotNull Material iconMaterial, @NotNull Consumer<ItemMeta> itemMetaApplier) {
        this.icon = new GuiIcon(iconMaterial, itemMetaApplier);

        return this;
    }

    @NotNull
    public GuiButton setIcon(@NotNull GuiIcon icon) {
        this.icon = icon;

        return this;
    }

    @NotNull
    public GuiButton setAmount(int amount) {
        this.amount = amount;

        return this;
    }

    @NotNull
    public GuiButton setName(@NotNull GuiString name) {
        this.name = name;

        return this;
    }

    @NotNull
    public GuiButton setName(@NotNull String name) {
        this.name = new GuiString(name);

        return this;
    }

    @NotNull
    public GuiButton setLore(@NotNull GuiString... lore) {
        this.lore = Arrays.asList(lore);

        return this;
    }

    @NotNull
    public GuiButton setLore(@NotNull String... lore) {
        this.lore = Arrays.stream(lore).map(GuiString::new).collect(Collectors.toList());

        return this;
    }

    @NotNull
    public GuiButton setLore(@NotNull List<String> lore) {
        this.lore = lore.stream().map(GuiString::new).collect(Collectors.toList());

        return this;
    }

    @NotNull
    public GuiButton setGlowing(boolean glowing) {
        this.glowing = glowing;

        return this;
    }

    @NotNull
    public GuiButton setClickSound(@Nullable Sound sound) {
        this.clickSound = sound;

        return this;
    }

    @NotNull
    public GuiButton setClickSound(@NotNull Sound sound, float volume, float pitch) {
        this.clickSound = sound;
        this.clickVolume = volume;
        this.clickPitch = pitch;

        return this;
    }

    @NotNull
    public GuiButton setClickAction(@NotNull Function<InventoryClickEvent, ClickAction> onClick, @NotNull ClickActionType... clickActionTypes) {
        if (clickActionTypes.length == 0) {
            this.clickActions.put(ClickActionType.ALL, onClick);
        } else {
            for (ClickActionType clickActionType : clickActionTypes)
                this.clickActions.put(clickActionType, onClick);
        }

        return this;
    }

    @NotNull
    public GuiButton setFlags(@NotNull GuiButtonFlag... flags) {
        this.flags = new HashSet<>(Arrays.asList(flags));

        return this;
    }

    @NotNull
    public GuiButton setHiddenReplacement(@Nullable ItemStack itemStack) {
        this.hiddenReplacement = itemStack;

        return this;
    }

    //endregion

    //region Supplier Setters

    @NotNull
    public GuiButton setIconSupplier(@NotNull Supplier<GuiIcon> iconSupplier) {
        this.iconSupplier = iconSupplier;

        return this;
    }

    @NotNull
    public GuiButton setAmountSupplier(@NotNull Supplier<Integer> amountSupplier) {
        this.amountSupplier = amountSupplier;

        return this;
    }

    @NotNull
    public GuiButton setNameSupplier(@NotNull Supplier<GuiString> nameSupplier) {
        this.nameSupplier = nameSupplier;

        return this;
    }

    @NotNull
    public GuiButton setLoreSupplier(@NotNull Supplier<List<GuiString>> loreSupplier) {
        this.loreSupplier = loreSupplier;

        return this;
    }

    @NotNull
    public GuiButton setGlowingSupplier(@NotNull Supplier<Boolean> glowingSupplier) {
        this.glowingSupplier = glowingSupplier;

        return this;
    }

    @NotNull
    public GuiButton setClickSoundSupplier(@NotNull Supplier<Sound> clickSoundSupplier) {
        this.clickSoundSupplier = clickSoundSupplier;

        return this;
    }

    @NotNull
    public GuiButton setClickSoundSupplier(@NotNull Supplier<Sound> clickSoundSupplier, float volume, float pitch) {
        this.clickSoundSupplier = clickSoundSupplier;
        this.clickVolume = volume;
        this.clickPitch = pitch;

        return this;
    }

    @NotNull
    public GuiButton setVisibilitySupplier(@NotNull Supplier<Boolean> visibilitySupplier) {
        this.visibilitySupplier = visibilitySupplier;

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

        GuiIcon icon = this.getIcon();
        this.itemStack = new ItemStack(icon.getMaterial(), this.getAmount());

        ItemMeta itemMeta = icon.getItemMeta().clone();
        itemMeta.setDisplayName(this.getName().toString());
        itemMeta.setLore(this.getLore().stream().map(GuiString::toString).collect(Collectors.toList()));
        itemMeta.addItemFlags(ItemFlag.values());

        if (this.isGlowing())
            itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

        this.itemStack.setItemMeta(itemMeta);

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
