package dev.rosewood.guiframework.examples.command;

import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.examples.GuiFrameworkExamples;
import java.util.Iterator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {

    public HelpCommand(GuiFrameworkExamples plugin, GuiFramework guiFramework) {
        super(plugin, guiFramework, "help");
    }

    @Override
    public void execute(Player player) {
        ComponentBuilder builder = new ComponentBuilder()
                .append("=== Available Examples ===")
                .color(ChatColor.YELLOW)
                .append(" (Click to view)\n")
                .color(ChatColor.GRAY);

        Iterator<SubCommand> subCommandIterator = this.plugin.getSubCommands().iterator();
        while (subCommandIterator.hasNext()) {
            SubCommand command = subCommandIterator.next();
            if (command == this)
                continue;

            BaseComponent[] hoverText = new ComponentBuilder()
                    .append("Click to view ")
                    .color(ChatColor.YELLOW)
                    .append(command.getName())
                    .color(ChatColor.AQUA)
                    .italic(true)
                    .create();

            TextComponent example = new TextComponent(command.getName());
            example.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guiframework " + command.getName()));
            example.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));

            builder.append(example).color(ChatColor.AQUA);

            if (subCommandIterator.hasNext())
                builder.append(", ").color(ChatColor.GRAY);
        }

        player.spigot().sendMessage(builder.create());
    }

}
