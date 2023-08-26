package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.enums.BiomeData;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.Crop;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.handlers.farming.FarmingHandler;
import com.technovision.craftedkingdoms.util.BlockUtils;
import com.technovision.craftedkingdoms.util.EffectUtils;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Handles events for fortifying blocks.
 *
 * @author TechnoVision
 */
public class FortifyHandler implements Listener {

    /**
     * Auto fortify placed blocks if material is in offhand slot.
     * @param event fires when a player places a block with main hand.
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        Material itemType = offHandItem.getType();
        if (!isValidMaterial(itemType, player.getWorld())) return;

        Resident res = CKGlobal.getResident(player);
        Group group = CKGlobal.getFortifyGroup(player);
        if (!isValidFortifyGroup(player, res, group)) return;
        if (!canPlayerFortify(player, res, group)) return;
        if (!FortifiedBlock.isReinforceable(event.getBlockPlaced())) {
            MessageUtils.sendError(player, "That block cannot be reinforced!");
            return;
        }

        fortifyBlock(res, player, group, offHandItem, event.getBlockPlaced());
    }

    /**
     * Fortify a block by right-clicking with item in main hand.
     * @param event fires when a player interacts.
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Spawn nametag with block reinforcement data if necessary
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Check if player has inspect mode on
            Player player = event.getPlayer();
            Resident res = CKGlobal.getResident(player);

            // Update nametag if needed
            if (res.isInspectMode()) {
                BlockFace face = getPlayerFacing(event.getPlayer());
                Location adjustedLocation = adjustLocationForFace(event.getClickedBlock().getLocation(), face);
                FortifiedBlock fb = CKGlobal.getFortifiedBlock(event.getClickedBlock().getLocation());
                if (fb != null) {
                    updateNametag(adjustedLocation, fb);
                }
            }
        }

        // Handle block reinforcement
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        Resident res = CKGlobal.getResident(player);
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        Material itemType = mainHandItem.getType();
        if (!isValidMaterial(itemType, player.getWorld())) return;

        Group group = CKGlobal.getFortifyGroup(player);
        if (!isValidFortifyGroup(player, res, group)) return;
        if (!canPlayerFortify(player, res, group)) return;
        if (!FortifiedBlock.isReinforceable(event.getClickedBlock())) {
            MessageUtils.sendError(player, "That block cannot be reinforced!");
            return;
        }

        event.setCancelled(true);
        handleBlockInteraction(player, group, mainHandItem, event.getClickedBlock());
    }

    public boolean isValidMaterial(Material type, World world) {
        if (world == Bukkit.getWorld("world")) {
            if (FortifiedBlock.OVERWORLD_MATERIALS.containsKey(type)){
                return true;
            }
        } else if (world == Bukkit.getWorld("world_nether")) {
            if (FortifiedBlock.NETHER_MATERIALS.containsKey(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a block is fortified when broken by a player.
     * @param event fires when a player breaks a block.
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        // Get bottom half if block is a door
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (BlockUtils.isDoor(block)) {
            block = BlockUtils.getBottomPartOfDoor(block);
        }

        // Check if block is fortified
        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(block.getLocation());
        if (fortifiedBlock == null) {
            // Remove crops if necessary
            if (BiomeData.isCrop(block.getType())) {
                FarmingHandler.removeCrop(block.getLocation());
            }
            Location aboveCrop = block.getLocation().clone().add(0, 1, 0);
            Crop crop = FarmingHandler.getCrop(aboveCrop);
            if (crop != null) FarmingHandler.removeCrop(aboveCrop);
            // Check if snitch is nearby
            SnitchHandler.handleBlockBreak(event);
            return;
        }

        // Check if fortified block is from your group
        Resident res = CKGlobal.getResident(event.getPlayer());
        if (res.getGroups().contains(fortifiedBlock.getGroup())) {
            if (res.hasPermission(fortifiedBlock.getGroup(), Permissions.BLOCKS)) {
                removeNametag(block.getLocation());
                fortifiedBlock.reimburseCost(player);
                fortifiedBlock.delete();
                // Remove crops if necessary
                Location aboveCrop = block.getLocation().clone().add(0, 1, 0);
                Crop crop = FarmingHandler.getCrop(aboveCrop);
                if (crop != null) FarmingHandler.removeCrop(aboveCrop);
                return;
            }
            if (BiomeData.isCrop(block.getType())) {
                if (res.hasPermission(fortifiedBlock.getGroup(), Permissions.CROPS)) {
                    removeNametag(block.getLocation());
                    fortifiedBlock.reimburseCost(player);
                    fortifiedBlock.delete();
                    // Remove crops if necessary
                    Location aboveCrop = block.getLocation().clone().add(0, 1, 0);
                    Crop crop = FarmingHandler.getCrop(aboveCrop);
                    if (crop != null) FarmingHandler.removeCrop(aboveCrop);
                    return;
                }
            }
        }

        // Check if resident is sharded
        if (res.getSoulShard() != null) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getPlayer(), "You cannot break reinforced blocks while sharded!");
            return;
        }

        // Remove one reinforcement from block
        fortifiedBlock.decrement();
        if (fortifiedBlock.getReinforcements() <= 0) {
            if (BiomeData.isCrop(block.getType())) {
                FarmingHandler.removeCrop(block.getLocation());
            }
            Location aboveCrop = block.getLocation().clone().add(0, 1, 0);
            Crop crop = FarmingHandler.getCrop(aboveCrop);
            if (crop != null) FarmingHandler.removeCrop(aboveCrop);
            removeNametag(block.getLocation());
            // Check if snitch is nearby
            SnitchHandler.handleBlockBreak(event);
            return;
        }
        event.setCancelled(true);

        // Update block nametag
        BlockFace face = getPlayerFacing(event.getPlayer());
        Location adjustedLocation = adjustLocationForFace(block.getLocation(), face);
        FortifiedBlock fb = CKGlobal.getFortifiedBlock(block.getLocation());
        if (fb != null) {
            updateNametag(adjustedLocation, fb);
        }
    }

    /**
     * Prevents player from opening reinforced container or chest without necessary perms.
     * @param event Fires when a player opens a chest or container.
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onContainerOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (event.getInventory().getType() == InventoryType.PLAYER) return;

        // Modify location for double chests
        Location loc = event.getInventory().getLocation();
        if (loc == null) return;
        Location chestLocation = loc.getBlock().getLocation().clone();

        // Check if block is fortified
        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(chestLocation);
        if (fortifiedBlock == null) {
            // Check if snitch is nearby
            SnitchHandler.handleChestOpen(event, player);
            return;
        }
        String groupName = fortifiedBlock.getGroup();

        Resident res = CKGlobal.getResident(player);
        if (res.getGroups().contains(groupName)) {
            // Check if player has perms to open chest
            InventoryType type = event.getInventory().getType();
            if (type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST) {
                if (res.hasPermission(groupName, Permissions.CHESTS)) { return; }
                event.setCancelled(true);
                MessageUtils.send(event.getPlayer(), getPermsNeededString(Permissions.CHESTS, groupName));
                return;
            }
            // Check if player has perms to open containers
            if (res.hasPermission(groupName, Permissions.CONTAINERS)) { return; }
            event.setCancelled(true);
            MessageUtils.send(event.getPlayer(), getPermsNeededString(Permissions.CONTAINERS, groupName));
            return;
        }
        event.setCancelled(true);
        MessageUtils.send(player, String.format("%sThat container is fortified with %s%s%s by %s%s%s.",
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(Material.valueOf(fortifiedBlock.getMaterial()), true),
                ChatColor.GRAY, ChatColor.YELLOW,
                fortifiedBlock.getGroup(), ChatColor.GRAY
        ));
    }

    /**
     * Prevents player from opening doors without necessary perms.
     * @param event Fires when a player opens a door, trapdoor, plate, or button.
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onSpecialBlocksInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Material type = clickedBlock.getType();
        boolean isSpecialBlock = type.name().endsWith("_DOOR") || type.name().endsWith("_TRAPDOOR") || type.name().endsWith("_BUTTON") || type.name().endsWith("_PLATE") || type.name().endsWith("_BED");
        if (!isSpecialBlock) return;

        // Get bottom half if block is a door
        if (BlockUtils.isDoor(clickedBlock)) {
            clickedBlock = BlockUtils.getBottomPartOfDoor(clickedBlock);
        }

        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(clickedBlock.getLocation());
        if (fortifiedBlock == null) return;

        String groupName = fortifiedBlock.getGroup();
        Resident res = CKGlobal.getResident(event.getPlayer());
        if (!res.getGroups().contains(groupName)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(String.format("%sThat block is fortified with %s%s%s by %s%s%s.",
                    ChatColor.GRAY, ChatColor.YELLOW,
                    StringUtils.stringifyType(Material.valueOf(fortifiedBlock.getMaterial()), true),
                    ChatColor.GRAY, ChatColor.YELLOW,
                    groupName, ChatColor.GRAY));
            return;
        }

        Permissions requiredPermission;
        if (type.name().endsWith("_BED")) {
            requiredPermission = Permissions.BEDS;
        } else {
            requiredPermission = Permissions.DOORS;
        }
        if (res.hasPermission(groupName, requiredPermission) || res.hasPermission(groupName, Permissions.BLOCKS)) return;

        event.setCancelled(true);
        MessageUtils.send(event.getPlayer(), getPermsNeededString(requiredPermission, groupName));
    }

    /**
     * Prevents player from sleeping in a bed without necessary perms.
     * @param event Fires when a player attempts to sleep in a bed.
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(event.getBed().getLocation());
        if (fortifiedBlock == null) return;
        String groupName = fortifiedBlock.getGroup();

        // Check if player has perms to sleep in bed
        Resident res = CKGlobal.getResident(event.getPlayer());
        if (res.getGroups().contains(groupName)) {
            if (res.hasPermission(groupName, Permissions.BEDS) || res.hasPermission(fortifiedBlock.getGroup(), Permissions.BLOCKS)) {
                return;
            }
            event.setCancelled(true);
            MessageUtils.send(event.getPlayer(), getPermsNeededString(Permissions.BEDS, groupName));
            return;
        }
        MessageUtils.send(event.getPlayer(), String.format("%sThat bed is fortified with %s%s%s by %s%s%s.",
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(Material.valueOf(fortifiedBlock.getMaterial()), true),
                ChatColor.GRAY, ChatColor.YELLOW,
                groupName, ChatColor.GRAY
        ));
    }

    /**
     * Prevent TNT for going off if a fortified block is within the blast radius.
     * @param event Fires when a TNT or creeper explosion goes off.
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(block.getLocation());
            if (fortifiedBlock != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Prevent liquid flow from damaging fortified crops.
     * @param event Fires when liquid flows.
     */
    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        // Prevent all lava flow
        Material flowingMaterial = event.getBlock().getType();
        if (flowingMaterial == Material.LAVA) {
            event.setCancelled(true);
            return;
        }
        // Prevent water flow on crops
        Location toLocation = event.getToBlock().getLocation();
        if (FarmingHandler.getCrop(toLocation) != null || CKGlobal.getFortifiedBlock(toLocation) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        // Check if block below is fortified
        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(event.getBlockClicked().getLocation());
        if (fortifiedBlock == null) return;
        String groupName = fortifiedBlock.getGroup();

        // Check if player is from fortified block's group
        Player player = event.getPlayer();
        Resident res = CKGlobal.getResident(player);
        if (res.getGroups().contains(groupName)) {
            if (!res.hasPermission(groupName, Permissions.BLOCKS)) {
                event.setCancelled(true);
                MessageUtils.send(event.getPlayer(), getPermsNeededString(Permissions.BLOCKS, groupName));
            }
            return;
        }
        event.setCancelled(true);
        MessageUtils.sendError(player, "You can't place liquids on another group's fortified blocks!");
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        BlockIgniteEvent.IgniteCause cause = event.getCause();
        if (cause == BlockIgniteEvent.IgniteCause.LIGHTNING || cause == BlockIgniteEvent.IgniteCause.LAVA) {
            event.setCancelled(true);
            return;
        }

        Block block = getAdjacentSolidBlock(event.getBlock(), event.getPlayer());
        if (block.getType() == Material.NETHERRACK) return;

        // Check if the block is fortified
        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(block.getLocation());
        if (fortifiedBlock != null) {
            Player player = event.getPlayer();

            if (cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL || cause == BlockIgniteEvent.IgniteCause.FIREBALL) {
                if (player != null) {
                    MessageUtils.sendError(player, "You cannot set fortified blocks on fire!");
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) {
            Block targetBlock = event.getBlock();

            // Check blocks around the target block
            for (BlockFace face : BlockFace.values()) {
                Block adjacentBlock = targetBlock.getRelative(face);

                if (CKGlobal.getFortifiedBlock(adjacentBlock.getLocation()) != null) {
                    event.setCancelled(true);
                    return;  // No need to check further once we know we should cancel the event
                }
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (CKGlobal.getFortifiedBlock(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    private Block getAdjacentSolidBlock(Block airBlock, Player player) {
        if (player == null) {
            return getPriorityAdjacentSolidBlock(airBlock);
        }

        Vector direction = player.getLocation().getDirection();
        BlockFace targetedFace = getTargetedBlockFace(direction);
        Block adjacent = airBlock.getRelative(targetedFace.getOppositeFace());

        if (!adjacent.getType().isAir()) {
            return adjacent;
        } else {
            return getPriorityAdjacentSolidBlock(airBlock);
        }
    }

    private BlockFace getTargetedBlockFace(Vector direction) {
        double angle = -1;
        BlockFace face = null;
        for (BlockFace blockFace : BlockFace.values()) {
            double currentAngle = direction.angle(blockFace.getDirection());
            if (angle == -1 || currentAngle < angle) {
                angle = currentAngle;
                face = blockFace;
            }
        }
        return face;
    }

    private Block getPriorityAdjacentSolidBlock(Block airBlock) {
        BlockFace[] priorityFaces = {BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN};

        for (BlockFace face : priorityFaces) {
            Block adjacent = airBlock.getRelative(face);
            if (!adjacent.getType().isAir()) {
                return adjacent;
            }
        }
        return null;
    }

    private String getPermsNeededString(Permissions perm, String groupName) {
        return String.format("%sYou need the %s%s%s permission in %s%s%s to use that!",
                ChatColor.GRAY, ChatColor.YELLOW,
                perm,
                ChatColor.GRAY, ChatColor.YELLOW,
                groupName,
                ChatColor.GRAY
        );
    }

    public BlockFace getPlayerFacing(Player player) {
        float pitch = player.getLocation().getPitch();
        double yaw = (player.getLocation().getYaw() + 360) % 360;

        if (pitch < -50.0) {
            return BlockFace.DOWN;
        } else if (pitch > 50.0) {
            return BlockFace.UP;
        }
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.NORTH;
        } else if (yaw < 135) {
            return BlockFace.EAST;
        } else if (yaw < 225) {
            return BlockFace.SOUTH;
        } else {
            return BlockFace.WEST;
        }
    }

    private Location adjustLocationForFace(Location original, BlockFace face) {
        Location adjusted = original.clone();
        switch (face) {
            case NORTH -> adjusted.add(0, 0.5, -0.6);
            case SOUTH -> adjusted.add(0, 0.5, 0.6);
            case EAST -> adjusted.add(0.6, 0.5, 0);
            case WEST -> adjusted.add(-0.6, 0.5, 0);
            case UP -> adjusted.add(0, 1.1, 0);
            case DOWN -> adjusted.add(0, -0.2, 0);
        }
        return adjusted;
    }

    private void updateNametag(Location location, FortifiedBlock fortifiedBlock) {
        // Adjust location for nametag to be inside the block
        location = location.clone().add(0.5, -0.3, 0.5);  // Centers and moves down
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(fortifiedBlock.getBlockCoord().asLocation().add(0.5, 0.5, 0.5), 1, 1.1, 1);
        ArmorStand armorStand = null;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof ArmorStand && entity.hasMetadata("fortifyTag")) {
                armorStand = (ArmorStand) entity;
                break;
            }
        }
        if (armorStand == null) {
            armorStand = location.getWorld().spawn(location, ArmorStand.class, as -> {
                as.setGravity(false);
                as.setSmall(true);
                as.setVisible(false);
                as.setInvulnerable(true);
                as.setBasePlate(false);
                as.setVisualFire(true);
                as.setMarker(true);
            });
            armorStand.setMetadata("fortifyTag", new FixedMetadataValue(CraftedKingdoms.plugin, true));
            // Schedule removal after 5 seconds
            Bukkit.getScheduler().scheduleSyncDelayedTask(CraftedKingdoms.plugin, armorStand::remove, 100L);
        }

        double reinforcements = fortifiedBlock.getReinforcements();
        double maxReinforcements = fortifiedBlock.getMaxReinforcements();
        double percentageValue = (reinforcements / maxReinforcements) * 100;
        String percentage = String.format("%d%%", (int) Math.ceil(percentageValue));

        armorStand.setCustomName(ChatColor.GREEN + percentage + " (" + (int)reinforcements + "/" + (int)maxReinforcements + ")");
        armorStand.setCustomNameVisible(true);
    }

    private void removeNametag(Location location) {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location.add(0.5, 0.5, 0.5), 1, 1.1, 1);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ArmorStand && entity.hasMetadata("fortifyTag")) {
                entity.remove();
            }
        }
    }

    private boolean isValidFortifyGroup(Player player, Resident res, Group group) {
        if (group == null) return false;
        if (!res.isInGroup(group.getName())) {
            MessageUtils.sendError(player, "You are no longer a member of the group you are fortifying for.");
            return false;
        }
        return true;
    }

    private boolean canPlayerFortify(Player player, Resident res, Group group) {
        if (!res.hasPermission(group, Permissions.BLOCKS)) {
            MessageUtils.sendError(player, "You need the " + ChatColor.YELLOW + "BLOCKS" + ChatColor.RED + " permission to fortify blocks.");
            return false;
        }
        return true;
    }

    private void handleBlockInteraction(Player player, Group group, ItemStack item, Block block) {
        // Get bottom half if block is a door
        if (BlockUtils.isDoor(block)) {
            block = BlockUtils.getBottomPartOfDoor(block);
        }
        // Check if block is already fortified
        FortifiedBlock alreadyFortifiedBlock = CKGlobal.getFortifiedBlock(block.getLocation());
        Material itemType = item.getType();
        if (alreadyFortifiedBlock != null) {

            if (!alreadyFortifiedBlock.getGroup().equals(group.getName())) {
                MessageUtils.sendError(player, "That block has already been reinforced by " + ChatColor.YELLOW + alreadyFortifiedBlock.getGroup());
                return;
            }
            int maxReinforcements = alreadyFortifiedBlock.getMaxReinforcements();
            int reinforcementsFromHand = FortifiedBlock.getReinforcements(itemType, player.getWorld());
            String stringAlreadyFortified = StringUtils.stringifyType(Material.valueOf(alreadyFortifiedBlock.getMaterial()), true);

            if (reinforcementsFromHand > maxReinforcements) {
                // Update block to better material
                String msg = String.format("%sUpgraded this block from %s%s%s to %s%s%s reinforcement",
                        ChatColor.GRAY, ChatColor.YELLOW,
                        stringAlreadyFortified,
                        ChatColor.GRAY, ChatColor.YELLOW,
                        StringUtils.stringifyType(itemType, true),
                        ChatColor.GRAY);
                item.setAmount(item.getAmount() - 1);
                alreadyFortifiedBlock.upgradeMaterial(itemType);
                EffectUtils.spawnEnderParticles(block);
                MessageUtils.send(player, msg);
            }
            else if (reinforcementsFromHand == maxReinforcements) {
                if (reinforcementsFromHand > alreadyFortifiedBlock.getReinforcements()) {
                    // Refill reinforcements to max of current material type
                    item.setAmount(item.getAmount() - 1);
                    alreadyFortifiedBlock.refillReinforcements();
                    EffectUtils.spawnEnderParticles(block);
                    MessageUtils.send(player, ChatColor.GRAY + "You have refilled this block's reinforcements to its max.");
                } else {
                    // Block is already at max reinforcements for that type
                    MessageUtils.sendError(player, "That block is already reinforced with " + ChatColor.YELLOW + stringAlreadyFortified);
                }
            }
            else {
                // Block is reinforced with a better material
                MessageUtils.sendError(player, "That block is already reinforced with " + ChatColor.YELLOW + stringAlreadyFortified);
            }
        } else {
            Resident res = CKGlobal.getResident(player);
            fortifyBlock(res, player, group, item, block);
        }
    }

    public static void fortifyCrop(FortifiedBlock fortifiedBlock, Location cropLocation) {
        Material material = Material.valueOf(fortifiedBlock.getMaterial());
        Group group = CKGlobal.getGroup(fortifiedBlock.getGroup());
        if (group == null) return;
        group.fortifyBlock(cropLocation, material);
    }

    public void fortifyBlock(Resident res, Player player, Group group, ItemStack item, Block block) {
        // Get bottom half if block is a door
        if (BlockUtils.isDoor(block)) {
            block = BlockUtils.getBottomPartOfDoor(block);
        }

        Material itemType = item.getType();
        if (itemType == Material.BEDROCK) {
            if (!player.isOp()) {
                item.setAmount(0);
                return;
            }
        }
        item.setAmount(item.getAmount() - 1);
        group.fortifyBlock(block, itemType);

        // Add snitch if block is noteblock or jukebox
        if (block.getType() == Material.JUKEBOX || block.getType() == Material.NOTE_BLOCK) {
            group.addSnitch(block);
        }

        if (res.isInspectMode()) {
            MessageUtils.send(player, fortifyMessage(block, itemType));
        }
        EffectUtils.spawnEnderParticles(block);
    }

    private String fortifyMessage(Block block, Material material) {
        return String.format("%sYou fortified a %s%s%s at %s(%d, %d, %d)%s with a %s%s%s.",
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(block.getType()),
                ChatColor.GRAY, ChatColor.YELLOW,
                block.getX(), block.getY(), block.getZ(),
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(material),
                ChatColor.GRAY
        );
    }
}
