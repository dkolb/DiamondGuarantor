package com.krinchan.minecraft.diamondguarantor.commands;

import com.krinchan.minecraft.diamondguarantor.DiamondGuarantor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by david on 2/8/16.
 */
public class SetDiamondScoreCommand implements CommandExecutor {

    private DiamondGuarantor plugin;

    public SetDiamondScoreCommand(DiamondGuarantor plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //The command spec for this command doesn't allow you to get
        //here without a player and a score.

        Player target = args.<Player>getOne("player").get();
        int score = args.<Integer>getOne("score").get();

        plugin.getScoreService().setPlayerScore(target, score);

        return CommandResult.success();
    }

}
