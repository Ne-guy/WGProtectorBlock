package me.Negai.WGProtectorBlock;

import me.Negai.WGProtectorBlock.API.AchievementCommand;
import me.Negai.WGProtectorBlock.util.util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Configurated {
    public static @NotNull List<util.ProtectorFlag> getProtectorFlags() {
        List<util.ProtectorFlag> flags = new ArrayList<>();
        for (String flagName : Main.getInstance().getConfig().getStringList("flags")) {
            if (util.getFlagByName(flagName) == null) continue;
            flags.add(new util.ProtectorFlag(util.getFlagByName(flagName)));
        }
        return flags;
    }

    public static List<World> getDisabledWorlds() {
        List<World> list = new ArrayList<>();
        List<String> disabledWorldStrings = Main.getInstance().getConfig().getStringList("disabled-worlds");

        for (String worldName : disabledWorldStrings) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) list.add(world);
        }

        return list;
    }

    public static String getDisabledWorldsMessage() {
        return Main.getInstance().getConfig().getString("disabled-world-message");
    }

    public static Set<String> getAchievementCommandsStrings() {
        return Main.getInstance().getConfig().getConfigurationSection("achieving-commands").getKeys(false);
    }

    public static List<AchievementCommand> getAchievementCommands() {
        List<AchievementCommand> list = new ArrayList<>();

        for (String commandName : getAchievementCommandsStrings()) {
            list.add(new AchievementCommand(commandName));
        }

        return list;
    }

    public static boolean doesAchievingCommandExist(String name) {
        return getAchievementCommandsStrings().contains(name);
    }

    public static String getAchievingCommandsNamespace() {
        return Main.getInstance().getConfig().getString("achieving-commands-namespace");
    }
}
