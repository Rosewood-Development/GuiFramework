package dev.rosewood.guiframework.framework.gui;

import dev.rosewood.guiframework.framework.util.VersionUtils;
import dev.rosewood.guiframework.gui.ClickAction;
import dev.rosewood.guiframework.gui.ClickActionType;
import dev.rosewood.guiframework.gui.GuiButton;
import dev.rosewood.guiframework.gui.GuiButtonFlag;
import dev.rosewood.guiframework.gui.GuiIcon;
import dev.rosewood.guiframework.gui.GuiString;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrameworkButton implements GuiButton {

    private Supplier<GuiIcon> icon;
    private Supplier<Integer> amount;
    private Supplier<GuiString> name;
    private Supplier<List<GuiString>> lore;
    private Supplier<Boolean> glowing;
    private Supplier<Sound> clickSound;
    private Supplier<Float> clickVolume;
    private Supplier<Float> clickPitch;
    private Supplier<Boolean> visibility;
    private Supplier<ItemStack> hiddenReplacement;
    private Supplier<List<ItemFlag>> itemFlags;

    private Map<ClickActionType, Function<InventoryClickEvent, ClickAction>> clickActions;
    private Set<GuiButtonFlag> flags;

    private ItemStack itemStack;
    private boolean forcedItemStack;

    public FrameworkButton() {
        FrameworkIcon frameworkIcon = new FrameworkIcon();
        FrameworkString frameworkString = new FrameworkString();

        this.icon = () -> frameworkIcon;
        this.amount = () -> 1;
        this.name = () -> frameworkString;
        this.lore = Collections::emptyList;
        this.glowing = () -> false;
        this.clickSound = () -> null;
        this.clickVolume = () -> 0.5F;
        this.clickPitch = () -> 1.0F;
        this.visibility = null;
        this.hiddenReplacement = null;
        this.itemFlags = Collections::emptyList;

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
        FrameworkIcon icon = new FrameworkIcon(iconMaterial);
        this.icon = () -> icon;

        return this;
    }

    @Override
    public FrameworkButton setIcon(Material iconMaterial, Consumer<ItemMeta> itemMetaApplier) {
        FrameworkIcon icon = new FrameworkIcon(iconMaterial, itemMetaApplier);
        this.icon = () -> icon;

        return this;
    }

    @Override
    public FrameworkButton setIcon(GuiIcon icon) {
        this.icon = () -> icon;

        return this;
    }

    @Override
    public FrameworkButton setAmount(int amount) {
        this.amount = () -> amount;

        return this;
    }

    @Override
    public FrameworkButton setName(GuiString name) {
        this.name = () -> name;

        return this;
    }

    @Override
    public FrameworkButton setName(String name) {
        FrameworkString string = new FrameworkString(name);
        this.name = () -> string;

        return this;
    }

    @Override
    public FrameworkButton setLore(GuiString... lore) {
        List<GuiString> list = Arrays.asList(lore);
        this.lore = () -> list;

        return this;
    }

    @Override
    public FrameworkButton setLore(String... lore) {
        List<GuiString> list = Arrays.stream(lore).map(FrameworkString::new).collect(Collectors.toList());
        this.lore = () -> list;

        return this;
    }

    @Override
    public FrameworkButton setLore(List<String> lore) {
        List<GuiString> list = lore.stream().map(FrameworkString::new).collect(Collectors.toList());
        this.lore = () -> list;

        return this;
    }

    @Override
    public FrameworkButton setGlowing(boolean glowing) {
        this.glowing = () -> glowing;

        return this;
    }

    @Override
    public FrameworkButton setClickSound(Sound sound) {
        this.clickSound = () -> sound;

        return this;
    }

    @Override
    public FrameworkButton setClickSound(Sound sound, float volume, float pitch) {
        this.clickSound = () -> sound;
        this.clickVolume = () -> volume;
        this.clickPitch = () -> pitch;

        return this;
    }

    @Override
    public FrameworkButton setClickVolume(float volume) {
        this.clickVolume = () -> volume;

        return this;
    }

    @Override
    public FrameworkButton setClickPitch(float pitch) {
        this.clickPitch = () -> pitch;

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
    public FrameworkButton setVisibility(Boolean visibility) {
        if (visibility == null) {
            this.visibility = null;
        } else {
            this.visibility = () -> visibility;
        }

        return this;
    }

    @Override
    public FrameworkButton setHiddenReplacement(ItemStack itemStack) {
        if (itemStack == null) {
            this.hiddenReplacement = null;
        } else {
            ItemStack clone = itemStack.clone();
            this.hiddenReplacement = () -> clone;
        }

        return this;
    }

    @Override
    public FrameworkButton setItemFlags(ItemFlag... itemFlags) {
        List<ItemFlag> list = Arrays.asList(itemFlags);
        this.itemFlags = () -> list;

        return this;
    }

    //endregion

    //region Supplier Setters

    @Override
    public FrameworkButton setIconSupplier(Supplier<GuiIcon> iconSupplier) {
        Objects.requireNonNull(iconSupplier);
        this.icon = iconSupplier;

        return this;
    }

    @Override
    public FrameworkButton setAmountSupplier(Supplier<Integer> amountSupplier) {
        Objects.requireNonNull(amountSupplier);
        this.amount = amountSupplier;

        return this;
    }

    @Override
    public FrameworkButton setNameSupplier(Supplier<GuiString> nameSupplier) {
        Objects.requireNonNull(nameSupplier);
        this.name = nameSupplier;

        return this;
    }

    @Override
    public FrameworkButton setLoreSupplier(Supplier<List<GuiString>> loreSupplier) {
        Objects.requireNonNull(loreSupplier);
        this.lore = loreSupplier;

        return this;
    }

    @Override
    public FrameworkButton setGlowingSupplier(Supplier<Boolean> glowingSupplier) {
        Objects.requireNonNull(glowingSupplier);
        this.glowing = glowingSupplier;

        return this;
    }

    @Override
    public FrameworkButton setClickSoundSupplier(Supplier<Sound> clickSoundSupplier) {
        Objects.requireNonNull(clickSoundSupplier);
        this.clickSound = clickSoundSupplier;

        return this;
    }

    @Override
    public FrameworkButton setClickSoundSupplier(Supplier<Sound> clickSoundSupplier, float volume, float pitch) {
        Objects.requireNonNull(clickSoundSupplier);
        this.clickSound = clickSoundSupplier;
        this.clickVolume = () -> volume;
        this.clickPitch = () -> pitch;

        return this;
    }

    @Override
    public FrameworkButton setClickVolumeSupplier(Supplier<Float> clickVolumeSupplier) {
        Objects.requireNonNull(clickVolumeSupplier);
        this.clickVolume = clickVolumeSupplier;

        return this;
    }

    @Override
    public FrameworkButton setClickPitchSupplier(Supplier<Float> clickPitchSupplier) {
        Objects.requireNonNull(clickPitchSupplier);
        this.clickVolume = clickPitchSupplier;

        return this;
    }

    @Override
    public FrameworkButton setVisibilitySupplier(Supplier<Boolean> visibilitySupplier) {
        Objects.requireNonNull(visibilitySupplier);
        this.visibility = visibilitySupplier;

        return this;
    }

    @Override
    public GuiButton setHiddenReplacementSupplier(Supplier<ItemStack> hiddenReplacementSupplier) {
        Objects.requireNonNull(hiddenReplacementSupplier);
        this.hiddenReplacement = hiddenReplacementSupplier;

        return this;
    }

    @Override
    public FrameworkButton setItemFlagsSupplier(Supplier<List<ItemFlag>> itemFlagsSupplier) {
        Objects.requireNonNull(itemFlagsSupplier);
        this.itemFlags = itemFlagsSupplier;

        return this;
    }

    //endregion

    //region Property Getters

    @NotNull
    private GuiIcon getIcon() {
        return this.icon.get();
    }

    private int getAmount() {
        return this.amount.get();
    }

    @NotNull
    private GuiString getName() {
        return this.name.get();
    }

    @NotNull
    private List<GuiString> getLore() {
        return this.lore.get();
    }

    private boolean isGlowing() {
        return this.glowing.get();
    }

    @Nullable
    private Sound getClickSound() {
        return this.clickSound.get();
    }

    private float getClickVolume() {
        return this.clickVolume.get();
    }

    private float getClickPitch() {
        return this.clickPitch.get();
    }

    @Override
    public boolean isVisible(int pageNumber, int maxPageNumber) {
        if (this.visibility != null)
            return this.visibility.get();

        if (pageNumber == 1 && this.flags.contains(GuiButtonFlag.HIDE_IF_FIRST_PAGE))
            return false;

        return pageNumber != maxPageNumber || !this.flags.contains(GuiButtonFlag.HIDE_IF_LAST_PAGE);
    }

    private ItemFlag[] getItemFlags() {
        return this.itemFlags.get().toArray(new ItemFlag[0]);
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
            return this.hiddenReplacement == null ? null : this.hiddenReplacement.get();

        ItemStack itemStack = this.itemStack;
        FrameworkIcon icon = (FrameworkIcon) this.getIcon();

        if (this.forcedItemStack)
            itemStack = itemStack.clone();

        if (this.itemStack == null)
            this.itemStack = new ItemStack(icon.getMaterial(), this.getAmount());

        if (!icon.isEmpty())
            this.itemStack.setType(icon.getMaterial());

        int amount = this.getAmount();
        if (this.itemStack.getAmount() == 1 && amount != 1)
            this.itemStack.setAmount(this.getAmount());

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
                itemMeta.addEnchant(VersionUtils.INFINITY, 1, true);

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
        this.icon.get().tick();
        this.name.get().tick();
        this.lore.get().forEach(GuiString::tick);
    }

}
