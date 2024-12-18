package me.Negai.WGProtectorBlock.menus;

import me.Negai.WGProtectorBlock.API.ProtectorBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class MainMenu extends Menu {

    private final ProtectorBlock protector;

    @Position(2)
    private final Button flagsButton;

    @Position(3)
    private final Button ownersButton;

    @Position(4)
    private final Button membersButton;

    @Position(5)
    private final Button infoButton;

    @Position(6)
    private final Button breakButton;


    public MainMenu(ProtectorBlock protector) {

        this.protector = protector;

        setTitle("Protector Block");
        setSize(9);
        setSound(null);

        this.flagsButton = new ButtonMenu(new FlagsMenu(this, protector), ItemCreator.of(CompMaterial.KNOWLEDGE_BOOK, "&9Flags"));

        this.ownersButton = new Button() {

            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new OwnersMenu(menu, protector).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.PLAYER_HEAD,
                                "Owners")
                        .skullUrl("http://textures.minecraft.net/texture/dad0581d3689b78096f1336e03ee5b0d371f27adab9b63f08b84fecf9318d2a0")
                        .make();
            }
        };

        this.membersButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new MembersMenu(menu, protector).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                                CompMaterial.PLAYER_HEAD,
                                "Members"
                        )
                        .skullUrl("http://textures.minecraft.net/texture/2a52d579afe2fdf7b8ecfa746cd016150d96beb75009bb2733ade15d487c42a1")
                        .make();
            }
        };

        this.infoButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

                player.performCommand("rg info -w \""+protector.getWorld().getName()+"\" "+protector.getRegionId());
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.OAK_SIGN,
                                "&fInfo")
                        .make();
            }
        };

        this.breakButton = new ButtonMenu(new BreakMenu(protector), ItemCreator.of(CompMaterial.BARRIER, "&cBreak"));
    }
}
