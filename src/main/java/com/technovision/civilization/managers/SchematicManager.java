package com.technovision.civilization.managers;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.exceptions.CivException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class SchematicManager {

    private static final int CHUNK_SIZE = 16;
    private static final int Y_MAX = 255;
    private static final int Y_MIN = 0;

    private CivilizationPlugin plugin;
    public static Map<Player, EditSession> editSessionMap = new HashMap<>();

    public static File camp;
    public static File cityCenter;

    public SchematicManager() {
        plugin = JavaPlugin.getPlugin(CivilizationPlugin.class);
        camp = getSchematic(plugin, "camp.schem");
        cityCenter = getSchematic(plugin, "city_center.schem");
    }

    /**
     * Places schematic fully all at once.
     */
    public void placeSchematic(File file, Player player, BlockVector3 origin) throws CivException {
        try {
            // Get schematic file (.schem)
            if (!file.exists()) {
                throw new IOException();
            }

            // Paste schematic into world
            BukkitWorld world = new BukkitWorld(player.getWorld());
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {

                Clipboard clipboard = reader.read();
                ClipboardHolder holder = new ClipboardHolder(clipboard);

                // TODO: Rotate schematic on y-axis with respect to center

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                    Operation operation = holder
                            .createPaste(editSession)
                            .to(origin)
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                } catch (WorldEditException e) {
                    throw new CivException("There was an error while trying to place building!");
                }
            }
        } catch (IOException e) {
            throw new CivException("There was an error while trying to load schematic!");
        }
    }

    /**
     * Places schematic fully but with barrier walls around perimeter.
     */
    public void previewSchematic(File file, Player player, BlockVector3 origin) throws CivException {
        try {
            // Get schematic file (.schem)
            if (!file.exists()) {
                throw new IOException();
            }

            // Paste schematic into world
            BukkitWorld world = new BukkitWorld(player.getWorld());
            ClipboardFormat format = ClipboardFormats.findByFile(file);

            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();
                Clipboard glassClipboard = addBarrierWalls(clipboard);

                EditSession editSession = WorldEdit.getInstance().newEditSession(world);
                Operation operation = new ClipboardHolder(glassClipboard)
                            .createPaste(editSession)
                            .to(origin) // Apply offset
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                editSession.close();

                // Store the edit session for undo later
                editSessionMap.put(player, editSession);
            } catch (WorldEditException e) {
                throw new CivException("There was an error while trying to place building!");
            }
        } catch (IOException e) {
            throw new CivException("There was an error while trying to load schematic!");
        }
    }

    int calculateRotation(Player player) {
        // Determine player's yaw
        float yaw = (player.getLocation().getYaw() + 360) % 360;  // Convert to positive angle

        if (yaw >= 315 || yaw < 45) {
            // Facing South, rotate 90 degrees to face North
            return 90;
        } else if (yaw >= 45 && yaw < 135) {
            // Facing West, no rotation needed
            return 0;
        } else if (yaw >= 135 && yaw < 225) {
            // Facing North, rotate 270 degrees to face South
            return 270;
        } else {
            // Facing East, rotate 180 degrees to face West
            return 180;
        }
    }

    BlockVector3 calculateOriginFor2By2(Player player) {
        // Determine chunk coordinates in front of player
        float yaw = (player.getLocation().getYaw() + 360) % 360;  // Convert to positive angle
        int chunkX = player.getLocation().getChunk().getX();
        int chunkZ = player.getLocation().getChunk().getZ();

        if (yaw >= 315 || yaw < 45) {
            // South
            chunkZ++;
        } else if (yaw >= 45 && yaw < 135) {
            // West
            chunkX--;
            chunkX--;
        } else if (yaw >= 135 && yaw < 225) {
            // North
            chunkZ--;
            chunkZ--;
        } else {
            // East
            chunkX++;
        }
        // Calculate the origin for pasting the schematic at the start of the next chunk
        return BlockVector3.at(
                chunkX * CHUNK_SIZE,
                player.getLocation().getY(),
                chunkZ * CHUNK_SIZE);
    }


    public void undoSchematicPaste(Player player) {
        EditSession editSession = editSessionMap.get(player);
        if (editSession != null) {
            try (EditSession newEditSession = WorldEdit.getInstance().newEditSession(editSession.getWorld())) {
                editSession.undo(newEditSession);
                removeEditSession(player);
            }
        }
    }

    public void removeEditSession(Player player) {
        EditSession session = editSessionMap.get(player);
        if (session != null) {
            session.close();
            editSessionMap.remove(player);
        }
    }

    /**
     * Places schematic block by block (one block per 0.5 seconds).
     */
    public void placeSchematicAsync(File file, Player player, BlockVector3 origin) throws CivException {
        try {
            // Get schematic file (.schem)
            if (!file.exists()) {
                throw new IOException();
            }

            // Paste schematic into world
            BukkitWorld world = new BukkitWorld(player.getWorld());
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();
                BlockVector3 minPoint = clipboard.getRegion().getMinimumPoint();

                List<BlockVector3> points = new ArrayList<>();
                for (BlockVector3 point : clipboard.getRegion()) {
                    points.add(point.subtract(minPoint));
                }

                AtomicInteger index = new AtomicInteger(0);
                AtomicInteger taskID = new AtomicInteger();
                taskID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    if (index.get() >= points.size()) {
                        Bukkit.getScheduler().cancelTask(taskID.get());
                        return;
                    }
                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                        BlockVector3 point = points.get(index.get());
                        BaseBlock block = clipboard.getFullBlock(point.add(minPoint));
                        // Skip air blocks if not necessary
                        while (block.getBlockType().getMaterial().isAir()) {
                            if (!editSession.getBlock(origin.add(point)).getBlockType().getMaterial().isAir()) {
                                break;
                            }
                            if (index.get() == points.size()-1) break;
                            index.incrementAndGet();
                            point = points.get(index.get());
                            block = clipboard.getFullBlock(point.add(minPoint));
                        }
                        // Set block in world and increment index
                        editSession.setBlock(origin.add(point), block);
                        index.incrementAndGet();
                    } catch (WorldEditException e) {
                        plugin.getLogger().log(Level.SEVERE, "There was an error while trying to place building block by block!", e);
                        Bukkit.getScheduler().cancelTask(taskID.get());
                    }
                }, 10, 10));
            } catch (IOException e) {
                throw new CivException("There was an error while trying to load schematic!");
            }
        } catch (IOException e) {
            throw new CivException("Schematic file does not exist!");
        }
    }

    private Clipboard addBarrierWalls(Clipboard clipboard) {
        // Get the dimensions of the clipboard
        Region region = clipboard.getRegion();
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        BlockVector3 origin = clipboard.getOrigin();

        // Create a new clipboard
        BlockArrayClipboard newClipboard = new BlockArrayClipboard(region);

        // Copy blocks from the original clipboard to the new one
        region.forEach((block) -> {
            BlockState blockState = clipboard.getBlock(block);
            try {
                newClipboard.setBlock(block, blockState);
            } catch (WorldEditException ignored) {
                System.out.println("ERROR 1");
            }
        });
        // Iterate through each layer (X-Z plane)
        for (int y = max.getY(); y >= origin.getY(); y--) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    // Set the block to glass if it is on the perimeter
                    if (x == min.getX() || x == max.getX() || z == min.getZ() || z == max.getZ() || y == max.getY()) {
                        try {
                            newClipboard.setBlock(BlockVector3.at(x, y, z), BlockTypes.GLASS.getDefaultState());
                        } catch (WorldEditException ignored) {
                            System.out.println("ERROR 2");
                        }
                    }
                }
            }
        }
        return newClipboard;
    }

    private File getSchematic(JavaPlugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder()+"/schematics/", fileName);
        if (!file.exists()) {
            plugin.saveResource("schematics/"+fileName, false);
            return new File(plugin.getDataFolder()+"/schematics/", fileName);
        }
        return file;
    }
}
