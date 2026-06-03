package it.kynos.kynoslib.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class StopPluginTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (!sender.hasPermission("kynoslib.admin.stopplugin")) {
            return List.of();
        }

        if (args.length == 1) {
            final String input = args[0].toLowerCase();
            final Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
            final List<String> names = new ArrayList<>(plugins.length);

            for (final Plugin p : plugins) {
                if (p.isEnabled()
                        && !p.getName().equalsIgnoreCase("KynosLib")
                        && p.getName().toLowerCase().startsWith(input)) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return List.of();
    }
}