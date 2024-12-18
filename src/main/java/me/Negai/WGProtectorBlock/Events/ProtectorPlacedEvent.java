package me.Negai.WGProtectorBlock.Events;

import me.Negai.WGProtectorBlock.API.ProtectorBlockItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProtectorPlacedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ProtectorBlockItem protectorBlockItem;
    private final Block blockPosition;

    private boolean isCancelled;

    public ProtectorPlacedEvent(Player player, ProtectorBlockItem item, Block block) {
        this.player = player;
        this.protectorBlockItem = item;
        this.blockPosition = block;
    }

    public Player getPlayer() {
        return player;
    }

    public ProtectorBlockItem getProtectorBlockItem() {
        return protectorBlockItem;
    }

    public Block getBlockPosition() {
        return blockPosition;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
