package com.krinchan.minecraft.diamondguarantor.data;

import com.google.common.cache.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.krinchan.minecraft.diamondguarantor.DiamondGuarantor;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by david on 2/6/16.
 */
public class DiamondScoreJsonFileProvider implements DiamondScoreService {

    private Path scoreDirectory;
    private Logger logger;
    private boolean failureMode;
    private LoadingCache<UUID, PlayerData> playerDataCache;

    private static final Gson GSON_PARSER = new Gson();

    public DiamondScoreJsonFileProvider(DiamondGuarantor plugin) {
        this.scoreDirectory = plugin.getConfig().getConfigPath().getParent().resolve("playerData");
        this.logger = plugin.getLogger();
        this.failureMode = checkScoreDirectory();
        this.playerDataCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .removalListener(new RemovalListener<UUID, PlayerData>() {
                    @Override
                    public void onRemoval(RemovalNotification<UUID, PlayerData> notification) {
                        savePlayerData(notification.getKey(), notification.getValue());
                    }
                })
                .build(new CacheLoader<UUID, PlayerData>() {
                           @Override
                           public PlayerData load(UUID player) throws Exception {
                               return readPlayerData(player);
                           }
                       }

                );
    }

    @Override
    public int getPlayerScore(Player player) {
        try {
            return playerDataCache.get(player.getUniqueId()).getScore();
        } catch (ExecutionException e) {
            logExecutionException(player, e);
            return 0;
        }
    }

    @Override
    public void setPlayerScore(Player player, int score) {
        try {
            playerDataCache.get(player.getUniqueId()).setScore(score);
        } catch (ExecutionException e) {
            logExecutionException(player, e);
        }
    }

    @Override
    public int incrementPlayerScore(Player player, int increment) {
        try {
            PlayerData data = playerDataCache.get(player.getUniqueId());
            int result = data.getScore() + increment;

            if(result >= 0) {
                data.setScore(result);
            }

            logger.debug("{} is at score {}", player, result);
            return result;
        } catch (ExecutionException e) {
            logExecutionException(player, e);
            return 0;
        }
    }


    @Override
    public void loadPlayer(Player player) {
        //no op, cache lazy loads.
    }

    @Override
    public void unloadPlayer(Player player) {
        playerDataCache.invalidate(player.getUniqueId());
    }

    @Override
    public void loadScores() {
        //no op, our cache lazy loads.
    }

    @Override
    public void saveScores() {
        playerDataCache.asMap().forEach(this::savePlayerData);
    }

    private void savePlayerData(UUID player, PlayerData data) {
        if(failureMode) {
            return;
        }
        Path scoreFile = scoreDirectory.resolve(player.toString());
        try {
            Files.write(scoreFile, Arrays.asList(GSON_PARSER.toJson(data)));
        } catch (IOException e) {
            logger.error(
                    String.format("Could not write %s's data to %s.",
                            player,
                            scoreFile),
                    e);
        }

    }

    private PlayerData readPlayerData(UUID player) {
        return readScoreFile(player)
                .map(s -> GSON_PARSER.fromJson(s, PlayerData.class))
                .orElse(new PlayerData());
    }

    private Optional<String> readScoreFile(UUID player) {

        Path scoreFile = scoreDirectory.resolve(player.toString());

        Optional<String> result = Optional.empty();
        try {
            result = Optional.ofNullable(
                    Files.readAllLines(scoreFile)
                            .stream()
                            .collect(Collectors.joining())
            );
        } catch (IOException e) {
            logger.debug("Failed to read score file, {}, with error {}.  Starting with empty scores.",
                    scoreFile, e.getMessage());
        }
        return result;
    }

    private boolean checkScoreDirectory() {
        if (!Files.exists(scoreDirectory)) {
            try {
                Files.createDirectory(scoreDirectory);
            } catch (IOException e) {
                logger.error("Could not create {} as a directory.  Running in in-memory failure mode.", scoreDirectory);
                return true;
            }

        }

        if (!Files.isDirectory(scoreDirectory)) {
            logger.error("{} is not a directory!  Running in in-memory failure mode.");
            return true;
        }

        if (!Files.isWritable(scoreDirectory)) {
            logger.error("Cannot write to {}!  Running in in-memory failure mode.");
            return true;
        }

        return false;
    }

    private void logExecutionException(Player player, ExecutionException exception) {
        logger.warn(String.format("Failed to load scores for %s/%s.", player.getName(), player.getUniqueId(),
                exception));
    }

    private static class PlayerData {
        private int score;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }


}
