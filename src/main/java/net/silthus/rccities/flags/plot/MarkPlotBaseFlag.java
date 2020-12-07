package net.silthus.rccities.flags.plot;

import net.milkbowl.vault.economy.Economy;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.flags.AbstractPlotFlag;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "MARK_FREE",
        friendlyName = "Fackelmarkierung",
        type = FlagType.BOOLEAN,
        cooldown = 0,
        needsRefresh = false
)
public class MarkPlotBaseFlag extends AbstractPlotFlag {

    public MarkPlotBaseFlag(Plot plot) {

        super(plot);
    }

    @Override
    public void refresh() throws RaidCraftException {

        if (getPlot() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());

        Chunk chunk = getPlot().getLocation().getChunk();
        ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot();
        if (currentValue) {

            //set torches
            Block block;
            int i;

            //EAST
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(i, chunkSnapshot.getHighestBlockYAt(i, 0), 0);
                setTorch(block);
            }

            //WEST
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(i, chunkSnapshot.getHighestBlockYAt(i, 15), 15);
                setTorch(block);
            }

            //NORTH
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(0, chunkSnapshot.getHighestBlockYAt(0, i), i);
                setTorch(block);
            }

            //SOUTH
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(15, chunkSnapshot.getHighestBlockYAt(15, i), i);
                setTorch(block);
            }
        } else {

            //remove torches
            Block block;
            int i;

            //EAST
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(i, chunkSnapshot.getHighestBlockYAt(i, 0), 0);
                removeTorch(block);
            }

            //WEST
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(i, chunkSnapshot.getHighestBlockYAt(i, 15), 15);
                removeTorch(block);
            }

            //NORTH
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(0, chunkSnapshot.getHighestBlockYAt(0, i), i);
                removeTorch(block);
            }

            //SOUTH
            for (i = 0; i < 16; i++) {
                block = chunk.getBlock(15, chunkSnapshot.getHighestBlockYAt(15, i), i);
                removeTorch(block);
            }
        }
    }

    private void setTorch(Block block) {

        Block belowBlock = block.getRelative(0, -1, 0);

        if (block.getType() != Material.AIR || !isTorchBlock(belowBlock)) return;

        block.setType(Material.TORCH);
    }

    private void removeTorch(Block block) {

        if (block.getType() != Material.TORCH) return;
        block.setType(Material.AIR);
    }

    private boolean isTorchBlock(Block block) {

        Material material = block.getType();

        return material.isSolid() && material.isBlock();
    }
}
