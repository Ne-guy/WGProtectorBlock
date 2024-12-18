package me.Negai.WGProtectorBlock.menus;

import me.Negai.WGProtectorBlock.API.ProtectorBlock;
import me.Negai.WGProtectorBlock.menutypes.PlayerSelectionMenu;
import me.Negai.WGProtectorBlock.menutypes.YesNoMenu;
import me.Negai.WGProtectorBlock.util.util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class OwnersMenu extends MenuPagged<OfflinePlayer> {

    private final ProtectorBlock protector;

    @Position(start = StartPosition.BOTTOM_CENTER)
    private ButtonMenu addOwner;

    public OwnersMenu(Menu parentMenu, ProtectorBlock protector) {
        super(parentMenu, util.getPlayersFromUUIDCollection(protector.getRegion().getOwners().getUniqueIds()));

        this.protector = protector;

        setTitle("Protector Block - Owners");
        setSize(9 * 3);
        setSound(null);

        addOwner = new ButtonMenu(new PlayerSelectionMenu(
                "Select a player",
                this,
                (OfflinePlayer selectedPlayer, ClickType clickType) -> {

                    protector.getRegion().getOwners().addPlayer(selectedPlayer.getUniqueId());

                    new OwnersMenu(getParent(), protector).displayTo(getViewer());

                },
                new ArrayList<>(Bukkit.getOnlinePlayers()).stream().filter(player -> !protector.getRegion().getOwners().getUniqueIds().contains(player.getUniqueId())).collect(Collectors.toList()),
                "Left Click to give this player",
                "ownership on your region"
        ),
                CompMaterial.NETHER_STAR,
                "&aAdd a Player"
        );
    }

    @Override
    protected ItemStack convertToItemStack(OfflinePlayer player) {
        String playerTitle = player.getName();
        String[] lore = {
                "Left Click to remove this player",
                "from owning your region"
        };

        if (player.getUniqueId() == getViewer().getUniqueId()) {
            playerTitle = "&a" + player.getName() + " (You)";

            lore = new String[] {
                    "Left Click to stop owning",
                    "this region"
            };
        }

        return ItemCreator.of(
                CompMaterial.PLAYER_HEAD,
                playerTitle,
                lore
        ).skullOwner(player.getName())
                .make();
    }

    @Override
    protected void onPageClick(Player player, OfflinePlayer offlinePlayer, ClickType clickType) {
        new YesNoMenu("Remove '" +offlinePlayer.getName()+ "' from owning?",
                () -> { // Yes
                    protector.getRegion().getOwners().removePlayer(offlinePlayer.getUniqueId());
                    if (offlinePlayer.getUniqueId() == player.getUniqueId()) {
                        player.closeInventory();
                        player.sendMessage("§dYou are now no longer owning this region.");
                    }
                    else {
                        new OwnersMenu(getParent(), protector).displayTo(getViewer());
                    }
                },
                () -> { // No
                    this.displayTo(player);
                }
        ).displayTo(player);
    }
}
