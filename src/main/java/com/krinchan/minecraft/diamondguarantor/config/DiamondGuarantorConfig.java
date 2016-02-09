package com.krinchan.minecraft.diamondguarantor.config;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Created by david on 2/6/16.
 */
@ConfigSerializable
public class DiamondGuarantorConfig implements Serializable {

    @Setting(comment = "Max depth to count mined stone at.")
    private int maxY = 16;

    @Setting(comment = "Min depth to count mined stone at.")
    private int minY = 0;

    @Setting(comment = "Value every mined stone block adds to player's diamond score.")
    private int stoneValue = 1;

    @Setting(comment = "Value at which player is awarded a diamond block.")
    private int diamondValue = 600;

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    private Path configPath;

    private void setLoader(ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.configLoader = loader;
    }

    public Path getConfigPath() {
        return configPath;
    }

    public void setConfigPath(Path configPath) {
        this.configPath = configPath;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }

    public int getStoneValue() {
        return stoneValue;
    }

    public int getDiamondValue() {
        return diamondValue;
    }

    public void save() throws ObjectMappingException, IOException {
        SimpleCommentedConfigurationNode out = SimpleCommentedConfigurationNode.root();

        ObjectMapper.forObject(this).serialize(out);

        configLoader.save(out);
    }


    public static DiamondGuarantorConfig load(Path configPath)
            throws IOException, ObjectMappingException {

        boolean saveDefaults = false;

        if(!configPath.toFile().exists()) {
            saveDefaults = true;
        }

        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setPath(configPath)
                .build();

        CommentedConfigurationNode node = loader.load(ConfigurationOptions.defaults());

        DiamondGuarantorConfig config = ObjectMapper.forClass(DiamondGuarantorConfig.class)
                .bindToNew().populate(node);

        config.setLoader(loader);
        config.setConfigPath(configPath);

        if(saveDefaults) {
            config.save();
        }

        return config;
    }
}
