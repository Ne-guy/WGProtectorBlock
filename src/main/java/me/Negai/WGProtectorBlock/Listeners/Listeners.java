package me.Negai.WGProtectorBlock.Listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.Negai.WGProtectorBlock.Configurated;
import me.Negai.WGProtectorBlock.Events.ProtectorPlacedEvent;
import me.Negai.WGProtectorBlock.menus.MainMenu;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;

import me.Negai.WGProtectorBlock.Main;
import me.Negai.WGProtectorBlock.API.ProtectorBlock;
import me.Negai.WGProtectorBlock.API.ProtectorBlockItem;
import me.Negai.WGProtectorBlock.util.*;

public class Listeners implements Listener{
	
	public static HashMap<Player, ProtectorBlock> selectedProtector = new HashMap<Player, ProtectorBlock>();
	
	public static boolean playerHasSelectedProtector(Player player) {
		return selectedProtector.containsKey(player);
	}
	
	public static ProtectorBlock getPlayerSelectedProtector(Player player) {
		if (!selectedProtector.containsKey(player)) {
			return null;
		}
		return selectedProtector.get(player);
	}
	
	public static void setPlayerSelectedProtector(Player player, ProtectorBlock protector) {
		selectedProtector.put(player, protector);
	}
	
	public static void removePlayerSelectedProtector(Player player) {
		if (playerHasSelectedProtector(player)) {
			selectedProtector.remove(player);
		}
	}
	
	private Main plugin;

	public Location findBlockPlacingPosition(Location blockPosition, BlockFace blockFacing) {
		Location placingPosition = blockPosition;
		switch (blockFacing) {
		case NORTH:
			placingPosition.subtract(0, 0, 1);
			return placingPosition;
		case SOUTH:
			placingPosition.add(0, 0, 1);
			return placingPosition;
		case WEST:
			placingPosition.subtract(1, 0, 0);
			return placingPosition;
		case EAST:
			placingPosition.add(1, 0, 0);
			return placingPosition;
		case UP:
			placingPosition.add(0, 1, 0);
			return placingPosition;
		case DOWN:
			placingPosition.subtract(0, 1, 1);
			return placingPosition;
		default:
			return null;
		}
	}
	
