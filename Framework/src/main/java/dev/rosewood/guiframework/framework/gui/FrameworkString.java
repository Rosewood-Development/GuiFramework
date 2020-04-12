package dev.rosewood.guiframework.framework.gui;

import dev.rosewood.guiframework.gui.GuiString;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class FrameworkString implements GuiString {

    private List<String> strings;
    private int currentIndex;

    public FrameworkString() {
        this.strings = new ArrayList<>();
        this.currentIndex = 0;
    }

    public FrameworkString(String string) {
        this();
        this.strings.add(string);
    }

    /**
     * Adds a String to the animation frames
     *
     * @param text The text to add
     */
    @Override
    public void addAnimationFrame(String text) {
        this.strings.add(text);
    }

    @Override
    public void tick() {
        if (!this.strings.isEmpty())
            this.currentIndex = (this.currentIndex + 1) % this.strings.size();
    }

    /**
     * Gets the current frame of the GuiString
     *
     * @return The current frame of the GuiString
     */
    @Override
    @NotNull
    public String toString() {
        if (this.strings.isEmpty())
            return "";

        return ChatColor.translateAlternateColorCodes('&', this.strings.get(this.currentIndex));
    }

}
