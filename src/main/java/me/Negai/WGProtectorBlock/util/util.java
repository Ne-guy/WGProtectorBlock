package me.Negai.WGProtectorBlock.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class util {

    public static String formatTime(long totalSeconds) {
        // Define time unit constants
        long SECONDS_IN_MINUTE = 60;
        long SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
        long SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;
        long SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;
        long SECONDS_IN_MONTH = SECONDS_IN_DAY * 30;  // Approximation (30 days in a month)
        long SECONDS_IN_YEAR = SECONDS_IN_DAY * 365;  // Approximation (365 days in a year)

        // Calculate each time unit
        long years = totalSeconds / SECONDS_IN_YEAR;
        totalSeconds %= SECONDS_IN_YEAR;

        long months = totalSeconds / SECONDS_IN_MONTH;
        totalSeconds %= SECONDS_IN_MONTH;

        long weeks = totalSeconds / SECONDS_IN_WEEK;
        totalSeconds %= SECONDS_IN_WEEK;

        long days = totalSeconds / SECONDS_IN_DAY;
        totalSeconds %= SECONDS_IN_DAY;

        long hours = totalSeconds / SECONDS_IN_HOUR;
        totalSeconds %= SECONDS_IN_HOUR;

        long minutes = totalSeconds / SECONDS_IN_MINUTE;
        totalSeconds %= SECONDS_IN_MINUTE;

        long seconds = totalSeconds;

        // Build the result string, adding only non-zero time units
        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append(" year").append(years == 1 ? "" : "s").append(", ");
        }
        if (months > 0) {
            result.append(months).append(" month").append(months == 1 ? "" : "s").append(", ");
        }
        if (weeks > 0) {
            result.append(weeks).append(" week").append(weeks == 1 ? "" : "s").append(", ");
        }
        if (days > 0) {
            result.append(days).append(" day").append(days == 1 ? "" : "s").append(", ");
        }
        if (hours > 0) {
            result.append(hours).append(" hour").append(hours == 1 ? "" : "s").append(", ");
        }
        if (minutes > 0) {
            result.append(minutes).append(" minute").append(minutes == 1 ? "" : "s").append(", ");
        }
        if (seconds > 0 || result.length() == 0) {  // Include seconds even if 0, when the result is empty
            result.append(seconds).append(" second").append(seconds == 1 ? "" : "s");
        }

        // Remove trailing comma and space if present
        if (result.length() > 2 && result.substring(result.length() - 2).equals(", ")) {
            result.setLength(result.length() - 2);
        }

        return result.toString();
    }

    public static boolean boxesOverlap(BlockVector3 min1, BlockVector3 max1, BlockVector3 min2, BlockVector3 max2) {
        // Check if the bounding boxes overlap on all three axes (X, Y, Z)
        return (min1.getX() <= max2.getX() && max1.getX() >= min2.getX()) &&  // X-axis overlap
                (min1.getY() <= max2.getY() && max1.getY() >= min2.getY()) &&  // Y-axis overlap
                (min1.getZ() <= max2.getZ() && max1.getZ() >= min2.getZ());    // Z-axis overlap
    }

    public static boolean willRegionOverlapAnyExisting(World world, BlockVector3 minPoint, BlockVector3 maxPoint) {
        // Get the RegionManager for the world
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            return true; // No region manager found for this world
        }

        // Iterate through all existing regions in the world
        for (Map.Entry<String, ProtectedRegion> entry : regionManager.getRegions().entrySet()) {
            ProtectedRegion existingRegion = entry.getValue();

            // Get the bounding box (min and max points) of the existing region
            BlockVector3 existingMin = existingRegion.getMinimumPoint();
            BlockVector3 existingMax = existingRegion.getMaximumPoint();

            // Check if the newly proposed region overlaps with the current existing region
            if (boxesOverlap(minPoint, maxPoint, existingMin, existingMax)) {
                return true; // Overlap found
            }
        }

        return false; // No overlaps with any regions
    }

    public static List<OfflinePlayer> getPlayersFromUUIDCollection(Set<UUID> uuids) {
        List<OfflinePlayer> list = new ArrayList<>();

        for (UUID uuid : uuids) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player == null) continue;
            list.add(player);
        }

        return list;
    }

    public static boolean isIndexInBounds(int index, List<StateFlag> list) {
        try {
            list.get(index);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static @Nullable Flag<?> getFlagByName(String flagName) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        if (registry.get(flagName) == null) return null;
        return registry.get(flagName);
    }

    public static class ProtectorFlag {
        private final Flag<?> flag;

        public ProtectorFlag(Flag<?> flag) {
            this.flag = flag;
        }

        public String getName() {
            return flag.getName();
        }

        public @Nullable Flag<?> getState() {
            return flag;
        }

        /*public void setFlag(@Nullable StateFlag.State state) {
            try {
                // Set the flag's state in the region, using proper type
                region.setFlag((StateFlag)flag, state);
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
                regionManager.addRegion(region);
            } catch (Exception e) {

            }
        }*/
    }

}

