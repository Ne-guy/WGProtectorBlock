package me.Negai.WGProtectorBlock.API;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Negai.WGProtectorBlock.Main;
import org.mineacademy.fo.remain.nbt.NBT;
import org.mineacademy.fo.remain.nbt.ReadableNBT;

public class ProtectorBlock {
	private Main plugin;
	private Block block;

	public ProtectorBlock(Main plugin, Block block) {
		this.plugin = plugin;
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

	public World getWorld() {
		return block.getWorld();
	}

	public String getRegionId() {
        return NBT.getPersistentData(block.getState(), nbt -> {
			ReadableNBT pb = nbt.getCompound(plugin.ProtectorBlockTag);
			return pb.getString("RegionId");
        });
	}
	
	public ProtectedRegion getRegion() {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(block.getWorld()));
		
		return regions.getRegion(getRegionId());
	}
	
	public int getRadius() {
		return NBT.getPersistentData(block.getState(), nbt -> {
			ReadableNBT pb = nbt.getCompound(plugin.ProtectorBlockTag);
			return pb.getInteger("Radius");
        });
	}
	
	public void breakProtector(boolean physical) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(block.getWorld()));
		regions.removeRegion(getRegionId());
		if (physical) {
			block.getWorld().dropItem(block.getLocation(), plugin.createProtectorBlockItem(getRadius()));
		}
		block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 0.8f);
		
		block.setType(Material.AIR);
	}
}
