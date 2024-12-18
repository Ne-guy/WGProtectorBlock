package me.Negai.WGProtectorBlock.API;

import org.bukkit.inventory.ItemStack;

import me.Negai.WGProtectorBlock.Main;
import org.mineacademy.fo.remain.nbt.NBT;
import org.mineacademy.fo.remain.nbt.ReadableNBT;

public class ProtectorBlockItem {
	private ItemStack item;
	private Main plugin;

	public ProtectorBlockItem(ItemStack item, Main plugin) {
		this.item = item;
		this.plugin = plugin;
		
	}
	
	public int getRadius() {
        return NBT.get(item, nbt -> {
			ReadableNBT pb = nbt.getCompound(plugin.ProtectorBlockTag);
			return pb.getInteger("Radius");
        });
	}
	
}
