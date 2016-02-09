package com.krinchan.minecraft.diamondguarantor.listeners;

import com.krinchan.minecraft.diamondguarantor.DiamondGuarantor;
import com.krinchan.minecraft.diamondguarantor.config.DiamondGuarantorConfig;
import com.krinchan.minecraft.diamondguarantor.events.PlayerDiamondScoreIncrementEvent;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;

import java.util.Optional;

/**
 * Created by david on 2/6/16.
 */
public class StoneBlockEventListener {

    Logger logger;
    DiamondGuarantorConfig config;
    DiamondGuarantor plugin;
    EventManager eventManager;


    public StoneBlockEventListener(DiamondGuarantor plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.eventManager = plugin.getEventManager();
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        if(!isValidBreakingEvent(event)) {
            return;
        }

        getPlayer(event).ifPresent(p -> sendPlayerScoreIncrementEvent(p, event, config.getStoneValue()));
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        if(!isValidPlacingEvent(event)) {
            return;
        }

        getPlayer(event).ifPresent(p -> sendPlayerScoreIncrementEvent(p, event, config.getStoneValue() * -1));
    }

    private void sendPlayerScoreIncrementEvent(Player player, ChangeBlockEvent event, int scoreChange) {
        eventManager.post(new PlayerDiamondScoreIncrementEvent(player, scoreChange,
                Cause.of(plugin, player, event)));
    }

    private boolean isValidPlacingEvent(ChangeBlockEvent.Place event) {
        return !event.isCancelled()
                && hasPlayer(event)
                && hasTransactionFinallyAboutStoneBlock(event)
                && isAtRightDepth(event);
    }

    private boolean isValidBreakingEvent(ChangeBlockEvent.Break event) {
        return !event.isCancelled()
                && hasPlayer(event)
                && hasTransactionOriginallyAboutStoneBlock(event)
                && isAtRightDepth(event);
    }

    private boolean isAtRightDepth(ChangeBlockEvent event) {
         return getStoneBlockTransaction(event)
                 .flatMap(transaction -> transaction.getOriginal().getLocation())
                 .map(l -> l.getBlockY() <= config.getMaxY() && l.getBlockY() >= config.getMinY())
                 .orElse(false);
    }

    private boolean hasTransactionOriginallyAboutStoneBlock(ChangeBlockEvent.Break event) {
        return event.getTransactions().stream()
                .filter(this::isTransactionAboutBreakingStoneBlock)
                .findFirst().isPresent();
    }

    private boolean hasTransactionFinallyAboutStoneBlock(ChangeBlockEvent.Place event) {
        return event.getTransactions().stream()
                .filter(this::isTransactionAboutPlacingStoneBlock)
                .findFirst().isPresent();
    }

    private boolean hasPlayer(ChangeBlockEvent event) {
        return event.getCause().first(Player.class).isPresent();
    }

    private Optional<Player> getPlayer(ChangeBlockEvent event) {
        return event.getCause().first(Player.class);
    }

    private Optional<Transaction<BlockSnapshot>> getStoneBlockTransaction(ChangeBlockEvent event) {
        return event.getTransactions().stream()
                .filter(t -> isTransactionAboutBreakingStoneBlock(t) || isTransactionAboutPlacingStoneBlock(t))
                .findFirst();
    }

    private boolean isTransactionAboutBreakingStoneBlock(Transaction<BlockSnapshot> t) {
        return t.getOriginal().getState().getType().equals(BlockTypes.STONE);
    }

    private boolean isTransactionAboutPlacingStoneBlock(Transaction<BlockSnapshot> t) {
        return t.getFinal().getState().getType().equals(BlockTypes.STONE);
    }

}
