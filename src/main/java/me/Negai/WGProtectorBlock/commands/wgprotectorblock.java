package me.Negai.WGProtectorBlock.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.Negai.WGProtectorBlock.Main;

public class wgprotectorblock {
    public static void register(Main plugin) {
        new CommandAPICommand("wgprotectorblock")
                .withPermission("wgprotectorblock.commands.wgprotectorblock")
                .withSubcommands(

                        new CommandAPICommand("reload")
                                .executes(((sender, args) -> {
                                    plugin.reloadConfigData();

                                    sender.sendMessage("§aReloaded successfully.");
                                }))

                ).register();
    }

}
