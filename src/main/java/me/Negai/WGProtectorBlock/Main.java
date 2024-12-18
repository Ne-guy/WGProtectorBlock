package me.Negai.WGProtectorBlock;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.Negai.WGProtectorBlock.commands.achievingCommands;
import me.Negai.WGProtectorBlock.commands.protectoradmin;
import me.Negai.WGProtectorBlock.commands.protectorblock;
import me.Negai.WGProtectorBlock.commands.wgprotectorblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Negai.WGProtectorBlock.API.ProtectorBlock;
import me.Negai.WGProtectorBlock.Listeners.Listeners;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.nbt.NBT;
import org.mineacademy.fo.remain.nbt.ReadWriteNBT;

public class Main extends SimplePlugin {

	public String ProtectorBlockTag = getDescription().getName()+".ProtectorBlock";

	public static final StateFlag PROTECTOR_PLACE_FLAG = new StateFlag("protector-place", true);
	
	public static String replaceTextColor(String text) {
		text = text.replace("&0", "§0");
		text = text.replace("&1", "§1");
		text = text.replace("&2", "§2");
		text = text.replace("&3", "§3");
		text = text.replace("&4", "§4");
		text = text.replace("&5", "§5");
		text = text.replace("&6", "§6");
		text = text.replace("&7", "§7");
		text = text.replace("&8", "§8");
		text = text.replace("&9", "§9");
		text = text.replace("&a", "§a");
		text = text.replace("&b", "§b");
		text = text.replace("&c", "§c");
		text = text.replace("&d", "§d");
		text = text.replace("&e", "§e");
		text = text.replace("&f", "§f");
		text = text.replace("&r", "§r");
		text = text.replace("&l", "§l");
		text = text.replace("&o", "§o");
		text = text.replace("&n", "§n");
		text = text.replace("&m", "§m");
		text = text.replace("&k", "§k");
		return text;
	}
	
//	public void sendBlockBreakAnimation(Player player, int x, int y, int z, int stage) {
//        try {
//            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
//            packet.getIntegers().write(0, player.getEntityId()); // Entity ID, can be any unique ID
//            packet.getBlockPositionModifier().write(0, new BlockPosition(x, y, z));
//            packet.getIntegers().write(1, stage); // Stage of breaking (0 - 9)
//
//            protocolManager.sendServerPacket(player, packet);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
	
	public ItemStack getCustomHead(String base64) {
        // Create a player head item stack
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        // Create a new GameProfile with a random UUID
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        // Add the base64 texture to the profile
        profile.getProperties().put("textures", new Property("textures", base64));

        // Use reflection to set the GameProfile on the SkullMeta
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Apply the SkullMeta to the ItemStack
        skull.setItemMeta(skullMeta);

        return skull;
    }
	
	public boolean removeOneItemFromMainHand(Player player) {
	    ItemStack itemInHand = player.getInventory().getItemInMainHand();

	    if (itemInHand != null && itemInHand.getType() != Material.AIR) {
	        int amount = itemInHand.getAmount();

	        if (amount > 1) {
	            itemInHand.setAmount(amount - 1);
	        } else {
	            player.getInventory().setItemInMainHand(null);
	        }
	        return true;
	    }
	    return false;
	}
	
	public void loadConfig() {
        // Ensure the plugin folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Load the database.yml file
        databaseConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "database.yml"));
        
        databaseFile = new File("plugins/"+getDescription().getName()+ "/database.yml");
    }
	
	public YamlConfiguration databaseConfig;
	
	public File databaseFile;