	public Listeners(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onProtectorPlace(ProtectorPlacedEvent event) {
		Player player = event.getPlayer();

		Block block = event.getBlockPosition();

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
		if (regions != null) {
			ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
			if (set.testState(null, Main.PROTECTOR_PLACE_FLAG)) {
				// Custom flag is set to ALLOW; apply custom behavior
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();

		ProtectorBlock protector = Main.getInstance().getProtectorBlock(event.getBlock());

		if (protector == null) return;

		if (!player.hasPermission("WGProtectorBlock.handbreakprotector")) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cAccess Denied"));
			event.setCancelled(true);
			return;
		}

		protector.breakProtector(false);
	}

	@EventHandler
	public void onPlayerLeftClick(PlayerInteractEvent e) {
		/*
		Player player = e.getPlayer();
		
		if (player.isSneaking() && plugin.isProtectorBlock(e.getClickedBlock()) && e.getAction() == Action.LEFT_CLICK_BLOCK) {
			
			ProtectorBlock protector = plugin.getProtectorBlock(e.getClickedBlock());
			
			if (!protector.getRegion().getOwners().contains(player.getUniqueId())) return;
			
			Inventory gui = Bukkit.createInventory(player, InventoryType.HOPPER, "Break protector block?");
			
			ItemStack yes_Item = new ItemStack(Material.STRUCTURE_VOID);
			ItemMeta yes_ItemM = yes_Item.getItemMeta();
			yes_ItemM.setDisplayName("§fYes, break Protector");
			yes_Item.setItemMeta(yes_ItemM);
			
			ItemStack no_Item = new ItemStack(Material.BARRIER);
			ItemMeta no_ItemM = no_Item.getItemMeta();
			no_ItemM.setDisplayName("§fCancel");
			no_Item.setItemMeta(no_ItemM);
			
			gui.setItem(1, yes_Item);
			gui.setItem(3, no_Item);
			
			setPlayerSelectedProtector(player, plugin.getProtectorBlock(e.getClickedBlock()));
			player.openInventory(gui);
			return;
		}
		*/
	}
	
	//// Flags Menu ////
	public void flagsMenu(Player player, ProtectorBlock protector) {
		
		Inventory gui = Bukkit.createInventory(player, InventoryType.CHEST, "Protector Block - Flags");
		
		if (protector.getRegion().getFlag(Flags.ENTRY) == StateFlag.State.ALLOW) {
			gui.setItem(0, GUIButton.getButton("entry", Arrays.asList(
					"§7Access: §9ALLOWED"
					), Material.BLUE_BANNER));
		}
		else if (protector.getRegion().getFlag(Flags.ENTRY) == StateFlag.State.DENY) {
			gui.setItem(0, GUIButton.getButton("entry", Arrays.asList(
					"§7Access: §cDENIED"
					), Material.RED_BANNER));
			
		}
		else {
			gui.setItem(0, GUIButton.getButton("entry", Arrays.asList(
					"§7Access: §8DEFAULT"
					), Material.GRAY_BANNER));
		}
		
		player.openInventory(gui);
		
	}
	//// Flags Menu ////
	
	//// Owners Menu ////
	public void ownersMenu(Player player, ProtectorBlock protector) {
		
		Inventory gui = Bukkit.createInventory(player, InventoryType.CHEST, "Protector Block - Flags");
		
		List<String> owners = Arrays.asList((String[])protector.getRegion().getOwners().getPlayers().toArray());
		
		if (protector.getRegion().getFlag(Flags.ENTRY) == StateFlag.State.ALLOW) {
			gui.setItem(0, GUIButton.getButton("entry", Arrays.asList(
					"§7Access: §9ALLOWED"
					), Material.BLUE_BANNER));
		}
		else if (protector.getRegion().getFlag(Flags.ENTRY) == StateFlag.State.DENY) {
			gui.setItem(0, GUIButton.getButton("entry", Arrays.asList(
					"§7Access: §cDENIED"
					), Material.RED_BANNER));
			
		}
		else {
			gui.setItem(0, GUIButton.getButton("entry", Arrays.asList(
					"§7Access: §8DEFAULT"
					), Material.GRAY_BANNER));
		}
		
		player.openInventory(gui);
		
	}
	//// Owners Menu ////
	
	//// Members Menu ////
	public void membersMenu(Player player) {
		
		
		
	}
	//// Owners Menu ////
	
	//// Info Menu ////
	public void infoMenu(Player player) {
		
		
		
	}
	//// Info Menu ////
	
	//// Break Menu ////
	public void breakMenu(Player player) {
		
		Inventory gui = Bukkit.createInventory(player, InventoryType.HOPPER, "Break protector block?");
		
		ItemStack yes_Item = new ItemStack(Material.STRUCTURE_VOID);
		ItemMeta yes_ItemM = yes_Item.getItemMeta();
		yes_ItemM.setDisplayName("§fYes, break Protector");
		yes_Item.setItemMeta(yes_ItemM);
		
		ItemStack no_Item = new ItemStack(Material.BARRIER);
		ItemMeta no_ItemM = no_Item.getItemMeta();
		no_ItemM.setDisplayName("§fCancel");
		no_Item.setItemMeta(no_ItemM);
		
		gui.setItem(1, yes_Item);
		gui.setItem(3, no_Item);
		
		player.openInventory(gui);
		
	}
	//// Break Menu ////
	
	/*@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		
		// --FORM-- MAIN MENU
		if (event.getView().getTitle().equals("Protector Block") && playerHasSelectedProtector((Player)event.getWhoClicked())) {
        	
        	int slot = event.getSlot();
        	
        	Inventory clickedInventory = event.getClickedInventory();
        	Inventory topInventory = event.getView().getTopInventory();
        	Inventory bottomInventory = event.getView().getBottomInventory();
        	
        	if (clickedInventory != topInventory) {
        		return;
        	}
        	
        	Player player = (Player) event.getWhoClicked();
        	
        	ProtectorBlock protector = getPlayerSelectedProtector(player);
        	
            event.setCancelled(true);  // Prevent players from taking the items

            // Handle clicking on specific items
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();
                
                switch (slot) {
                case 0:
                	player.closeInventory();
                	flagsMenu(player, protector);
                	break;
                case 1:
                	player.closeInventory();
                	ownersMenu(player, protector);
                	break;
                case 2:
                	player.closeInventory();
                	membersMenu(player);
                	break;
                case 3:
                	player.closeInventory();
                	infoMenu(player);
                	break;
                case 4:
                	player.closeInventory();
                	breakMenu(player);
                	break;
                }
            }
        }
		
        // --FORM-- Break Protector Block
        if (event.getView().getTitle().equals("Break protector block?") && playerHasSelectedProtector((Player)event.getWhoClicked())) {
        	
        	int slot = event.getSlot();
        	
        	Inventory clickedInventory = event.getClickedInventory();
        	Inventory topInventory = event.getView().getTopInventory();
        	Inventory bottomInventory = event.getView().getBottomInventory();
        	
        	if (clickedInventory != topInventory) {
        		return;
        	}
        	
        	Player player = (Player) event.getWhoClicked();
        	
        	ProtectorBlock protector = getPlayerSelectedProtector(player);
        	
            event.setCancelled(true);  // Prevent players from taking the items

            // Handle clicking on specific items
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();
                
                switch (slot) {
                case 1:
                	player.closeInventory();
                	protector.breakProtector(true);
                	break;
                case 3:
                	player.closeInventory();
                	break;
                }
            }
        }
    }*/
	
	/*@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        
        removePlayerSelectedProtector(player);
    }*/
	
//	@EventHandler
//	public void onPlayerLeave(PlayerQuitEvent event) {
//		Player player = event.getPlayer();
//
//		removePlayerSelectedProtector(player);
//	}
	
	@EventHandler
	public void onPlayerRightClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		Block clickedBlock = e.getClickedBlock();
		
		if (plugin.isProtectorBlock(clickedBlock)) {
			if (!player.isSneaking()) {
				
				e.setCancelled(true);
				
				ProtectorBlock protector = plugin.getProtectorBlock(clickedBlock);

				if (protector.getRegion().getOwners().contains(player.getUniqueId())) {
					new MainMenu(protector).displayTo(player);
				}

//				setPlayerSelectedProtector(player, protector);
//
//				Inventory gui = Bukkit.createInventory(player, InventoryType.HOPPER, "Protector Block");
//
//				ItemStack flags_Item = new ItemStack(Material.KNOWLEDGE_BOOK);
//				ItemMeta flags_ItemM = flags_Item.getItemMeta();
//				flags_ItemM.setDisplayName("§9Flags");
//				flags_Item.setItemMeta(flags_ItemM);
//
//				ItemStack owners_Item = plugin.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFkMDU4MWQzNjg5Yjc4MDk2ZjEzMzZlMDNlZTViMGQzNzFmMjdhZGFiOWI2M2YwOGI4NGZlY2Y5MzE4ZDJhMCJ9fX0=");
//				SkullMeta owners_ItemM = (SkullMeta)owners_Item.getItemMeta();
//				owners_ItemM.setDisplayName("§fOwners");
//				owners_Item.setItemMeta(owners_ItemM);
//
//				ItemStack members_Item = plugin.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE1MmQ1NzlhZmUyZmRmN2I4ZWNmYTc0NmNkMDE2MTUwZDk2YmViNzUwMDliYjI3MzNhZGUxNWQ0ODdjNDJhMSJ9fX0=");
//				SkullMeta members_ItemM = (SkullMeta)members_Item.getItemMeta();
//				members_ItemM.setDisplayName("§fMembers");
//				members_Item.setItemMeta(members_ItemM);
//
//				ItemStack info_Item = new ItemStack(Material.OAK_SIGN);
//				ItemMeta info_ItemM = info_Item.getItemMeta();
//				info_ItemM.setDisplayName("§fInfo");
//				info_Item.setItemMeta(info_ItemM);
//
//				ItemStack cancel_Item = new ItemStack(Material.BARRIER);
//				ItemMeta cancel_ItemM = cancel_Item.getItemMeta();
//				cancel_ItemM.setDisplayName("§cBreak");
//				cancel_Item.setItemMeta(cancel_ItemM);
//
//				gui.setItem(0, flags_Item);
//				gui.setItem(1, owners_Item);
//				gui.setItem(2, members_Item);
//				gui.setItem(3, info_Item);
//				gui.setItem(4, cancel_Item);
//
//				setPlayerSelectedProtector(player, protector);
				
				return;
			}
		}
		
		ItemStack item = e.getItem();
		
		if (!plugin.isProtectorBlockItem(item)) return;
		
		ProtectorBlockItem pbitem = new ProtectorBlockItem(item, plugin);
		
		Location blockPlacingLocation = findBlockPlacingPosition(e.getClickedBlock().getLocation(), e.getBlockFace());

		Block block = blockPlacingLocation.getWorld().getBlockAt(blockPlacingLocation);
		
		if (block.getType().isSolid()) return;
		
		if (player.getGameMode() == GameMode.ADVENTURE) return;

		Location corner1 = block.getLocation();
		Location corner2 = block.getLocation();

		corner1.subtract(pbitem.getRadius(), 0, pbitem.getRadius());
		corner2.add(pbitem.getRadius(), 0, pbitem.getRadius());
		corner1.setY(Main.getInstance().getConfig().getDouble("protector-regions-y1"));
		corner2.setY(Main.getInstance().getConfig().getDouble("protector-regions-y2"));

		if (Configurated.getDisabledWorlds().contains(block.getWorld())) {
			if (!Configurated.getDisabledWorldsMessage().isEmpty()) {
				player.sendMessage(Main.replaceTextColor(Configurated.getDisabledWorldsMessage()));
			}
			return;
		}

		if (util.willRegionOverlapAnyExisting(block.getWorld(), BlockVector3.at(corner1.getX(), corner1.getY(), corner1.getZ()), BlockVector3.at(corner2.getX(), corner2.getY(), corner2.getZ()))) {

			player.sendMessage(
					"§cYou cannot place a protector block here because it overlaps with an existing protected region.",
					"§cPlease try placing it in a different location."
			);

			return;
		}

		ProtectorPlacedEvent ppEvent = new ProtectorPlacedEvent(player, pbitem, block);

		Bukkit.getPluginManager().callEvent(ppEvent);

		if (ppEvent.isCancelled()) {e.setCancelled(true); return;}

		// Block Placing
		blockPlacingLocation.getWorld().playSound(blockPlacingLocation, Sound.BLOCK_STONE_PLACE, 1f, 0.8f);

		plugin.setProtectorBlock(block, plugin.getRandomRegionId(block), pbitem.getRadius(), player);
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			plugin.removeOneItemFromMainHand(player);
		}
		
		player.sendMessage("§dYou have put a protector block.");
		player.sendMessage("§dRight Click on your protector block to modify it");
		// Block Placing
	}
	
	@EventHandler
	public void onPlayerPlaceBlockEvent(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		
		if (plugin.isProtectorBlockItem(e.getItemInHand())) e.setCancelled(true);
	}
	
}