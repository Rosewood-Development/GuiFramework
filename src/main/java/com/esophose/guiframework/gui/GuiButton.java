package com.esophose.guiframework.gui;

import com.esophose.guiframework.gui.screen.ISlotable;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GuiButton implements ITickable, ISlotable {

    private ItemStack itemStack;
    private GuiIcon icon;
    private int amount;
    private GuiString name;
    private List<GuiString> lore;
    private boolean glowing;
    private Map<ClickActionType, Function<InventoryClickEvent, ClickAction>> onClickActions;

    public GuiButton() {
        this.itemStack = null;
        this.icon = new GuiIcon();
        this.amount = 1;
        this.name = new GuiString();
        this.lore = new ArrayList<>();
        this.glowing = false;
        this.onClickActions = new HashMap<>();
    }

    public GuiButton(@NotNull ItemStack itemStack) {
        this();
        this.itemStack = itemStack;
    }

    @NotNull
    public GuiButton setIcon(@NotNull Material iconMaterial, byte iconData) {
        this.icon = new GuiIcon(iconMaterial, iconData);

        return this;
    }

    @NotNull
    public GuiButton setIcon(@NotNull Material iconMaterial) {
        this.icon = new GuiIcon(iconMaterial);

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
    public GuiButton setGlowing(boolean glowing) {
        this.glowing = glowing;

        return this;
    }

    @NotNull
    public GuiButton setClickAction(@NotNull Function<InventoryClickEvent, ClickAction> onClick, @NotNull ClickActionType... clickActionTypes) {
        if (clickActionTypes.length == 0) {
            this.onClickActions.put(ClickActionType.ALL, onClick);
        } else {
            for (ClickActionType clickActionType : clickActionTypes)
                this.onClickActions.put(clickActionType, onClick);
        }

        return this;
    }

    /**
     * Gets the ItemStack that represents this button
     *
     * @return An ItemStack with all this button's settings applied
     */
    @Override
    @NotNull
    public ItemStack getItemStack() {
        if (this.itemStack == null)
            this.itemStack = new ItemStack(this.icon.getMaterial(), this.amount, this.icon.getData());

        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null)
            return this.itemStack;

        if (this.name != null)
            itemMeta.setDisplayName(this.name.toString());

        if (this.lore != null)
            itemMeta.setLore(this.lore.stream().map(GuiString::toString).collect(Collectors.toList()));

        itemMeta.addItemFlags(ItemFlag.values());

        if (this.glowing)
            itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

        this.itemStack.setItemMeta(itemMeta);

        return this.itemStack;
    }

    /**
     * Executes the click function stored on this button
     *
     * @param event The InventoryClickEvent that triggered this button click
     */
    public ClickAction click(@NotNull InventoryClickEvent event) {
        Function<InventoryClickEvent, ClickAction> onClick = null;

        if (this.onClickActions.containsKey(ClickActionType.ALL)) {
            onClick = this.onClickActions.get(ClickActionType.ALL);
        } else if (event.isLeftClick()) {
            if (event.isShiftClick()) {
                onClick = this.onClickActions.get(ClickActionType.SHIFT_LEFT_CLICK);
            } else {
                onClick = this.onClickActions.get(ClickActionType.LEFT_CLICK);
            }
        } else if (event.isRightClick()) {
            if (event.isShiftClick()) {
                onClick = this.onClickActions.get(ClickActionType.SHIFT_RIGHT_CLICK);
            } else {
                onClick = this.onClickActions.get(ClickActionType.RIGHT_CLICK);
            }
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
