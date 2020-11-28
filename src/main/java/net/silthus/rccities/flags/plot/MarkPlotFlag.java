package net.silthus.rccities.flags.plot;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.flags.AbstractPlotFlag;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.flags.FlagType;
import de.raidcraft.rccities.api.plot.Plot;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "MARK",
        friendlyName = "Fackelmarkierung",
        type = FlagType.BOOLEAN,
        cooldown = 5,
        needsRefresh = false
)
public class MarkPlotFlag extends AbstractPlotFlag {

    public MarkPlotFlag(Plot plot) {

        super(plot);
    }

    @Override
    public void refresh() throws RaidCraftException {

        if (getPlot() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());
        String bankAccount = getPlot().getCity().getBankAccountName();
        double markCost = RaidCraft.getComponent(RCCitiesPlugin.class).getConfig().flagPlotMarkCost;

        if (currentValue) {

            Economy economy = RaidCraft.getEconomy();
            if (!economy.hasEnough(AccountType.CITY, bankAccount, markCost)) {
                throw new RaidCraftException("Es ist nicht genug Geld in der Stadtkasse! " + economy.getFormattedAmount(markCost) + " benötigt!");
            }

            // withdraw
            economy.substract(AccountType.CITY, bankAccount, markCost);
            RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                    .broadcastCityMessage(getPlot().getCity(), "Plot Markierung: " + economy.getFormattedAmount(markCost) + ChatColor.GOLD + " abgezogen!");

            //set torches
            Chunk chunk = getPlot().getLocation().getChunk();
            ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot();
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
            Chunk chunk = getPlot().getLocation().getChunk();
            ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot();
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

        if (material == Material.GRASS
                || material == Material.DIRT
                || material == Material.COBBLESTONE
                || material == Material.STONE
                || material == Material.LOG
                || material == Material.SAND
                || material == Material.GRAVEL
                || material == Material.WOOD
                || material == Material.FENCE
                || material == Material.IRON_FENCE
                || material == Material.NETHER_FENCE
                || material == Material.SMOOTH_BRICK
                || material == Material.OBSIDIAN
                || material == Material.DOUBLE_STEP
                || material == Material.WOOL
                || material == Material.BRICK
                || material == Material.QUARTZ_BLOCK
                || material == Material.QUARTZ_ORE
                || material == Material.DIAMOND_ORE
                || material == Material.IRON_ORE
                || material == Material.COAL_ORE
                || material == Material.COAL_BLOCK
                || material == Material.GOLD_ORE
                || material == Material.GOLD_BLOCK
                || material == Material.IRON_BLOCK
                || material == Material.DIAMOND_BLOCK
                || material == Material.EMERALD
                || material == Material.EMERALD_BLOCK
                || material == Material.NETHER_BRICK
                || material == Material.CLAY) {
            return true;
        }
        return false;
    }
}
