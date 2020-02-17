package dev.esophose.guiframework.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class GuiString implements ITickable {

    private List<String> strings;
    private int currentIndex;

    public GuiString() {
        this.strings = new ArrayList<>();
        this.currentIndex = 0;
    }

    public GuiString(String string) {
        this();
        this.strings.add(string);
    }

    /**
     * Adds a String to the animation frames
     *
     * @param string The String to add
     */
    public void addAnimationFrame(@NotNull String string) {
        this.strings.add(string);
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
