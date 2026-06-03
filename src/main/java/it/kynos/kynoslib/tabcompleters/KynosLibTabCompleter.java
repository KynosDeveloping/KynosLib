package it.kynos.kynoslib.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class KynosLibTabCompleter implements TabCompleter {

    private static final List<String> SUB_COMMANDS = List.of("gui", "reload", "help");

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            final String input = args[0].toLowerCase();
            final List<String> completions = new ArrayList<>(SUB_COMMANDS.size());

            for (final String sub : SUB_COMMANDS) {
                if (sub.startsWith(input)) {
                    if (sub.equals("gui") && !sender.hasPermission("kynoslib.admin.gui")) continue;
                    if (sub.equals("reload") && !sender.hasPermission("kynoslib.admin.reload")) continue;
                    completions.add(sub);
                }
            }
            return completions;
        }
        return List.of();
    }
}