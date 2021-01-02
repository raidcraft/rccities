package net.silthus.rccities.manager;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.silthus.rccities.RCCities;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Philip Urban
 *
 * Based on: https://matthewmiller.dev/blog/how-to-load-and-save-schematics-with-the-worldedit-api/
 */
public class SchematicManager {

    private final static String SCHEMATIC_PREFIX = "plot_";

    private final RCCities plugin;

    public SchematicManager(RCCities plugin) {

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
            Vector pos2 = new Vector(plot.getLocation().getChunk().getX() * 16 + 15,
                    plot.getLocation().getWorld().getMaxHeight(), plot.getLocation().getChunk().getZ() * 16 + 15);

            BlockVector3 min = BlockVector3.at(Math.min(pos1.getX(), pos2.getX()),
                    Math.min(pos1.getY(), pos2.getY()),
                    Math.min(pos1.getZ(), pos2.getZ()));
            BlockVector3 max = BlockVector3.at(Math.max(pos1.getX(), pos2.getX()),
                    Math.max(pos1.getY(), pos2.getY()),
                    Math.max(pos1.getZ(), pos2.getZ()));

            com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(plot.getLocation().getWorld());

            CuboidRegion region = new CuboidRegion(worldEditWorld, min, max);
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);

            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            forwardExtentCopy.setCopyingEntities(true);
            Operations.complete(forwardExtentCopy);

            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard);
            }

        } catch (WorldEditException | IOException e) {
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
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();

                com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(plot.getLocation().getWorld());
                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldEditWorld, -1)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(clipboard.getOrigin())
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                }
            }
        } catch(IOException e) {
            throw new RaidCraftException("Fehler beim laden der Schematic!");
        } catch(WorldEditException e) {
            throw new RaidCraftException("Fehler beim pasten der Schematic!");
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
