package it.kynos.kynoslib.commands;

import it.kynos.kynoslib.KynosLib;
import it.kynos.kynoslib.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class StopPluginCommand extends KynosCommand {

    public StopPluginCommand(KynosLib plugin) {
        super(plugin, "stopplugin");
        permission("kynoslib.admin.stopplugin");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginDisabler.Usage")));
            return true;
        }

        final String targetName = args[0];

        if (targetName.equalsIgnoreCase("KynosLib")) {
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginDisabler.Self")));
            return true;
        }

        final Plugin target = Bukkit.getPluginManager().getPlugin(targetName);
        if (target == null || !target.isEnabled()) {
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginDisabler.NotFound", "{plugin}", targetName)));
            return true;
        }

        try {
            Bukkit.getPluginManager().disablePlugin(target);
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginDisabler.Success", "{plugin}", targetName)));
        } catch (Throwable t) {
            sender.sendMessage(ColorUtils.translate(this.msg.get("Messages.PluginDisabler.Error", "{plugin}", targetName)));
            this.plugin.getLogger().severe("Critical exception encountered disabling plugin '" + targetName + "': " + t.getMessage());
        }

        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
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