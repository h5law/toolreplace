package com.github.h5law.toolreplace;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToolReplaceCommand implements CommandExecutor {

    private final ToolReplace plugin;

    public ToolReplaceCommand(ToolReplace plugin) {
        this.plugin = plugin;
    }
    public void usage(Player player) {
        player.sendMessage("[ToolReplace] Usage: /tr [OPTION] [ARGUMENT]");
        player.sendMessage("[ToolReplace] Options: help, debug, replace");
        player.sendMessage("[ToolReplace] Arguments: enable, disable, toggle");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                usage(player);
            } else {
                switch(args[0]) {
                    case "help":
                        usage(player);
                        return true;
                    case "debug":
                        if (args.length > 1) {
                            switch (args[1]) {
                                case "true":
                                    plugin.toggle(player, plugin.PlayerDebug, true);
                                    return true;
                                case "false":
                                    plugin.toggle(player, plugin.PlayerDebug ,false);
                                    return true;
                                default:
                                    player.sendMessage("[ToolReplace] Usage: /tr debug <true/false>");
                            }
                        } else {
                            plugin.toggle(player, plugin.PlayerDebug);
                            return true;
                        }
                    case "replace":
                        if (args.length > 1) {
                            switch (args[1]) {
                                case "true":
                                    // remove from disable set
                                    plugin.toggle(player, plugin.DisableToolReplace, false);
                                    return true;
                                case "false":
                                    // add to disable set
                                    plugin.toggle(player, plugin.DisableToolReplace, true);
                                    return true;
                                default:
                                    player.sendMessage("[ToolReplace] Usage: /tr replace <true/false>");
                            }
                        } else {
                            plugin.toggle(player, plugin.DisableToolReplace);
                        }
                    default:
                        player.sendMessage("[ToolReplace] Unknown option");
                        usage(player);
                }
            }
        }
        return false;
    }

}
