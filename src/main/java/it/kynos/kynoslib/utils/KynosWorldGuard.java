package it.kynos.kynoslib.utils;

import it.kynos.kynoslib.managers.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.List;

public final class KynosWorldGuard {

    private static boolean available = false;
    private static WorldGuardHook hookInstance = null;

    private KynosWorldGuard() {}

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                hookInstance = new WorldGuardHook();
                available = true;
                ColorUtils.log("§7[KynosLib] §aSuccessfully hooked into WorldGuard API!");
            } catch (Throwable t) {
                available = false;
                ColorUtils.log("§7[KynosLib] §cFailed to load WorldGuard hook class.");
            }
        } else {
            available = false;
            ColorUtils.log("§7[KynosLib] §eWorldGuard not found. Features safely bypassed.");
        }
    }

    public static boolean isAvailable() {
        return available;
    }

    public static boolean isPlayerInRegion(final Player player, final String regionName) {
        if (!available || hookInstance == null) return false;
        return hookInstance.isPlayerInRegion(player, regionName);
    }

    public static List<String> getRegionsAt(final Location location) {
        if (!available || hookInstance == null) return List.of();
        return hookInstance.getRegionsAt(location);
    }

    public static boolean hasRegionsAt(final Location location) {
        if (!available || hookInstance == null) return false;
        return hookInstance.hasRegionsAt(location);
    }
}