package com.krinchan.minecraft.diamondguarantor.data;

import com.krinchan.minecraft.diamondguarantor.DiamondGuarantor;
import com.krinchan.minecraft.diamondguarantor.config.DiamondGuarantorConfig;
import com.krinchan.minecraft.diamondguarantor.events.PlayerDiamondScoreIncrementEvent;
import com.krinchan.minecraft.diamondguarantor.events.PlayerGotDiamondsEvent;
import com.krinchan.minecraft.diamondguarantor.events.PlayerNeedsDiamondsEvent;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by david on 2/6/16.
 */
public interface DiamondScoreService {

    int getPlayerScore(Player player);
    void setPlayerScore(Player player, int score);
    int incrementPlayerScore(Player player, int increment);
    void saveScores();
    void loadScores();
    void loadPlayer(Player player);
    void unloadPlayer(Player player);
}
