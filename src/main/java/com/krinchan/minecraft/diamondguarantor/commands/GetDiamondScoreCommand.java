package com.krinchan.minecraft.diamondguarantor.commands;

import com.krinchan.minecraft.diamondguarantor.DiamondGuarantor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Created by david on 2/8/16.
 */
public class GetDiamondScoreCommand implements CommandExecutor {

    private DiamondGuarantor plugin;

    public GetDiamondScoreCommand(DiamondGuarantor plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        //Command spec requires thi argument.
        Player target = args.<Player>getOne("player").get();

        int score = plugin.getScoreService().getPlayerScore(target);

        src.sendMessage(Text.builder(
                String.format("%s's current diamond score is %d", target.getName(), score)).build());

        return CommandResult.success();
    }
}
