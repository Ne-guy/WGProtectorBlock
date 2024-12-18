package me.Negai.WGProtectorBlock;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class CustomTagUtils {

    public static void addCustomTag(ItemStack item, String pluginName, String keyString, String value) {
        if (item == null || pluginName == null || keyString == null || value == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(pluginName.toLowerCase(), keyString);
        dataContainer.set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static String getCustomTag(ItemStack item, String pluginName, String keyString) {
        if (item == null || pluginName == null || keyString == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(pluginName.toLowerCase(), keyString);
        return dataContainer.get(key, PersistentDataType.STRING);
    }
    
 // Method to retrieve all custom tags from an item
    public static Map<String, String> getAllCustomTags(ItemStack item, String pluginName) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return new HashMap<>();
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        Map<String, String> tags = new HashMap<>();

        for (NamespacedKey key : dataContainer.getKeys()) {
            if (pluginName.equals(key.getNamespace())) {
                String value = dataContainer.get(key, PersistentDataType.STRING);
                if (value != null) {
                    tags.put(key.getKey(), value);
                }
            }
        }

        return tags;
    }
}