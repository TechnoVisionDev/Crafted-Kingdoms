package com.technovision.craftedkingdoms.handlers.farming;

import com.technovision.craftedkingdoms.data.objects.Crop;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Runnable task that grows a crop if growth time has completed.
 *
 * @author TechnoVision
 */
class CropGrowTask implements Runnable {

    @Override
    public void run() {
        Iterator<Map.Entry<Chunk, Map<Location, Crop>>> chunkIterator = FarmingHandler.PLANTED_CROPS.entrySet().iterator();

        while (chunkIterator.hasNext()) {
            Map.Entry<Chunk, Map<Location, Crop>> chunkEntry = chunkIterator.next();
            Chunk chunk = chunkEntry.getKey();

            if (!chunk.isLoaded()) {
                continue;  // Skip this iteration if the chunk is not loaded
            }

            Iterator<Map.Entry<Location, Crop>> locationIterator = chunkEntry.getValue().entrySet().iterator();

            while (locationIterator.hasNext()) {
                Map.Entry<Location, Crop> locationEntry = locationIterator.next();
                Location location = locationEntry.getKey();
                Crop crop = locationEntry.getValue();

                if (!FarmingHandler.isReadyToGrow(crop)) {
                    continue;  // Skip this iteration if the crop is not ready to grow
                }

                FarmingHandler.growCrop(crop);
                Material cropMaterial = Material.valueOf(crop.getMaterial());

                if (cropMaterial == Material.SUGAR_CANE || cropMaterial == Material.CACTUS) {
                    crop.setTimePlanted(new Date());
                }
                else if (cropMaterial == Material.PUMPKIN_STEM || cropMaterial == Material.MELON_STEM) {
                    chunkEntry.getValue().put(location, new Crop(location, cropMaterial));
                }
                else {
                    locationIterator.remove();
                }
            }

            // Remove the chunk from PLANTED_CROPS if there are no more crops in this chunk
            if (chunkEntry.getValue().isEmpty()) {
                chunkIterator.remove();
            }
        }
    }
}
