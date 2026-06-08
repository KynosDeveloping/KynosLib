package it.kynos.kynoslib.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for text color translation.
 * Handles legacy ampersand codes (&amp;0-9, &amp;a-f), standard Minecraft section symbols (§0-9, §a-f),
 * and modern HEX formatting (&amp;#RRGGBB).
 * <p>
 * This utility normalizes input strings to ensure cross-compatibility between standard
 * configuration layouts ('&amp;') and runtime Spigot/Bukkit raw data ('§').
 * </p>
 */
public final class ColorUtils {

    /**
     * Serializer configured to parse ampersand ('&amp;') formats.
     * Includes native support for HEX colors (&amp;#RRGGBB) and cross-version compatibility flags.
     */
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat() // Ensures maximum downstream compatibility with older Spigot pipelines
            .build();

    /**
     * Serializer configured to parse standard Minecraft section ('§') formats.
     * Primarily used to fallback down into raw legacy Strings for older Bukkit API implementations.
     */
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

    // Prevent instantiation of utility class
    private ColorUtils() {}

    /**
     * Normalizes the input text by transforming legacy section symbols (§) into clean ampersands (&amp;).
     * This acts as a safe pre-processor so the main Adventure pipeline can interpret both formats simultaneously.
     *
     * @param input The raw message string.
     * @return A sanitized string ready for ampersand-based deserialization.
     */
    private static String preProcess(final String input) {
        if (input == null) return null;
        // Replaces all section characters with ampersands, ensuring '§' inputs are readable by LEGACY_AMPERSAND
        return input.replace('§', '&');
    }

    /**
     * Translates a legacy/HEX/section formatted string into an Adventure Text Component.
     *
     * @param message The raw input string containing color codes (supports both '&amp;' and '§').
     * @return A rich text {@link Component} ready for native Adventure rendering.
     */
    public static Component translate(final String message) {
        if (message == null) return Component.empty();
        // Pre-process ensures that any existing '§' character is converted to '&' before feeding it to Adventure
        return LEGACY_AMPERSAND.deserialize(preProcess(message));
    }

    /**
     * Translates a string into an Adventure Text Component (Player-specific overload).
     *
     * @param message The raw input string.
     * @param player  The target player context (reserved for future per-player locale/placeholder parsing).
     * @return A rich text {@link Component}.
     */
    public static Component translate(final String message, final Player player) {
        return translate(message);
    }

    /**
     * Translates a legacy/HEX/section formatted string into a standard legacy Bukkit string ('§').
     * Useful when interacting with vanilla inventory titles, scoreboards, or legacy plugins.
     *
     * @param message The raw input string with ampersand or section symbols.
     * @return A native legacy string formatted with section ('§') markers.
     */
    public static String translateToString(final String message) {
        if (message == null) return "";
        // Pre-processes, builds the abstract Component tree, then converts it entirely back to raw '§' formatting
        return LEGACY_SECTION.serialize(LEGACY_AMPERSAND.deserialize(preProcess(message)));
    }

    /**
     * Translates a string into a standard legacy Bukkit string (Player-specific overload).
     *
     * @param message The raw input string.
     * @param player  The target player context (reserved for future placeholder evaluation).
     * @return A native legacy string formatted with section ('§') markers.
     */
    public static String translateToString(final String message, final Player player) {
        return translateToString(message);
    }

    /**
     * Bulk maps a list of raw strings into immutable Adventure Components.
     *
     * @param messages A list of strings containing color codes.
     * @return An unmodifiable list of translated {@link Component}s.
     */
    public static List<Component> translateList(final List<String> messages) {
        if (messages == null) return List.of();
        final List<Component> translated = new ArrayList<>(messages.size());
        for (final String s : messages) {
            translated.add(translate(s));
        }
        return List.copyOf(translated);
    }

    /**
     * Bulk maps a list of raw strings into a list of legacy section ('§') formatted strings.
     *
     * @param messages A list of strings containing ampersand or section color codes.
     * @return An unmodifiable list of standard legacy formatted strings.
     */
    public static List<String> translateListToString(final List<String> messages) {
        if (messages == null) return List.of();
        final List<String> translated = new ArrayList<>(messages.size());
        for (final String s : messages) {
            translated.add(translateToString(s));
        }
        return List.copyOf(translated);
    }

    /**
     * Formats and prints a message directly to the console sender terminal with proper colors.
     *
     * @param message The raw message to log.
     */
    public static void log(final String message) {
        Bukkit.getConsoleSender().sendMessage(translate(message));
    }

    /**
     * Formats and prints a system-prefixed message directly to the console terminal.
     *
     * @param prefix  The module or plugin prefix identifier.
     * @param message The log payload message.
     */
    public static void log(final String prefix, final String message) {
        log("&7[" + prefix + "] " + message);
    }
}