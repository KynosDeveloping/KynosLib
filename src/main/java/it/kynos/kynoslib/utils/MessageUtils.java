package it.kynos.kynoslib.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to read, translate, and dispatch config keys mapped with runtime placeholder contexts.
 */
public class MessageUtils {

    private final JavaPlugin plugin;
    private final boolean papiEnabled;

    public MessageUtils(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.papiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public final String get(final String path, final String... placeholders) {
        return get(null, path, placeholders);
    }

    public final String get(final Player player, final String path, final String... placeholders) {
        final FileConfiguration cfg = this.plugin.getConfig();
        final String raw = cfg.getString(path);
        if (raw == null) {
            this.plugin.getLogger().warning("[MessageUtils] Missing config key definition: " + path);
            return "§c[missing: " + path + "]";
        }
        return applyPlaceholders(raw, player, buildMap(placeholders));
    }

    public final List<String> getList(final String path, final String... placeholders) {
        return getList(null, path, placeholders);
    }

    public final List<String> getList(final Player player, final String path, final String... placeholders) {
        final List<String> lines = this.plugin.getConfig().getStringList(path);
        if (lines.isEmpty()) return List.of();

        final Map<String, String> vars = buildMap(placeholders);
        final List<String> compiled = new ArrayList<>(lines.size());
        for (final String line : lines) {
            compiled.add(applyPlaceholders(line, player, vars));
        }
        return List.copyOf(compiled);
    }

    public final void send(final CommandSender sender, final String path, final String... placeholders) {
        final Player player = (sender instanceof Player p) ? p : null;
        sender.sendMessage(get(player, path, placeholders));
    }

    public final void sendList(final CommandSender sender, final String path, final String... placeholders) {
        final Player player = (sender instanceof Player p) ? p : null;
        final List<String> lines = getList(player, path, placeholders);
        for (int i = 0; i < lines.size(); i++) {
            sender.sendMessage(lines.get(i));
        }
    }

    public final void sendRaw(final CommandSender sender, final String raw, final String... placeholders) {
        final Player player = (sender instanceof Player p) ? p : null;
        sender.sendMessage(applyPlaceholders(raw, player, buildMap(placeholders)));
    }
    public final void sendNoPermission(final CommandSender sender) {
        // Assuming 'plugin' points to your main KynosLib instance or its file module wrapper
        String msg = plugin.getConfig().getString("Messages.NoPermission", "&cYou do not have permission to execute this command!");
        sender.sendMessage(ColorUtils.translateToString(msg));
    }

    public final void sendPlayerOnly(final CommandSender sender) {
        String msg = plugin.getConfig().getString("Messages.PlayerOnly", "&cThis command can only be executed by a player.");
        sender.sendMessage(ColorUtils.translateToString(msg));
    }

    public final void sendUnknownCommand(final CommandSender sender, final String cmdName) {
        String msg = plugin.getConfig().getString("Messages.UnknownCommand", "&cUnknown command. Use &e/{cmd} help &cfor a list.");
        // Process the dynamic syntax parameter placeholder before translating colors
        String formatted = msg.replace("{cmd}", cmdName);
        sender.sendMessage(ColorUtils.translateToString(formatted));
    }

    private String applyPlaceholders(String text, final Player player, final Map<String, String> vars) {
        if (text == null || text.isEmpty()) return "";

        // 1. Array Iteration replacement over native runtime variables map
        if (!vars.isEmpty()) {
            for (final Map.Entry<String, String> e : vars.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    text = text.replace(e.getKey(), e.getValue());
                }
            }
        }

        // 2. Built-in global context placeholders parsing
        if (player != null) {
            text = text.replace("{player}", player.getName());
        }
        text = text.replace("{version}", this.plugin.getDescription().getVersion())
                .replace("{plugin_name}", this.plugin.getName());

        // 3. Third-party PlaceholderAPI integration routing
        if (this.papiEnabled) {
            text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }

        // 4. Color translation layer transformation execution
        return ColorUtils.translateToString(text, player);
    }

    private Map<String, String> buildMap(final String... pairs) {
        if (pairs == null || pairs.length == 0) return Map.of();
        final Map<String, String> map = new HashMap<>((pairs.length / 2) + 1);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }
}