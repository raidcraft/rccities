package net.silthus.rccities.flags.plot;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
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
        double markCost = RCCitiesPlugin.getPlugin().getPluginConfig().getFlagPlotMarkCost();

        if (currentValue) {

            Economy economy = RCCitiesPlugin.getPlugin().getEconomy();
            if (economy.bankHas(bankAccount, markCost).type == EconomyResponse.ResponseType.SUCCESS) {
                throw new RaidCraftException("Es ist nicht genug Geld in der Stadtkasse! " + economy.format(markCost) + " benötigt!");
            }

            // withdraw
            economy.bankWithdraw(bankAccount, markCost);
            RCCitiesPlugin.getPlugin().getResidentManager()
                    .broadcastCityMessage(getPlot().getCity(), "Plot Markierung: " + economy.format(markCost) + ChatColor.GOLD + " abgezogen!");

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

        // TODO: Extend list with new materials
        if (material == Material.GRASS
                || material == Material.DIRT
                || material == Material.COBBLESTONE
                || material == Material.STONE
                || material == Material.SAND
                || material == Material.GRAVEL
                || material == Material.OBSIDIAN
                || material == Material.WHITE_WOOL
                || material == Material.BRICK
                || material == Material.QUARTZ_BLOCK
                || material == Material.QUARTZ
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
