package dev.rosewood.guiframework.examples.command;

import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.examples.GuiFrameworkExamples;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    protected final GuiFrameworkExamples plugin;
    protected final GuiFramework guiFramework;
    private final String name;

    public SubCommand(GuiFrameworkExamples plugin, GuiFramework guiFramework, String name) {
        this.plugin = plugin;
        this.guiFramework = guiFramework;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract void execute(Player player);

}
