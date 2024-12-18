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

public class MembersMenu extends MenuPagged<OfflinePlayer> {

    private ProtectorBlock protector;

    @Position(start = StartPosition.BOTTOM_CENTER)
    private ButtonMenu addMember;

    public MembersMenu(Menu parentMenu, ProtectorBlock protector) {
        super(parentMenu, util.getPlayersFromUUIDCollection(protector.getRegion().getMembers().getUniqueIds()));

        this.protector = protector;

        setTitle("Protector Block - Members");
        setSize(9 * 3);
        setSound(null);

        addMember = new ButtonMenu(new PlayerSelectionMenu(
                "Select a player",
                this,
                (OfflinePlayer selectedPlayer, ClickType clickType) -> {

                    protector.getRegion().getMembers().addPlayer(selectedPlayer.getUniqueId());

                    new MembersMenu(parentMenu, protector).displayTo(getViewer());

                },
                new ArrayList<>(Bukkit.getOnlinePlayers()).stream().filter(player -> !protector.getRegion().getMembers().getUniqueIds().contains(player.getUniqueId())).collect(Collectors.toList()),
                "Left Click to give this player",
                "access to your region"
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
                "from accessing your region"
        };

        if (player.getUniqueId() == getViewer().getUniqueId()) {
            playerTitle = "&a" + player.getName() + " (You)";

            lore = new String[] {
                    "Left Click to remove yourself",
                    "from members"
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
        new YesNoMenu("Remove \'"+offlinePlayer.getName()+"\' from members?",
                () -> { // Yes
                    protector.getRegion().getMembers().removePlayer(offlinePlayer.getUniqueId());

                    new MembersMenu(getParent(), protector).displayTo(getViewer());
                },
                () -> { // No
                    this.displayTo(player);
                }
        ).displayTo(player);
    }

//    @Override
//    protected void onDisplay(InventoryDrawer drawer, Player player) {
//        super.onDisplay(drawer, player);
//
//        restartMenu();
//        drawer.build();
//
//        Bukkit.getLogger().info("Menu Restarted & Built");
//    }
}
