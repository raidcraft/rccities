package net.silthus.rccities.manager;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.plot.Plot;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

/**
 * @author Philip Urban
 */
public class SchematicManager {

    private final static String SCHEMATIC_PREFIX = "plot_";

    private RCCitiesPlugin plugin;

    public SchematicManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    public void createSchematic(Plot plot) throws RaidCraftException {

        try {
            String schematicName = getSchematicName(plot);
            File file = new File(getSchematicDir(plot.getLocation().getWorld()), schematicName);
            if (file.exists()) {
                return;
            }

            Vector pos1 = new Vector(plot.getLocation().getChunk().getX() * 16, 0, plot.getLocation().getChunk().getZ() * 16);
            Vector pos2 = new Vector(plot.getLocation().getChunk().getX() * 16 + 15, plot.getLocation().getWorld().getMaxHeight(), plot.getLocation().getChunk().getZ() * 16 + 15);

            Vector min = new Vector(Math.min(pos1.getX(), pos2.getX()),
                    Math.min(pos1.getY(), pos2.getY()),
                    Math.min(pos1.getZ(), pos2.getZ()));
            Vector max = new Vector(Math.max(pos1.getX(), pos2.getX()),
                    Math.max(pos1.getY(), pos2.getY()),
                    Math.max(pos1.getZ(), pos2.getZ()));

            // create clipboard
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            BukkitWorld bukkitWorld = new BukkitWorld(plot.getLocation().getWorld());
            // store blocks
            clipboard.copy(new EditSession(bukkitWorld, Integer.MAX_VALUE));
            // store entities
            //            for (LocalEntity entity : bukkitWorld.getEntities(new CuboidRegion(min, max))) {
            //                clipboard.storeEntity(entity);
            //            }
            // save schematic
            MCEditSchematicFormat.MCEDIT.save(clipboard, file);
        } catch (IOException | DataException e) {
            throw new RaidCraftException("Fehler beim speichern der Schematic!");
        }
    }

    public void restoreCity(City city) throws RaidCraftException {

        for (Plot plot : plugin.getPlotManager().getPlots(city)) {

            restorePlot(plot);
        }
    }

    public void restorePlot(Plot plot) throws RaidCraftException {

        String schematicName = getSchematicName(plot);
        File file = new File(getSchematicDir(plot.getLocation().getWorld()), schematicName);
        try {
            CuboidClipboard clipboard = MCEditSchematicFormat.MCEDIT.load(file);
            clipboard.paste(new EditSession(new BukkitWorld(plot.getLocation().getWorld()), Integer.MAX_VALUE), clipboard.getOrigin(), false);
            //            clipboard.pasteEntities(clipboard.getOrigin());
        } catch (IOException | DataException e) {
            throw new RaidCraftException("Fehler beim laden der Schematic!");
        } catch (MaxChangedBlocksException e) {
            throw new RaidCraftException("Fehler beim pasten der Schematic! (Zu viele Blöcke)");
        }
    }

    public void deleteSchematic(Plot plot) throws RaidCraftException {

        String schematicName = getSchematicName(plot);
        File file = new File(getSchematicDir(plot.getLocation().getWorld()), schematicName);

        if (!file.delete()) {
            throw new RaidCraftException("Can't remove schematic file " + file.getAbsolutePath());
        }
    }

    public String getSchematicName(Plot plot) {

        return SCHEMATIC_PREFIX + "x" + plot.getLocation().getBlockX() + "_z" + plot.getLocation().getBlockZ() + ".schematic";
    }

    public File getSchematicDir(World world) throws RaidCraftException {

        File dir;
        dir = new File(plugin.getDataFolder(), "schematics");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RaidCraftException("Der Schematics Ordner konnte nicht erstellt werden!");
            }
        }
        dir = new File(dir, world.getName());
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RaidCraftException("Der Schematics Ordner konnte nicht erstellt werden!");
            }
        }
        return dir;
    }
}
