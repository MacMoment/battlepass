package com.battlepass.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Utility class for item creation and management
 */
public class ItemUtils {

    // The special pickaxe name (using color codes for special characters)
    public static final String SPECIAL_PICKAXE_NAME = ChatColor.RED + "" + ChatColor.BOLD + "ᴋʀᴀᴍᴘᴜꜱ' ᴄᴀɴᴅʏ ᴄᴀɴᴇ";

    /**
     * Creates the special BattlePass pickaxe that gives bonus XP
     */
    public static ItemStack createSpecialPickaxe() {
        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(SPECIAL_PICKAXE_NAME);
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━",
                ChatColor.GREEN + "✦ BattlePass Special Pickaxe",
                "",
                ChatColor.GOLD + "➥ " + ChatColor.YELLOW + "+4 XP per block",
                ChatColor.GOLD + "➥ " + ChatColor.LIGHT_PURPLE + "3% chance for +8 XP",
                ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━",
                "",
                ChatColor.DARK_PURPLE + "BattlePass Exclusive"
            ));
            
            // Add some enchantments to make it special
            meta.addEnchant(Enchantment.EFFICIENCY, 5, true);
            meta.addEnchant(Enchantment.UNBREAKING, 3, true);
            meta.addEnchant(Enchantment.FORTUNE, 3, true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            pickaxe.setItemMeta(meta);
        }
        
        return pickaxe;
    }

    /**
     * Checks if an item is the special BattlePass pickaxe
     */
    public static boolean isSpecialPickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_PICKAXE) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        
        String displayName = meta.getDisplayName();
        // Check if the name contains the special identifier
        return displayName.contains("ᴋʀᴀᴍᴘᴜꜱ' ᴄᴀɴᴅʏ ᴄᴀɴᴇ");
    }

    /**
     * Creates a basic ItemStack with a name
     */
    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Creates a basic ItemStack with a name and lore
     */
    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
