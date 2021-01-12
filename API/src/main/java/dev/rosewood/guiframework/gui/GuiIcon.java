package dev.rosewood.guiframework.gui;

import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public interface GuiIcon extends Tickable {

    /**
     * Adds an animation frame to the GuiIcon using default ItemMeta
     *
     * @param material The Material for the frame
     */
    GuiIcon addAnimationFrame(@NotNull Material material);

    /**
     * Adds an animation frame to the GuiIcon and allows you to edit the ItemMeta
     *
     * @param material The material for the frame
     * @param itemMetaApplier The Consumer to edit the ItemMeta for the frame
     */
    GuiIcon addAnimationFrame(@NotNull Material material, @NotNull Consumer<ItemMeta> itemMetaApplier);

}
