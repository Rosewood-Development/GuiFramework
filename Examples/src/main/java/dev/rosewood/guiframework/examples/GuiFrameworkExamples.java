package dev.rosewood.guiframework.examples;

import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.examples.command.AnimatedExample;
import dev.rosewood.guiframework.examples.command.EditableExample;
import dev.rosewood.guiframework.examples.command.HelpCommand;
import dev.rosewood.guiframework.examples.command.PaginatedExample;
import dev.rosewood.guiframework.examples.command.RotationExample;
import dev.rosewood.guiframework.examples.command.SubCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class GuiFrameworkExamples extends JavaPlugin {

    private static final String PERMISSION = "guiframeworkexamples.use";
    private GuiFramework guiFramework;
    private List<SubCommand> subCommands;

    @Override
    public void onEnable() {
        this.guiFramework = GuiFramework.instantiate(this);
        this.subCommands = Arrays.asList(
                new HelpCommand(this, this.guiFramework),
                new AnimatedExample(this, this.guiFramework),
                new PaginatedExample(this, this.guiFramework),
                new RotationExample(this, this.guiFramework),
                new EditableExample(this, this.guiFramework)
        );

        PluginCommand command = this.getCommand("guiframework");
        if (command != null)
            command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("guiframework"))
            return true;

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This plugin can only be used by a Player.");
            return true;
        }

        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
            return true;
        }

        if (args.length == 0) {
            this.subCommands.stream()
                    .filter(x -> x.getName().equalsIgnoreCase("help"))
                    .findFirst()
                    .ifPresent(x -> x.execute((Player) sender));
        } else {
            Optional<SubCommand> subCommand = this.subCommands.stream()
                    .filter(x -> x.getName().equalsIgnoreCase(args[0]))
                    .findFirst();

            if (subCommand.isPresent()) {
                subCommand.get().execute((Player) sender);
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown example name.");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (sender instanceof Player && sender.hasPermission(PERMISSION)) {
            List<String> possibilities = this.subCommands.stream().map(SubCommand::getName).collect(Collectors.toList());
            if (args.length == 0) {
                completions.addAll(possibilities);
            } else if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], possibilities, completions);
            }
        }

        return completions;
    }

    public List<SubCommand> getSubCommands() {
        return this.subCommands;
    }

}
