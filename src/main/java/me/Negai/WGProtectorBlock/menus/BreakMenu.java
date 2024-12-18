package me.Negai.WGProtectorBlock.menus;

import me.Negai.WGProtectorBlock.API.ProtectorBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class BreakMenu extends Menu {

    // Buttons
    @Position(start = StartPosition.CENTER, value = -1)
    private final Button yesButton = new Button() {

        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            protector.breakProtector(true);
            player.closeInventory();
        }

        @Override
        public ItemStack getItem() {
            return ItemCreator.of(CompMaterial.STRUCTURE_VOID, "Yes, break Protector").make();
        }
    };
    @Position(start = StartPosition.CENTER, value = +1)
    private final Button noButton = new Button() {

        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            getPreviousMenu(player).displayTo(player);
        }

        @Override
        public ItemStack getItem() {
            return ItemCreator.of(CompMaterial.BARRIER, "Cancel").make();
        }
    };
    // Buttons

    private final ProtectorBlock protector;

    public BreakMenu(ProtectorBlock protector) {
        this.protector = protector;

        setTitle("Break your protector block?");
        setSize(9);
        setSound(null);


    }
}
