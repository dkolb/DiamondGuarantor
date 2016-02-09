package com.krinchan.minecraft.diamondguarantor.data;

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
import java.util.stream.Collectors;

/**
 * Created by david on 2/6/16.
 */
public class DiamondScoreJsonFileProvider extends DiamondScoreService {

    private Path scoreFile;
    private Logger logger;

    private Map<UUID, Integer> scores;

    public DiamondScoreJsonFileProvider(DiamondGuarantor plugin) {
        super(plugin);
        this.scoreFile = plugin.getConfig().getConfigPath().getParent().resolve("scores.json");
        this.logger = plugin.getLogger();
        loadScores();
    }

    @Override
    public int getPlayerScore(Player player) {
        return scores.getOrDefault(player.getUniqueId(), 0);
    }

    @Override
    public void setPlayerScore(Player player, int score) {
        scores.put(player.getUniqueId(), score);
    }

    @Override
    public int incrementPlayerScore(Player player, int increment) {
        Integer result = scores.put(player.getUniqueId(), (scores.getOrDefault(player.getUniqueId(), 0) + increment));
        logger.debug("{} is at score {}", player, result);
        return result;
    }

    @Override
    public void loadScores() {
        Type t = new TypeToken<ConcurrentHashMap<UUID, Integer>>(){}.getType();
        scores = readScoreFile()
                .map(s -> new Gson().<ConcurrentHashMap<UUID, Integer>>fromJson(s, t))
                .orElse(new ConcurrentHashMap<>());
    }

    @Override
    public void saveScores() {
        try {
            Files.write(scoreFile, Arrays.asList(new Gson().toJson(scores)));
        } catch (IOException e) {
            logger.error("Cannot write out scores file {} due to error {}.", scoreFile, e.getMessage());
        }
    }


    private Optional<String> readScoreFile() {

        Optional<String> result = Optional.empty();
        try {
            result = Optional.ofNullable(
                    Files.readAllLines(scoreFile)
                            .stream()
                            .collect(Collectors.joining())
            );
        } catch (IOException e) {
            logger.error("Failed to read score file, {}, with error {}.  Starting with empty scores.",
                    scoreFile, e.getMessage());
        }
        return result;
    }
}
