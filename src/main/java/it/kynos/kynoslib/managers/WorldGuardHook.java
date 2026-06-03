package it.kynos.kynoslib.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * WorldGuard API Bridge wrapper.
 * Lazily decoupled via specialized package routes to safely prevent Classpath issues.
 */
public class WorldGuardHook {

    public boolean isPlayerInRegion(Player player, String regionName) {
        final Location loc = player.getLocation();
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();
        final ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));

        for (final ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getRegionsAt(Location location) {
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();
        final ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));

        // Allocated explicitly using known collection bounds to optimize memory usage
        final List<String> regionNames = new ArrayList<>(set.size());
        for (final ProtectedRegion region : set) {
            regionNames.add(region.getId());
        }
        return regionNames;
    }

    public boolean hasRegionsAt(Location location) {
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();
        final ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));
        return set.size() > 0;
    }
}