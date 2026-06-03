package it.kynos.kynoslib.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Smart and ultra-optimized layout mapping engine managing backward cross-version sound data hooks.
 */
public final class SoundManager {

    private static final Map<String, Sound> SOUND_CACHE = new HashMap<>(Sound.values().length);
    private static final Map<String, String> FALLBACK_MAP = Map.ofEntries(
            Map.entry("UI_BUTTON_CLICK", "CLICK"),
            Map.entry("BLOCK_NOTE_BLOCK_PLING", "NOTE_PLING"),
            Map.entry("BLOCK_NOTE_BLOCK_CHIME", "NOTE_CHIME"),
            Map.entry("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"),
            Map.entry("ENTITY_EXPERIENCE_ORB_PICKUP", "ORB_PICKUP"),
            Map.entry("ENTITY_VILLAGER_NO", "VILLAGER_NO"),
            Map.entry("ENTITY_VILLAGER_YES", "VILLAGER_YES"),
            Map.entry("ENTITY_GENERIC_EXPLODE", "EXPLODE"),
            Map.entry("ENTITY_PLAYER_ATTACK_CRIT", "SUCCESSFUL_HIT"),
            Map.entry("ENTITY_ZOMBIE_AMBIENT", "ZOMBIE_IDLE"),
            Map.entry("ENTITY_CREEPER_PRIMED", "CREEPER_HISS")
    );

    static {
        for (final Sound sound : Sound.values()) {
            SOUND_CACHE.put(sound.name(), sound);
        }
    }

    private SoundManager() {}

    public static void playFromConfig(final Player player, final FileConfiguration config, final String path) {
        if (player == null || config == null || path == null) return;

        final String soundName = config.getString(path + ".sound");
        if (soundName == null || soundName.isEmpty()) return;

        final float volume = (float) config.getDouble(path + ".volume", 1.0);
        final float pitch = (float) config.getDouble(path + ".pitch", 1.0);

        play(player, soundName, volume, pitch);
    }

    public static void playGlobalFromConfig(final Location location, final FileConfiguration config, final String path) {
        if (location == null || config == null || path == null) return;

        final String soundName = config.getString(path + ".sound");
        if (soundName == null || soundName.isEmpty()) return;

        final float volume = (float) config.getDouble(path + ".volume", 1.0);
        final float pitch = (float) config.getDouble(path + ".pitch", 1.0);

        playGlobal(location, soundName, volume, pitch);
    }

    public static void play(final Player player, final String soundName, final float volume, final float pitch) {
        if (player == null || soundName == null) return;
        final Sound sound = getSoundCached(soundName);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static void playGlobal(final Location location, final String soundName, final float volume, final float pitch) {
        if (location == null || location.getWorld() == null || soundName == null) return;
        final Sound sound = getSoundCached(soundName);
        if (sound != null) {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
    }

    private static Sound getSoundCached(final String soundName) {
        final String upperName = soundName.toUpperCase().trim();

        final Sound cached = SOUND_CACHE.get(upperName);
        if (cached != null) return cached;
        if (SOUND_CACHE.containsKey(upperName)) return null; // Avoid re-evaluating known invalid definitions

        final String fallbackName = FALLBACK_MAP.get(upperName);
        if (fallbackName != null) {
            final Sound fallbackSound = SOUND_CACHE.get(fallbackName);
            if (fallbackSound != null) {
                Bukkit.getLogger().log(Level.INFO, "[KynosLib Sound] ''{0}'' adapted using legacy fallback: ''{1}''", new Object[]{upperName, fallbackName});
                SOUND_CACHE.put(upperName, fallbackSound);
                return fallbackSound;
            }
        }

        try {
            final Sound dynamicSound = Sound.valueOf(upperName);
            SOUND_CACHE.put(upperName, dynamicSound);
            return dynamicSound;
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.WARNING, "[KynosLib Sound] ''{0}'' is unsupported by current core engine versions.", upperName);
            SOUND_CACHE.put(upperName, null);
            return null;
        }
    }
}