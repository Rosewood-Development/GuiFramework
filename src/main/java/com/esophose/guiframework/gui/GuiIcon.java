package com.esophose.guiframework.gui;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiIcon implements ITickable {

    private List<Material> materials;
    private List<Byte> dataValues;
    private int currentIndex;

    public GuiIcon() {
        this.materials = new ArrayList<>();
        this.dataValues = new ArrayList<>();
        this.currentIndex = 0;
    }

    public GuiIcon(Material material, byte data) {
        this();
        this.materials.add(material);
        this.dataValues.add(data);
    }

    public GuiIcon(Material material) {
        this(material, (byte) 0);
    }

    /**
     * Adds an animation frame to the GuiIcon
     *
     * @param material The Material for the frame
     * @param data The data for the frame
     */
    public void addAnimationFrame(@NotNull Material material, byte data) {
        this.materials.add(material);
        this.dataValues.add(data);
    }

    /**
     * Adds an animation frame to the GuiIcon
     * Uses a value of 0 for data
     *
     * @param material The Material for the frame
     */
    public void addAnimationFrame(@NotNull Material material) {
        this.addAnimationFrame(material, (byte) 0);
    }

    @Override
    public void tick() {
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

    /**
     * Gets the data this icon should be displayed with
     *
     * @return The current frame's data
     */
    public byte getData() {
        if (this.dataValues.isEmpty())
            return 0;

        return this.dataValues.get(this.currentIndex);
    }

}
