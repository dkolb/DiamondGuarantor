package com.krinchan.minecraft.diamondguarantor.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;

/**
 * Created by david on 2/7/16.
 */
public class PlayerGotDiamondsEvent implements TargetPlayerEvent, Cancellable {

    private boolean cancelled;
    private Player player;
    private Cause cause;

    public PlayerGotDiamondsEvent(Player player, Cause cause) {
        if (player == null || cause == null) {
            throw new IllegalArgumentException("Can't create PlayerNeedsDiamondsEvent with a null player or cause.");
        }
        this.player = player;
        this.cause = cause;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Player getTargetEntity() {
        return player;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}