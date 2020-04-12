package dev.rosewood.guiframework.gui;

import dev.rosewood.guiframework.gui.screen.Slotable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuiButton extends Tickable, Slotable {

    //region Normal Setters

    @NotNull
    GuiButton setIcon(@NotNull Material iconMaterial);

    @NotNull
    GuiButton setIcon(@NotNull Material iconMaterial, @NotNull Consumer<ItemMeta> itemMetaApplier);

    @NotNull
    GuiButton setIcon(@NotNull GuiIcon icon);

    @NotNull
    GuiButton setAmount(int amount);

    @NotNull
    GuiButton setName(@NotNull String name);

    @NotNull
    GuiButton setName(@NotNull GuiString name);

    @NotNull
    GuiButton setLore(@NotNull String... lore);

    @NotNull
    GuiButton setLore(@NotNull List<String> lore);

    @NotNull
    GuiButton setLore(@NotNull GuiString... lore);

    @NotNull
    GuiButton setGlowing(boolean glowing);

    @NotNull
    GuiButton setClickSound(@Nullable Sound sound);

    @NotNull
    GuiButton setClickSound(@NotNull Sound sound, float volume, float pitch);

    @NotNull
    GuiButton setClickAction(@NotNull Function<InventoryClickEvent, ClickAction> onClick, @NotNull ClickActionType... clickActionTypes);

    @NotNull
    GuiButton setFlags(@NotNull GuiButtonFlag... flags);

    @NotNull
    GuiButton setHiddenReplacement(@Nullable ItemStack itemStack);

    //endregion

    //region Suppliers

    @NotNull
    GuiButton setIconSupplier(@NotNull Supplier<GuiIcon> iconSupplier);

    @NotNull
    GuiButton setAmountSupplier(@NotNull Supplier<Integer> amountSupplier);

    @NotNull
    GuiButton setNameSupplier(@NotNull Supplier<GuiString> nameSupplier);

    @NotNull
    GuiButton setLoreSupplier(@NotNull Supplier<List<GuiString>> loreSupplier);

    @NotNull
    GuiButton setGlowingSupplier(@NotNull Supplier<Boolean> glowingSupplier);

    @NotNull
    GuiButton setClickSoundSupplier(@NotNull Supplier<Sound> clickSoundSupplier);

    @NotNull
    GuiButton setClickSoundSupplier(@NotNull Supplier<Sound> clickSoundSupplier, float volume, float pitch);

    @NotNull
    GuiButton setVisibilitySupplier(@NotNull Supplier<Boolean> visibilitySupplier);

    //endregion

}