//	private ProtocolManager protocolManager;
	
	public void saveDatabase() {
		try { databaseConfig.save(databaseFile); } catch (IOException e) {Bukkit.getLogger().log(Level.SEVERE, "Unable to save changes in the database.yml file.");}
	}
	
	public static void sendTestMessage(String msg) {
		Player target = Bukkit.getPlayer(UUID.fromString("fe723967-f802-41ff-988e-8616c19fd8bd"));
		if (target != null) {
			target.sendMessage(msg);
		}
	}
	
	public void reloadConfigData() {
		reloadConfig();
		loadConfig();
		achievingCommands.loadCooldowns();
		achievingCommands.register(this);
	}
	
	public String getConfigString(String key) {
		return getConfig().getString(key);
	}
	
	public boolean getConfigBool(String key) {
		return getConfig().getBoolean(key);
	}
	
	public int getConfigInt(String key) {
		return getConfig().getInt(key);
	}
	
	public boolean isProtectorBlockItem(ItemStack item) {
		if (item == null) return false;
		return NBT.get(item, nbt -> {
			if (nbt.getKeys().contains(ProtectorBlockTag) && item.getType() == Material.STRUCTURE_BLOCK) return true;
			else return false;
        });
	}
	
	public boolean isProtectorBlock(Block block) {
	    if (block == null) return false;  // Ensure block is not null
	    if (block.getState() == null) return false;  // Ensure block has a state

	    // Check if block is a TileEntity (only TileEntities have NBT data)
	    if (block.getState() instanceof TileState) {
	        TileState tileState = (TileState) block.getState();
	        
	        // Use NBT.getPersistentData only if the block is a TileEntity
	        return NBT.getPersistentData(tileState, nbt -> {
	            // Check for the ProtectorBlockTag and ensure the block is a structure block
	            return nbt.getKeys().contains(ProtectorBlockTag) && block.getType() == Material.STRUCTURE_BLOCK;
	        });
	    }

	    return false;  // Return false if the block is not a TileEntity
	}
	
	public String getRandomRegionId(Block block) {
		
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(block.getWorld()));
		
		for (int i = 0; i < getConfig().getInt("max-regions-number"); i++) {
			String id = getDescription().getName()+"-ProtectorBlockRegion-"+i;
			if (regions.hasRegion(id)) continue;
			return id;
		}
        
		return null;
		
	}

	@Nullable
	public ProtectorBlock getProtectorBlock(Block block) {
		if (!isProtectorBlock(block)) {
			return null;
		}
		return new ProtectorBlock(this, block);
	}
	
	public void setProtectorBlock(Block block, String regionId, int radius, Player owner) {
        
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(block.getWorld()));
		
		block.setType(Material.STRUCTURE_BLOCK);
		
        NBT.modify(block.getState(), nbt -> {
        	nbt.setString("mode", "DATA");
			ReadWriteNBT pb = nbt.getOrCreateCompound(ProtectorBlockTag);
			pb.setString("RegionId", regionId);
			pb.setInteger("Radius", radius);
        });
        
        NBT.modifyPersistentData(block.getState(), nbt -> {
        	ReadWriteNBT pb = nbt.getOrCreateCompound(ProtectorBlockTag);
			pb.setString("RegionId", regionId);
			pb.setInteger("Radius", radius);
        });
        
        Location pos1 = block.getLocation();
        Location pos2 = block.getLocation();
        
        pos1.subtract(radius, 0, radius);
        pos2.add(radius, 0, radius);
        pos1.setY(getConfig().getDouble("protector-regions-y1"));
        pos2.setY(getConfig().getDouble("protector-regions-y2"));
        
        ProtectedRegion region = new ProtectedCuboidRegion(regionId, BlockVector3.at(pos1.getBlockX(),pos1.getBlockY(),pos1.getBlockZ()), BlockVector3.at(pos2.getBlockX(),pos2.getBlockY(),pos2.getBlockZ()));
        DefaultDomain ownerGroup = region.getOwners();
        ownerGroup.addPlayer(owner.getUniqueId());
        region.setOwners(ownerGroup);
        regions.addRegion(region);
        
	}
	
	public ItemStack createProtectorBlockItem(int radius) {
		ItemStack item = new ItemStack(Material.STRUCTURE_BLOCK);
	    ItemMeta meta = item.getItemMeta();

	    meta.setDisplayName(ChatColor.RESET+replaceTextColor(getConfigString("protector-block-name")));
        meta.setLore(Arrays.asList(
        		
        		"§d§lProtector",
        		"§dRadius: "+radius
        		
        		));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        
        NBT.modify(item, nbt -> {
			ReadWriteNBT pb = nbt.getOrCreateCompound(ProtectorBlockTag);
			pb.setInteger("Radius", radius);
        });
        
        return item;
	}

	public void registerPermission(String node) {
		Permission perm = new Permission(node);
		getServer().getPluginManager().addPermission(perm);
	}

	@Override
	public void onPluginLoad() {
		CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true).usePluginNamespace());
	}
	
	@Override
	public void onPluginStart() {

		WorldGuard.getInstance().getFlagRegistry().register(PROTECTOR_PLACE_FLAG);

		CommandAPI.onEnable();

		instance = this;
		
		loadConfig();
		saveDatabase();
		
//		protocolManager = ProtocolLibrary.getProtocolManager();

		registerPermission("WGProtectorBlock.handbreakprotector");

		new Listeners(this);

		wgprotectorblock.register(this);
		protectorblock.register(this);
		protectoradmin.register(this);
		
		saveDefaultConfig();

		reloadConfigData();
		
		Bukkit.getLogger().info("["+getDescription().getName()+"] "+getDescription().getName()+" (by Negai_) was enabled successfully.");
	}
	
	@Override
	public void onPluginStop() {

		CommandAPI.onDisable();

		achievingCommands.saveCooldowns();

		Bukkit.getLogger().info("["+getDescription().getName()+"] "+ getDescription().getName()+" (by Negai_) was disabled successfully.");
	}

	public static Main getInstance() {
		return instance;
	}

	private static Main instance;
	
}