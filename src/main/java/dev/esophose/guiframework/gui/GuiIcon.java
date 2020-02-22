package dev.esophose.guiframework.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class GuiIcon implements Tickable {

    private List<Material> materials;
    private int currentIndex;

    public GuiIcon() {
        this.materials = new ArrayList<>();
        this.currentIndex = 0;
    }

    public GuiIcon(Material material) {
        this();
        this.materials.add(material);
    }

    /**
     * Adds an animation frame to the GuiIcon
     *
     * @param material The Material for the frame
     */
    public void addAnimationFrame(@NotNull Material material) {
        this.materials.add(material);
    }

    @Override
    public void tick() {
        if (!this.materials.isEmpty())
            this.currentIndex = (this.currentIndex + 1) % this.materials.size();
    }

    /**
     * Gets the Material this icon should be displayed with
     *
     * @return The current frame's Material
     */
    @NotNull
    public Material getMaterial() {
        if (this.materials.isEmpty())
            return Material.BARRIER;

        return this.materials.get(this.currentIndex);
    }

}
