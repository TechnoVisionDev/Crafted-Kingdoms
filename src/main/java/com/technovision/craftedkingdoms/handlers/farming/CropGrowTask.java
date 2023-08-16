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

            Chunk chunk = location.getChunk();
            if (!FarmingHandler.isReadyToGrow(crop) || !chunk.isLoaded()) { return; }

            FarmingHandler.growCrop(crop);
            Material cropMaterial = Material.valueOf(crop.getMaterial());
            if (cropMaterial == Material.SUGAR_CANE || cropMaterial == Material.CACTUS) {
                crop.setTimePlanted(new Date());
                FarmingHandler.PLANTED_CROPS.put(location, crop);
            }
            else if (cropMaterial == Material.PUMPKIN_STEM) {
                crop = new Crop(location, Material.PUMPKIN_STEM);
                FarmingHandler.PLANTED_CROPS.put(location, crop);
            }
            else if (cropMaterial == Material.MELON_STEM) {
                crop = new Crop(location, Material.MELON_STEM);
                FarmingHandler.PLANTED_CROPS.put(location, crop);
            }
            else {
                iterator.remove();
            }
        }
    }
}
