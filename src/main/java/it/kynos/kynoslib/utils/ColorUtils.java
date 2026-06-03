package it.kynos.kynoslib.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Color translation utility for legacy ampersand codes and modern HEX formatting (&#RRGGBB).
 */
public final class ColorUtils {

    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ColorUtils() {} // Prevent instantiation

    public static Component translate(final String message) {
        if (message == null) return Component.empty();
        final String colored = convertHexColors(message);
        return LEGACY_SECTION.deserialize(LEGACY_AMPERSAND.serialize(LEGACY_AMPERSAND.deserialize(colored)));
    }

    public static Component translate(final String message, final Player player) {
        return translate(message);
    }

    public static String translateToString(final String message) {
        if (message == null) return "";
        final String colored = convertHexColors(message);
        return LEGACY_SECTION.serialize(LEGACY_AMPERSAND.deserialize(colored));
    }

    public static String translateToString(final String message, final Player player) {
        return translateToString(message);
    }

    public static List<Component> translateList(final List<String> messages) {
        if (messages == null) return List.of();
        final List<Component> translated = new ArrayList<>(messages.size());
        for (final String s : messages) {
            translated.add(translate(s));
        }
        return List.copyOf(translated);
    }

    public static List<String> translateListToString(final List<String> messages) {
        if (messages == null) return List.of();
        final List<String> translated = new ArrayList<>(messages.size());
        for (final String s : messages) {
            translated.add(translateToString(s));
        }
        return List.copyOf(translated);
    }

    public static void log(final String message) {
        Bukkit.getConsoleSender().sendMessage(translate(message));
    }

    public static void log(final String prefix, final String message) {
        log("&7[" + prefix + "] " + message);
    }

    /**
     * Translates standard &#RRGGBB tokens into native Bukkit color paths.
     */
    private static String convertHexColors(final String message) {
        if (message == null || !message.contains("#")) return message;

        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuilder sb = new StringBuilder(message.length() + 16);

        while (matcher.find()) {
            final String hex = matcher.group(1);
            final StringBuilder replacement = new StringBuilder("§x");
            for (int i = 0; i < 6; i++) {
                replacement.append('§').append(hex.charAt(i));
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}