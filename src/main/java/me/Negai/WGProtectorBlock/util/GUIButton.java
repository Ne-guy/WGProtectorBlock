package me.Negai.WGProtectorBlock.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIButton {
	
	public static ItemStack getButton(String title, List<String> lore, Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta itemM = item.getItemMeta();
		itemM.setDisplayName(title);
		if (lore != null) {
			itemM.setLore(lore);
		}
		item.setItemMeta(itemM);
		
		return item;
	}
	
	public String title;
	public Material material;

	public GUIButton(String title, Material material) {
		this.title = title;
		this.material = material;
	}
	
	public ItemStack getItem() {
		ItemStack item = new ItemStack(material);
		ItemMeta itemM = item.getItemMeta();
		itemM.setDisplayName(title);
		item.setItemMeta(itemM);
		
		return item;
	}
}
