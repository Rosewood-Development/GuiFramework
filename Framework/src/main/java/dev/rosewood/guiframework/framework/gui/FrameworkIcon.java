package dev.rosewood.guiframework.framework.gui;

import dev.rosewood.guiframework.gui.GuiIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public GuiIcon addAnimationFrame(Material material) {
        this.materials.add(material);
        this.itemMetas.add(Bukkit.getItemFactory().getItemMeta(material));

        return this;
    }

    @Override
    public GuiIcon addAnimationFrame(Material material, Consumer<ItemMeta> itemMetaApplier) {
        this.materials.add(material);

        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        itemMetaApplier.accept(itemMeta);
        this.itemMetas.add(itemMeta);

        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.materials.isEmpty();
    }

    @NotNull
    public Material getMaterial() {
        if (this.materials.isEmpty())
            return Material.BARRIER;
        return this.materials.get(this.currentIndex);
    }

    @NotNull
    public ItemMeta getItemMeta() {
        if (this.itemMetas.isEmpty())
            return Objects.requireNonNull(Bukkit.getItemFactory().getItemMeta(Material.BARRIER));
        return this.itemMetas.get(this.currentIndex);
    }

    @Override
    public void tick() {
        if (!this.materials.isEmpty())
            this.currentIndex = (this.currentIndex + 1) % this.materials.size();
    }

}
