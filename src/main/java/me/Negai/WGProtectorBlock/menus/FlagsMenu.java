package me.Negai.WGProtectorBlock.menus;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Negai.WGProtectorBlock.API.ProtectorBlock;
import me.Negai.WGProtectorBlock.Configurated;
import me.Negai.WGProtectorBlock.util.util;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Collections;
import java.util.List;

public class FlagsMenu extends MenuPagged<util.ProtectorFlag> {

    private final ProtectorBlock protector;

    public static List<StateFlag> displayedFlags = Collections.singletonList(
            Flags.ENTRY
    );

    public static ItemStack makeFlagItem(Flag<?> flag, ProtectedRegion region) {
        if (region.getFlag(flag) == StateFlag.State.ALLOW) {
            return ItemCreator.of(CompMaterial.BLUE_BANNER, "&9"+flag.getName(), "Access: "+"&9ALLOWED").make();
        } else if (region.getFlag(flag) == StateFlag.State.DENY) {
            return ItemCreator.of(CompMaterial.RED_BANNER, "&c"+flag.getName(), "Access: "+"&cDENIED").make();
        } else if (region.getFlag(flag) == null) {
            return ItemCreator.of(CompMaterial.WHITE_BANNER, "&7"+flag.getName(), "Access: "+"&7UNSET").make();
        } else {
            return ItemCreator.of(CompMaterial.WHITE_BANNER, "&4"+flag.getName(), "Access: "+"&cERROR").make();
        }
    }

    public FlagsMenu(Menu parentMenu, ProtectorBlock protector) {

        super(parentMenu, Configurated.getProtectorFlags());

        this.protector = protector;

        setTitle("Protector Block - Flags");
        //setSize(9 * 3);
        setSound(null);

        /*for (int i = 0; i < getSize(); i++) {
            if (!util.isIndexInBounds(i, displayedFlags)) {
                if (i < 8) {
                    setItem(i, makeFlagItem(displayedFlags.get(i), protector.getRegion()));
                }
            }
        }*/

    }

    @Override
    protected ItemStack convertToItemStack(util.@NotNull ProtectorFlag protectorFlag) {
        return FlagsMenu.makeFlagItem(protectorFlag.getState(), protector.getRegion());
    }

    @Override
    protected String[] getInfo() {
        return new String[] {
                "Left click to toggle the flag state (Allow/Deny access).",
                "",
                "Right click to Unset a flag."
        };
    }

    @Override
    protected void onPageClick(Player player, util.ProtectorFlag protectorFlag, ClickType clickType) {
        try {
            if (clickType.isLeftClick()) {
                if (protector.getRegion().getFlag(protectorFlag.getState()) == StateFlag.State.ALLOW) {
                    protector.getRegion().setFlag((StateFlag)protectorFlag.getState(), StateFlag.State.DENY);
                } else if (protector.getRegion().getFlag(protectorFlag.getState()) == StateFlag.State.DENY) {
                    protector.getRegion().setFlag((StateFlag)protectorFlag.getState(), StateFlag.State.ALLOW);
                } else if (protector.getRegion().getFlag(protectorFlag.getState()) == null) {
                    protector.getRegion().setFlag((StateFlag)protectorFlag.getState(), StateFlag.State.ALLOW);
                }
                else {
                    return;
                }
            } else if (clickType.isRightClick()) {
                if (protector.getRegion().getFlag(protectorFlag.getState()) == null) {
                    //protector.getRegion().setFlag((StateFlag)protectorFlag.getState(), StateFlag.State.ALLOW);
                }
                else {
                    protector.getRegion().setFlag(protectorFlag.getState(), null);
                }
            }
            else {
                return;
            }
            redrawButtons();
        } catch (Exception ignored) {}
    }
}
