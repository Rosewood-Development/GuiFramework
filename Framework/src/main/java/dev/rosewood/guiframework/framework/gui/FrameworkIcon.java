package dev.rosewood.guiframework.framework.gui;

import dev.rosewood.guiframework.gui.GuiIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class FrameworkIcon implements GuiIcon {

    private List<Material> materials;
    private List<ItemMeta> itemMetas;
    private int currentIndex;

    public FrameworkIcon() {
        this.materials = new ArrayList<>();
        this.itemMetas = new ArrayList<>();
        this.currentIndex = 0;
    }

    public FrameworkIcon(Material material) {
        this();
        this.materials.add(material);
        this.itemMetas.add(Bukkit.getItemFactory().getItemMeta(material));
    }

    public FrameworkIcon(Material material, Consumer<ItemMeta> itemMetaApplier) {
        this();
        this.materials.add(material);

        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        itemMetaApplier.accept(itemMeta);
        this.itemMetas.add(itemMeta);
    }

    @Override
    public void addAnimationFrame(Material material) {
        this.materials.add(material);
        this.itemMetas.add(Bukkit.getItemFactory().getItemMeta(material));
    }

    @Override
    public void addAnimationFrame(Material material, Consumer<ItemMeta> itemMetaApplier) {
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
