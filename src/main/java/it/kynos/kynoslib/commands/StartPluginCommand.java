package it.kynos.kynoslib.commands;

import it.kynos.kynoslib.KynosLib;
import it.kynos.kynoslib.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class StartPluginCommand extends KynosCommand {

    public StartPluginCommand(KynosLib plugin) {
        super(plugin, "startplugin");
        permission("kynoslib.admin.startplugin");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginEnabler.Usage")));
            return true;
        }

        final String targetName = args[0];
        final Plugin target = Bukkit.getPluginManager().getPlugin(targetName);

        if (target == null || target.isEnabled()) {
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginEnabler.NotFound", "{plugin}", targetName)));
            return true;
        }

        try {
            Bukkit.getPluginManager().enablePlugin(target);
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginEnabler.Success", "{plugin}", targetName)));
        } catch (Throwable t) {
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginEnabler.Error", "{plugin}", targetName)));
            this.plugin.getLogger().severe("Critical exception encountered enabling plugin '" + targetName + "': " + t.getMessage());
        }

        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            final String input = args[0].toLowerCase();
            final Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
            final List<String> names = new ArrayList<>(plugins.length); // Allocated based on total plugin count max boundary

            for (final Plugin p : plugins) {
                if (!p.isEnabled() && p.getName().toLowerCase().startsWith(input)) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return List.of();
    }
}