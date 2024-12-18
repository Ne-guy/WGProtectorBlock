package me.Negai.WGProtectorBlock.menutypes;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class PlayerSelectionMenu extends MenuPagged<OfflinePlayer> {

    private final String[] lore;

    public static interface PlayerSelectionResult {
        void execute(OfflinePlayer selectedPlayer, ClickType clickType);
    }

    private final PlayerSelectionResult action;

    public PlayerSelectionMenu(String title, Menu parentMenu, PlayerSelectionResult action, List<OfflinePlayer> players, String... lore) {
        super(parentMenu, players);

        setTitle(title);

        this.lore = lore;

        this.action = action;
    }

//    "Left click to select",
//            "this player"

    @Override
    protected ItemStack convertToItemStack(OfflinePlayer player) {
        String playerTitle = player.getName();

        if (player.getUniqueId() == getViewer().getUniqueId()) {
            playerTitle = "&a" + player.getName() + " (You)";
        }

        if (lore == null) {
            return ItemCreator.of(
                    CompMaterial.PLAYER_HEAD,
                    playerTitle
            ).skullOwner(player.getName())
                    .make();
        }
        else {
            return ItemCreator.of(
                    CompMaterial.PLAYER_HEAD,
                    playerTitle,
                    lore
            ).skullOwner(player.getName())
                    .make();
        }
    }

    @Override
    protected void onPageClick(Player player, OfflinePlayer selectedPlayer, ClickType clickType) {
        action.execute(selectedPlayer, clickType);
    }
}
