package com.krinchan.minecraft.diamondguarantor.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;

/**
 * Created by david on 2/7/16.
 */
public class PlayerDiamondScoreIncrementEvent implements TargetPlayerEvent, Cancellable {

    private boolean canceled;
    private Player player;
    private int scoreIncrement;
    private Cause cause;

    public PlayerDiamondScoreIncrementEvent(Player player, int scoreIncrement, Cause cause) {
        if(player == null || cause == null) {
            throw new IllegalArgumentException("Cannot create PlayerDiamondScoreIncrementEvent " +
                    "without a player or cause.");
        }

        canceled = false;
        this.player = player;
        this.scoreIncrement = scoreIncrement;
        this.cause = cause;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    @Override
    public Player getTargetEntity() {
        return player;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    public int getScoreIncrement() {
        return scoreIncrement;
    }
}
