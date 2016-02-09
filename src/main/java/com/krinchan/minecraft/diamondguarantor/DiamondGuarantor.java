package com.krinchan.minecraft.diamondguarantor;

import com.google.inject.Inject;
import com.krinchan.minecraft.diamondguarantor.commands.GetDiamondScoreCommand;
import com.krinchan.minecraft.diamondguarantor.commands.SetDiamondScoreCommand;
import com.krinchan.minecraft.diamondguarantor.config.DiamondGuarantorConfig;
import com.krinchan.minecraft.diamondguarantor.data.DiamondScoreJsonFileProvider;
import com.krinchan.minecraft.diamondguarantor.data.DiamondScoreService;
import com.krinchan.minecraft.diamondguarantor.listeners.PlayerNeedsDiamondsListener;
import com.krinchan.minecraft.diamondguarantor.listeners.ScoreChangeListener;
import com.krinchan.minecraft.diamondguarantor.listeners.StoneBlockEventListener;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

/**
 * Created by david on 2/6/16.
 */
@Plugin(id = "DiamondGuarantor", name = "Diamond Guarantor", version = "0.1")
public class DiamondGuarantor {

    @Inject
    private Logger logger;

    @Inject
    private EventManager eventManager;

    @Inject
    @DefaultConfig(sharedRoot = false)
    Path configPath;

    private DiamondScoreService scoreService;

    private DiamondGuarantorConfig config;

    private PlayerNeedsDiamondsListener generator;


    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Recieved GameStartedServer Event!");

        config = loadConfig().orElseGet(DiamondGuarantorConfig::new);

        scoreService = new DiamondScoreJsonFileProvider(this);

        eventManager.registerListeners(this, new StoneBlockEventListener(this));
        eventManager.registerListeners(this, new ScoreChangeListener(this));
        eventManager.registerListeners(this, new PlayerNeedsDiamondsListener(this));

        registerCommands();

    }

    public Logger getLogger() {
        return logger;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public DiamondGuarantorConfig getConfig() {
        return config;
    }

    public DiamondScoreService getScoreService() {
        return scoreService;
    }

    private Optional<DiamondGuarantorConfig> loadConfig() {
        try {
            return Optional.of(DiamondGuarantorConfig.load(configPath));
        } catch (ObjectMappingException e) {
            logger.error("Couldn't map configuration file!", e);
        } catch (IOException e) {
            logger.error("Issue opening config file!", e);
        }
        return Optional.empty();
    }

    private void registerCommands() {

        CommandSpec commandSpec = CommandSpec.builder()
                .child(setDiamondScoreCommandSpec(), "setScore")
                .child(getDiamondScoreCommandSpec(), "getScore")
                .build();

        Sponge.getCommandManager().register(this, commandSpec, "dg", "diamondGaurantor");
    }

    private CommandSpec setDiamondScoreCommandSpec() {
        CommandSpec commandSpec = CommandSpec.builder()
                .arguments(player(Text.of("player")),
                        integer(Text.of("score")))
                .executor(new SetDiamondScoreCommand(this))
                .permission("diamondguarantor.command.setdiamondscore.use")
                .build();

        return commandSpec;
    }

    private CommandSpec getDiamondScoreCommandSpec() {
        CommandSpec commandSpec = CommandSpec.builder()
                .arguments(player(Text.of("player")))
                .executor(new GetDiamondScoreCommand(this))
                .permission("diamondguarantor.command.getdiamondscore.use")
                .build();

        return commandSpec;
    }
}
