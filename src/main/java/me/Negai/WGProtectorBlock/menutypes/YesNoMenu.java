package me.Negai.WGProtectorBlock.menutypes;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class YesNoMenu extends Menu {

    private Runnable yesAction;
    private Runnable noAction;

    private ItemStack yesItem;
    private ItemStack noItem;

    @Position(start = StartPosition.CENTER, value = -1)
    private final Button yes = new Button() {
        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            if (yesAction == null) return;
            yesAction.run();
        }

        @Override
        public ItemStack getItem() {
            return yesItem;
        }
    };

    @Position(start = StartPosition.CENTER, value = +1)
    private final Button no = new Button() {
        @Override
        public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            if (noAction == null) return;
            noAction.run();
        }

        @Override
        public ItemStack getItem() {
            return noItem;
        }
    };

    private void init(
            String title,
            Runnable yesAction,
            Runnable noAction
    ) {
        setTitle(title);
        setSize(9);
        this.yesAction = yesAction;
        this.noAction = noAction;
    }

    public YesNoMenu(String title, Runnable yesAction, Runnable noAction) {
        init(title, yesAction, noAction);

        this.yesItem = ItemCreator.of(CompMaterial.STRUCTURE_VOID, "Yes").make();
        this.noItem = ItemCreator.of(CompMaterial.BARRIER, "No").make();
    }

    public YesNoMenu(String title, Runnable yesAction, Runnable noAction, String yesTitle, String noTitle) {
        init(title, yesAction, noAction);

        this.yesItem = ItemCreator.of(CompMaterial.STRUCTURE_VOID, yesTitle).make();
        this.noItem = ItemCreator.of(CompMaterial.BARRIER, noTitle).make();

    }

    public YesNoMenu(String title, Runnable yesAction, Runnable noAction, String yesTitle, String noTitle, String[] yesLore, String[] noLore) {
        init(title, yesAction, noAction);

        if (yesLore != null) {
            this.yesItem = ItemCreator.of(CompMaterial.STRUCTURE_VOID, yesTitle, yesLore).make();
        }
        else {
            this.yesItem = ItemCreator.of(CompMaterial.STRUCTURE_VOID, yesTitle).make();
        }
        if (noLore != null) {
            this.noItem = ItemCreator.of(CompMaterial.BARRIER, noTitle, noLore).make();
        }
        else {
            this.noItem = ItemCreator.of(CompMaterial.BARRIER, noTitle).make();
        }
    }
}
