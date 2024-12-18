package me.Negai.WGProtectorBlock.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import me.Negai.WGProtectorBlock.API.AchievementCommand;
import me.Negai.WGProtectorBlock.Configurated;
import me.Negai.WGProtectorBlock.Main;
import me.Negai.WGProtectorBlock.util.util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.Tuple;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class achievingCommands {

    public static HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();

    // Reset all cooldowns for the specified player
    public static void resetCooldowns(Player player) {
        UUID playerUUID = player.getUniqueId();
        cooldowns.remove(playerUUID); // Remove all cooldowns for this player
    }

    // Check if a player is in cooldown for a specific command type
    public static boolean isInCooldown(UUID playerUUID, String commandType, int cooldown) {
        if (cooldowns.containsKey(playerUUID)) {
            HashMap<String, Long> playerCooldowns = cooldowns.get(playerUUID);
            if (playerCooldowns.containsKey(commandType)) {
                long lastUsedTime = playerCooldowns.get(commandType);
                return (System.currentTimeMillis() - lastUsedTime) < (cooldown * 1000L);
            }
        }
        return false;
    }

    // Calculate how much time is left on the cooldown for a specific command type
    public static int getTimeLeft(UUID playerUUID, String commandType, int cooldown) {
        long lastUsedTime = cooldowns.get(playerUUID).get(commandType);
        return (int) ((cooldown * 1000L - (System.currentTimeMillis() - lastUsedTime)) / 1000);
    }

    // Execute the command based on the command type
    public static void executeCommand(Player player, String commandType) {
        player.sendMessage("You used the command (" + commandType + ") successfully!");
    }

    // Set the player's cooldown for a specific command type
    public static void setCooldown(UUID playerUUID, String commandType) {
        cooldowns.putIfAbsent(playerUUID, new HashMap<>()); // Create a new HashMap if it doesn't exist
        cooldowns.get(playerUUID).put(commandType, System.currentTimeMillis());
    }

    // Save cooldowns to the databaseConfig
    public static void saveCooldowns() {
        FileConfiguration config = Main.getInstance().databaseConfig;
        for (UUID uuid : cooldowns.keySet()) {
            for (String commandType : cooldowns.get(uuid).keySet()) {
                config.set("cooldowns." + uuid.toString() + "." + commandType, cooldowns.get(uuid).get(commandType));
            }
        }
        Main.getInstance().saveDatabase(); // Save to disk
    }

    public static void loadCooldowns() {
        FileConfiguration config = Main.getInstance().databaseConfig;

        // Check if the "cooldowns" section exists
        if (config.contains("cooldowns")) {
            // Check if we can retrieve the configuration section
            var cooldownsSection = config.getConfigurationSection("cooldowns");
            if (cooldownsSection != null) {
                for (String uuidString : cooldownsSection.getKeys(false)) {
                    UUID uuid = UUID.fromString(uuidString);
                    if (cooldownsSection.contains(uuidString)) {
                        var commandTypesSection = cooldownsSection.getConfigurationSection(uuidString);
                        if (commandTypesSection != null) {
                            for (String commandType : commandTypesSection.getKeys(false)) {
                                long lastUsedTime = commandTypesSection.getLong(commandType);
                                cooldowns.putIfAbsent(uuid, new HashMap<>());
                                cooldowns.get(uuid).put(commandType, lastUsedTime);
                            }
                        }
                    }
                }
            } else {
                // Log a message if the cooldowns section is null
                Bukkit.getLogger().warning("The cooldowns section in the config is null.");
            }
        } else {
            // Log a message if the "cooldowns" section does not exist
            Bukkit.getLogger().warning("The cooldowns section does not exist in the config.");
        }
    }

    public static void register(Main plugin) {

        for (AchievementCommand command : Configurated.getAchievementCommands()) {
            if (!CommandAPI.getRegisteredCommands().stream().filter(registeredCommand -> Objects.equals(registeredCommand.commandName(), command.name)).collect(Collectors.toList()).isEmpty()) {
                CommandAPI.unregister(command.name);
            }
        }

        for (AchievementCommand command : Configurated.getAchievementCommands()) {

            new CommandAPICommand(command.name)
                    .withPermission(command.getPermissionNode())
                    .executesPlayer((player, args) -> {

                        UUID playerUUID = player.getUniqueId();

                        if (isInCooldown(playerUUID, command.name, command.getDelay())) {
                            int timeLeft = getTimeLeft(playerUUID, command.name, command.getDelay());
                            if (command.getDelayMessage(command.getDelayMessage(util.formatTime(timeLeft))).isEmpty()) return;
                            player.sendMessage(command.getDelayMessage(util.formatTime(timeLeft)));
                            return;
                        }

                        // Execute the command since the delay is over
                        player.getInventory().addItem(plugin.createProtectorBlockItem(command.getRadius()));
                        if (!command.getMessage().isEmpty()) {
                            player.sendMessage(command.getMessage());
                        }

                        // Set the player's last use time to now
                        setCooldown(playerUUID, command.name);

                    }).register(Configurated.getAchievingCommandsNamespace());
        }

    }

}
