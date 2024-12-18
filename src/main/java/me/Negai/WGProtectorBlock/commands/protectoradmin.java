package me.Negai.WGProtectorBlock.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import me.Negai.WGProtectorBlock.API.ProtectorBlock;
import me.Negai.WGProtectorBlock.Main;
import me.Negai.WGProtectorBlock.menus.MainMenu;
import org.bukkit.entity.Player;

public class protectoradmin {

    public static ProtectorBlock getProtectorBlock(Player player, Main plugin) {
        // Cast a ray from the player's eye location in the direction they are looking at.
        return plugin.getProtectorBlock(player.getTargetBlockExact(5));
    }

    public static void register(Main plugin) {

        new CommandAPICommand("protectoradmin")
                .withAliases("prtctadmin")
                .withPermission("wgprotectorblock.commands.protectoradmin")
                .withSubcommands(

                        new CommandAPICommand("resetcooldowns")
                                .withArguments(new EntitySelectorArgument.OnePlayer("target"))
                                .executes(((sender, args) -> {

                                    Player player = (Player) args.get(0);

                                    achievingCommands.resetCooldowns(player);

                                    sender.sendMessage("§dAll the cooldowns of player §e"+player.getName()+"§d have been reset.");

                                })),

                        new CommandAPICommand("action")
                                .withArguments(new MultiLiteralArgument("category", "break", "physicalbreak", "menu"))
                                .executesPlayer(((player, args) -> {
                                    String arg = (String) args.get(0);

                                    ProtectorBlock protector = getProtectorBlock(player, plugin);

                                    if (protector == null) {
                                        player.sendMessage("§cYou are not staring at any protector block");
                                        return;
                                    }

                                    switch (arg) {
                                        case "break":
                                            player.sendMessage("§dProtector block broken.");
                                            protector.breakProtector(false);
                                            break;
                                        case "physicalbreak":
                                            player.sendMessage("§dProtector block physically broken.");
                                            protector.breakProtector(true);
                                            break;
                                        case "menu":
                                            new MainMenu(protector).displayTo(player);
                                            player.sendMessage("§dOpened Protector Block's menu.");
                                            break;
                                        default:
                                            player.sendMessage("§cInvalid category");
                                            break;
                                    }
                                })),

                        new CommandAPICommand("info")
                                .withArguments(new MultiLiteralArgument("category", "radius", "regionid", "region"))
                                .executesPlayer(((player, args) -> {

                                    String arg = (String) args.get(0);

                                    ProtectorBlock protector = getProtectorBlock(player, plugin);

                                    if (protector == null) {
                                        player.sendMessage("§cYou are not staring at any protector block");
                                        return;
                                    }

                                    switch (arg) {
                                        case "radius":
                                            player.sendMessage("This is the radius of this protector block: §a"+protector.getRadius());
                                            break;
                                        case "region":
                                            player.performCommand("rg info -w \""+protector.getWorld().getName()+"\" "+protector.getRegionId());
                                            break;
                                        case "regionid":
                                            player.sendMessage("This is the region Id of this protector block: '§a"+protector.getRegionId()+"§r'");
                                            break;
                                        default:
                                            player.sendMessage("§cInvalid category");
                                            break;
                                    }

                                }))

                )
                .register();

    }
}
