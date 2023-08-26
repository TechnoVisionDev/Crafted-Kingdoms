package com.technovision.craftedkingdoms.handlers.factory;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Handles the print press factory.
 *
 * @author TechnoVision
 */
public class PrintingPressHandler implements Listener {

    private static final long COOLDOWN_IN_MILLIS = 500;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public static final String GUI_TITLE = "Printing Press";
    private final Map<Integer, FactoryRecipe> slotToRecipe = new HashMap<>();

    public PrintingPressHandler() {
        // Tier 1 Recipes
        slotToRecipe.put(3, new FactoryRecipe("Bind Books", new ItemStack(Material.BOOK, 12),List.of(
                new ItemStack(Material.PAPER, 18),
                new ItemStack(Material.LEATHER, 6),
                new ItemStack(Material.CHARCOAL, 4)
        )));
        slotToRecipe.put(5, new FactoryRecipe("Bind Writable Books", new ItemStack(Material.WRITABLE_BOOK, 12),List.of(
                new ItemStack(Material.PAPER, 18),
                new ItemStack(Material.LEATHER, 6),
                new ItemStack(Material.INK_SAC, 6),
                new ItemStack(Material.CHARCOAL, 4)
        )));
        slotToRecipe.put(11, new FactoryRecipe("Print Book", new ItemStack(Material.ENCHANTED_BOOK, 6),List.of(
                new ItemStack(Material.PAPER, 18),
                new ItemStack(Material.INK_SAC, 1),
                new ItemStack(Material.WRITTEN_BOOK, 1),
                new ItemStack(Material.CHARCOAL, 4)
        )));
        slotToRecipe.put(13, new FactoryRecipe("Print Note", new ItemStack(Material.PAPER, 3),List.of(
                new ItemStack(Material.PAPER, 5),
                new ItemStack(Material.WRITTEN_BOOK, 1),
                new ItemStack(Material.CHARCOAL, 4)
        )));
        slotToRecipe.put(15, new FactoryRecipe("Print Secure Note", new ItemStack(Material.PAPER, 3),List.of(
                new ItemStack(Material.PAPER, 5),
                new ItemStack(Material.WRITTEN_BOOK, 1),
                new ItemStack(Material.CHARCOAL, 4)
        )));
    }

