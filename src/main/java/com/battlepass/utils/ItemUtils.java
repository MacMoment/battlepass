package com.battlepass.utils;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

/**
 * Utility class for item creation and management
 */
public class ItemUtils {

    // The special pickaxe name (using color codes for special characters)
    public static final String SPECIAL_PICKAXE_NAME = ChatColor.RED + "" + ChatColor.BOLD + "ᴋʀᴀᴍᴘᴜꜱ' ᴄᴀɴᴅʏ ᴄᴀɴᴇ";
    
    // Persistent data key for identifying special items
    private static final String SPECIAL_PICKAXE_KEY = "battlepass_special_pickaxe";

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
                ChatColor.GOLD + "➥ " + ChatColor.YELLOW + "+4 XP per ore",
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
            
            // Add persistent data tag for reliable identification
            BattlePassPlugin plugin = BattlePassPlugin.getInstance();
            if (plugin != null) {
                NamespacedKey key = new NamespacedKey(plugin, SPECIAL_PICKAXE_KEY);
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(key, PersistentDataType.BYTE, (byte) 1);
            }
            
            pickaxe.setItemMeta(meta);
        }
        
        return pickaxe;
    }

    /**
     * Checks if an item is the special BattlePass pickaxe
     * Uses PersistentDataContainer for reliable identification, with fallback to name check
     */
    public static boolean isSpecialPickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_PICKAXE) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        // Primary check: Use PersistentDataContainer (most reliable)
        BattlePassPlugin plugin = BattlePassPlugin.getInstance();
        if (plugin != null) {
            NamespacedKey key = new NamespacedKey(plugin, SPECIAL_PICKAXE_KEY);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(key, PersistentDataType.BYTE)) {
                return true;
            }
        }
        
        // Fallback check: Use display name (for backwards compatibility)
        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            return displayName.contains("ᴋʀᴀᴍᴘᴜꜱ' ᴄᴀɴᴅʏ ᴄᴀɴᴇ");
        }
        
        return false;
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
