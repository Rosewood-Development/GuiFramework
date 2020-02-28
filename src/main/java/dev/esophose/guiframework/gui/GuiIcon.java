package dev.esophose.guiframework.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class GuiIcon implements Tickable {

    private List<Material> materials;
    private List<ItemMeta> itemMetas;
    private int currentIndex;

    public GuiIcon() {
        this.materials = new ArrayList<>();
        this.itemMetas = new ArrayList<>();
        this.currentIndex = 0;
    }

    public GuiIcon(Material material) {
        this();
        this.materials.add(material);
        this.itemMetas.add(Bukkit.getItemFactory().getItemMeta(material));
    }

    public GuiIcon(Material material, Consumer<ItemMeta> itemMetaApplier) {
        this();
        this.materials.add(material);

        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        itemMetaApplier.accept(itemMeta);
        this.itemMetas.add(itemMeta);
    }

    /**
     * Adds an animation frame to the GuiIcon using default ItemMeta
     *
     * @param material The Material for the frame
     */
    public void addAnimationFrame(@NotNull Material material) {
        this.materials.add(material);
        this.itemMetas.add(Bukkit.getItemFactory().getItemMeta(material));
    }

    /**
     * Adds an animation frame to the GuiIcon and allows you to edit the ItemMeta
     *
     * @param material The material for the frame
     * @param itemMetaApplier The Consumer to edit the ItemMeta for the frame
     */
    public void addAnimationFrame(@NotNull Material material, @NotNull Consumer<ItemMeta> itemMetaApplier) {
        this.materials.add(material);

        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        itemMetaApplier.accept(itemMeta);
        this.itemMetas.add(itemMeta);
    }

    @NotNull
    public Material getMaterial() {
        return this.materials.get(this.currentIndex);
    }

    @NotNull
    public ItemMeta getItemMeta() {
        return this.itemMetas.get(this.currentIndex);
    }

    @Override
    public void tick() {
        if (!this.materials.isEmpty())
            this.currentIndex = (this.currentIndex + 1) % this.materials.size();
    }

}