    /**
     * Handles cauldron XP factory
     * @param event Fires when player right clicks cauldron with stick
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Check for cooldown
        if(cooldowns.containsKey(playerId) && (System.currentTimeMillis() - cooldowns.get(playerId)) < COOLDOWN_IN_MILLIS) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.STICK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (clickedBlock.getType() != Material.CARTOGRAPHY_TABLE) return;

        FortifiedBlock cartography = CKGlobal.getFortifiedBlock(clickedBlock.getLocation());
        if (cartography == null || CKGlobal.getGroup(cartography.getGroup()) == null) {
            MessageUtils.sendError(player, "That block must be reinforced to access factory recipes!");
            cooldowns.put(playerId, System.currentTimeMillis());  // Set cooldown
            return;
        }

        Resident resident = CKGlobal.getResident(player);
        if (!resident.isInGroup(cartography.getGroup())) {
            MessageUtils.sendError(player, "You must be a member of " + ChatColor.YELLOW + cartography.getGroup() + ChatColor.RED + " to use that factory!");
            cooldowns.put(playerId, System.currentTimeMillis());  // Set cooldown
            return;
        }

        event.setCancelled(true);
        openFactory(player);
        cooldowns.put(playerId, System.currentTimeMillis());  // Set cooldown
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;

            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();

            // Retrieve the recipe based on the slot
            FactoryRecipe matchingRecipe = slotToRecipe.get(clickedSlot);
            if (matchingRecipe == null) return;
            Material recipeType = matchingRecipe.getResult().getType();

            ItemStack result;
            if (recipeType == Material.ENCHANTED_BOOK) {
                ItemStack book = player.getInventory().getItemInOffHand();
                if (book.getType() != Material.WRITTEN_BOOK) {
                    MessageUtils.sendError(player, "You must be holding a written book in your offhand!");
                    return;
                }
                result = book.clone();
                result.setAmount(matchingRecipe.getResult().getAmount());
            }
            else if (recipeType == Material.PAPER) {
                ItemStack book = player.getInventory().getItemInOffHand();
                if (book.getType() != Material.WRITTEN_BOOK) {
                    MessageUtils.sendError(player, "You must be holding a written book in your offhand!");
                    return;
                }
                result = matchingRecipe.getResult();
                BookMeta bookMeta = (BookMeta) book.getItemMeta();
                ItemMeta meta = result.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + bookMeta.getTitle());
                List<String> lore;
                if (clickedSlot == 15) {
                    lore = convertPageToLore(bookMeta, true);
                } else {
                    lore = convertPageToLore(bookMeta, false);
                }
                meta.setLore(lore);
                result.setItemMeta(meta);
            }
            else {
                result = matchingRecipe.getResult();
            }

            // Complete recipe
            if (hasRequiredIngredients(player, matchingRecipe)) {
                removeIngredients(player, matchingRecipe);
                player.getInventory().addItem(result);
                player.sendMessage(String.format("%sYou crafted %s%d %s",
                        ChatColor.GREEN,
                        ChatColor.YELLOW,
                        matchingRecipe.getResult().getAmount(),
                        StringUtils.stringifyType(matchingRecipe.getResult().getType())
                ));
            } else {
                player.sendMessage(ChatColor.RED + "You don't have the required ingredients!");
            }
        }
    }

    public String generateSecurityCode(BookMeta bookMeta) {
        StringBuilder input = new StringBuilder();
        input.append(bookMeta.getTitle());
        input.append(bookMeta.getAuthor());
        for (int i = 1; i <= bookMeta.getPageCount(); i++) {
            input.append(bookMeta.getPage(i));
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.toString().getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> convertPageToLore(BookMeta bookMeta, boolean isSecure) {
        String text = ChatColor.GRAY + bookMeta.getPage(1).replace("&", "§");
        List<String> lore = new ArrayList<>();
        StringBuilder temp = new StringBuilder();
        String[] lines = text.split("\n");
        for (String line : lines) {
            String[] words = line.split(" ");
            for (String word : words) {
                if (temp.length() + word.length() + 1 > 30) {
                    lore.add(ChatColor.GRAY + temp.toString().trim());
                    temp = new StringBuilder();
                }
                temp.append(word).append(" ");
                if (temp.length() >= 140) break;
            }
            lore.add(ChatColor.GRAY + temp.toString().trim());
            temp = new StringBuilder();
            if (temp.length() >= 140) break;
        }
        if (!temp.isEmpty()) {
            lore.add(temp.toString().trim());
        }
        if (isSecure) {
            lore.add(ChatColor.DARK_GRAY + "#" + generateSecurityCode(bookMeta));
        }
        return lore;
    }

    private boolean hasRequiredIngredients(Player player, FactoryRecipe recipe) {
        for (ItemStack ingredient : recipe.getIngredients()) {
            if (ingredient.getType() == Material.WRITTEN_BOOK) continue;
            if (!player.getInventory().containsAtLeast(ingredient, ingredient.getAmount())) {
                return false;
            }
        }
        return true;
    }

    private void removeIngredients(Player player, FactoryRecipe recipe) {
        for (ItemStack ingredient : recipe.getIngredients()) {
            if (ingredient.getType() == Material.WRITTEN_BOOK) continue;
            player.getInventory().removeItem(new ItemStack(ingredient.getType(), ingredient.getAmount()));
        }
    }

    private void openFactory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 18, GUI_TITLE);
        for (Map.Entry<Integer, FactoryRecipe> entry : slotToRecipe.entrySet()) {
            gui.setItem(entry.getKey(), createRecipeItem(entry.getValue()));
        }
        player.openInventory(gui);
    }

    private ItemStack createRecipeItem(FactoryRecipe recipe) {
        ItemStack item = recipe.getResult().clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + recipe.getTitle());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "--------------");
        for (ItemStack ingredient : recipe.getIngredients()) {
            lore.add(ChatColor.GRAY+"- "+ChatColor.GREEN+ingredient.getAmount()
                    +" "+StringUtils.stringifyType(ingredient.getType())
            );
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
