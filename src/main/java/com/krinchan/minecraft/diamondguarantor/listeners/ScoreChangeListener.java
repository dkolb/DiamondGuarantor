package com.krinchan.minecraft.diamondguarantor.listeners;

import com.krinchan.minecraft.diamondguarantor.DiamondGuarantor;
import com.krinchan.minecraft.diamondguarantor.data.DiamondScoreService;
import com.krinchan.minecraft.diamondguarantor.events.PlayerDiamondScoreIncrementEvent;
import com.krinchan.minecraft.diamondguarantor.events.PlayerGotDiamondsEvent;
import com.krinchan.minecraft.diamondguarantor.events.PlayerNeedsDiamondsEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by david on 2/7/16.
 */
public class ScoreChangeListener {
    private DiamondScoreService scoreService;
    private DiamondGuarantor plugin;
    private Optional<World> worldSaveKey = Optional.empty();

    public ScoreChangeListener(DiamondGuarantor plugin) {
        this.scoreService = plugin.getScoreService();
        this.plugin = plugin;
    }

    @Listener(order=Order.LAST)
    public void onGameStartedEvent(GameStartedServerEvent event) {
        scoreService.loadScores();
    }

    @Listener
    public void onWorldSave(SaveWorldEvent event) {

        if (worldSaveKey.isPresent()) {
            if (!event.getTargetWorld().equals(worldSaveKey.get())) {
                return;
            }
        } else {
            worldSaveKey = Optional.of(event.getTargetWorld());
        }

        if (event.isCancelled()) {
            return;
        }

        plugin.getLogger().debug("Flushing scores to disk.");

        scoreService.saveScores();
    }

    @Listener
    public void onAdjustDiamondScoreEvent(PlayerDiamondScoreIncrementEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player p = event.getTargetEntity();

        int newScore = scoreService.incrementPlayerScore(p, event.getScoreIncrement());
        plugin.getLogger().debug("Incremented {} by {}.", p.getName(), event.getScoreIncrement());

        if (newScore > plugin.getConfig().getDiamondValue()) {
            emitPlayerNeedsDiamondsEvent(event);
        }
    }

    @Listener
    public void onPlayerGotDiamondsEvent(PlayerGotDiamondsEvent event) {
        if (event.isCancelled()) {
            return;
        }

        UUID playerUUID = event.getTargetEntity().getUniqueId();
        scoreService.incrementPlayerScore(event.getTargetEntity(), (plugin.getConfig().getDiamondValue() * -1));
    }

    @Listener
    public void onClientDisconnectionEvent(ClientConnectionEvent.Disconnect event) {
        scoreService.unloadPlayer(event.getTargetEntity());
    }

    @Listener
    public void onClientJoinEvent(ClientConnectionEvent.Join event) {
        scoreService.loadPlayer(event.getTargetEntity());
    }

    private void emitPlayerNeedsDiamondsEvent(PlayerDiamondScoreIncrementEvent event) {
        plugin.getEventManager().post(new PlayerNeedsDiamondsEvent(event.getTargetEntity(),
                Cause.of(plugin, event.getTargetEntity(), event)));
    }

}
