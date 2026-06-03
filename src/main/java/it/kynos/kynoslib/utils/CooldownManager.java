package it.kynos.kynoslib.utils;

import it.kynos.kynoslib.KynosLib;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class CooldownManager {

    private static final Map<String, Map<String, Long>> COOLDOWNS = new HashMap<>();

    private CooldownManager() {}

    public static boolean checkAndSet(final Player player, final String cooldownKey, final long timeInMs) {
        if (player == null) return false;

        final String playerName = player.getName();
        final long currentTime = System.currentTimeMillis();
        final Map<String, Long> playerCooldowns = COOLDOWNS.get(playerName);

        if (playerCooldowns != null) {
            final Long expireTime = playerCooldowns.get(cooldownKey);

            if (expireTime != null && currentTime < expireTime) {
                final double timeLeft = (expireTime - currentTime) / 1000.0;
                // Translated in-game message strings to English
                final String msg = KynosLib.getInstance().getConfig()
                        .getString("Messages.CooldownActive", "&cPlease wait %.1fs before doing this again!");

                player.sendMessage(ColorUtils.translateToString(String.format(msg, timeLeft)));
                return false;
            }
        }

        COOLDOWNS.computeIfAbsent(playerName, k -> new HashMap<>(4)).put(cooldownKey, currentTime + timeInMs);
        return true;
    }

    public static boolean hasCooldown(final String playerName, final String cooldownKey) {
        final Map<String, Long> playerCooldowns = COOLDOWNS.get(playerName);
        if (playerCooldowns == null) return false;

        final Long expireTime = playerCooldowns.get(cooldownKey);
        if (expireTime == null) return false;

        if (System.currentTimeMillis() >= expireTime) {
            playerCooldowns.remove(cooldownKey);
            return false;
        }

        return true;
    }

    public static void remove(final String playerName, final String cooldownKey) {
        final Map<String, Long> playerCooldowns = COOLDOWNS.get(playerName);
        if (playerCooldowns != null) {
            playerCooldowns.remove(cooldownKey);
        }
    }
}