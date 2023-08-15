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
        Iterator<Map.Entry<Location, Crop>> iterator = FarmingHandler.PLANTED_CROPS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, Crop> entry = iterator.next();
            Location location = entry.getKey();
            Crop crop = entry.getValue();

            if (FarmingHandler.isReadyToGrow(crop)) {
                Chunk chunk = location.getChunk();
                if (chunk.isLoaded()) {
                    FarmingHandler.growCrop(crop);
                    Material cropMaterial = Material.valueOf(crop.getMaterial());
                    if (cropMaterial == Material.SUGAR_CANE || cropMaterial == Material.CACTUS) {
                        crop.setTimePlanted(new Date());
                        FarmingHandler.PLANTED_CROPS.put(crop.getBlockCoord().asLocation(), crop);
                    } else {
                        iterator.remove();
                    }
                } else {
                    FarmingHandler.PERSISTENT_CROPS.put(location, crop);
                }
            }
        }
    }
}
