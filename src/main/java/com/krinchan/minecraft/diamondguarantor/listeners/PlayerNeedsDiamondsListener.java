package com.krinchan.minecraft.diamondguarantor.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.krinchan.minecraft.diamondguarantor.DiamondGuarantor;
import com.krinchan.minecraft.diamondguarantor.events.PlayerDiamondScoreIncrementEvent;
import com.krinchan.minecraft.diamondguarantor.events.PlayerGotDiamondsEvent;
import com.krinchan.minecraft.diamondguarantor.events.PlayerNeedsDiamondsEvent;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.StoneTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by david on 2/7/16.
 */
public class PlayerNeedsDiamondsListener {
    private DiamondGuarantor plugin;

    private static Random random = new Random();

    public PlayerNeedsDiamondsListener(DiamondGuarantor plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onPlayerNeedsDiamondsEvent(PlayerNeedsDiamondsEvent event) {
        List<Location<World>> candidates = getCandidatesForDiamondOre(event);

        //If the location is missing, cancel event and leave.
        //If there are no candidates, cancel and leave.

        if (candidates.size() == 0) {
            event.setCancelled(true);
            return;
        }

        Location<World> updatedBlock = updateRandomBlock(candidates);

        emitPlayerGotDiamondsEvent(event, updatedBlock);
    }

    private void emitPlayerGotDiamondsEvent(PlayerNeedsDiamondsEvent event, Location<World> updatedBlock) {
        plugin.getEventManager().post(new PlayerGotDiamondsEvent(event.getTargetEntity(),
                Cause.of(plugin, event, updatedBlock)));
    }

    private Location<World> updateRandomBlock(List<Location<World>> candidates) {
        //Compute block to change.
        int selectedBlock = random.nextInt(candidates.size());

        Location<World> updatedBlock = candidates.get(selectedBlock);
        updatedBlock.setBlockType(BlockTypes.DIAMOND_ORE);
        return updatedBlock;
    }

    private List<Location<World>> getCandidatesForDiamondOre(PlayerNeedsDiamondsEvent event) {
        return event
                    //Work our way to the block break event if possible.
                    .getCause().first(PlayerDiamondScoreIncrementEvent.class)
                    .flatMap(scoreIncrementEvent -> scoreIncrementEvent.getCause().first(ChangeBlockEvent.Break.class))

                    //Work our way to the first transaction we can find
                    //where the original block was of type stone.
                    .flatMap(breakEvent -> breakEvent.getTransactions()
                            .stream()
                            .filter(transaction -> transaction.getOriginal().getState().getType().equals(BlockTypes.STONE))
                            .findFirst())

                    //Grab the location (which may not be there).
                    .flatMap(transaction -> transaction.getOriginal().getLocation())
                    .map(location -> generateSurroundingBlockLocations(location)
                            .stream()
                            //Is of block type stone
                            .filter(candidate -> candidate.getBlock().getType().equals(BlockTypes.STONE))

                            //Is not granite or andorisite, etc since Diamond ore doesn't spawn
                            //in veins of those variants.
                            .filter(candidate -> candidate.getBlock().get(Keys.STONE_TYPE)
                                    .map(stoneType -> stoneType.equals(StoneTypes.STONE))
                                    .orElse(false))

                            //Has only one air block touching it.  Prevents a "visible" block
                            //from changing in front of the player.
                            //
                            //Maybe.
                            .filter(candidate -> generateSurroundingBlockLocations(candidate)
                                    .stream()
                                    .filter(candidateNeighbor ->
                                            candidateNeighbor.getBlock().getType().equals(BlockTypes.AIR))
                                    .count() == 1)
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
    }

    private List<Location<World>> generateSurroundingBlockLocations(Location<World> center) {
        World w = center.getExtent();
        Vector3i blockPos = center.getBlockPosition();
        List<Location<World>> results = new ArrayList<>(27);
        results.add(new Location<>(w, blockPos.add(new Vector3i(0, 1, 0))));
        results.add(new Location<>(w, blockPos.add(new Vector3i(0, -1, 0))));
        results.add(new Location<>(w, blockPos.add(new Vector3i(1, 0, 0))));
        results.add(new Location<>(w, blockPos.add(new Vector3i(-1, 0, 0))));
        results.add(new Location<>(w, blockPos.add(new Vector3i(0, 0, 1))));
        results.add(new Location<>(w, blockPos.add(new Vector3i(0, 0, -1))));
        return results;
    }
}
