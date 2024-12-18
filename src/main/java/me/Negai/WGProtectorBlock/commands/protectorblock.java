package me.Negai.WGProtectorBlock.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import me.Negai.WGProtectorBlock.Main;

public class protectorblock {
    public static void register(Main plugin) {

        new CommandAPICommand("protectorblock")
                .withArguments(new IntegerArgument("radius"))
                .withPermission("wgprotectorblock.commands.protectorblock")
                .executesPlayer(((player, args) -> {
                    int radius = (int)args.get(0);

                    player.getInventory().addItem(plugin.createProtectorBlockItem(radius));

                    player.sendMessage("§dYou got a Protector Block now of §e"+radius+"§d radius.");
                }))
                .register();

    }
}
