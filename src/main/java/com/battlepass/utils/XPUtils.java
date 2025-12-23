package com.battlepass.utils;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Utility class for XP calculations and level management
 */
public class XPUtils {

    // Default values (can be overridden by config)
    private static final double DEFAULT_BASE_XP = 100;
    private static final double DEFAULT_MULTIPLIER = 1.21;

    /**
     * Calculates the XP required for a specific level using config values
     * @param level The level to calculate XP for (must be >= 1)
     * @return The XP required for the given level
     */
    public static int calculateXPRequired(int level) {
        // Validate level is positive
        if (level < 1) {
            level = 1;
        }
        
        BattlePassPlugin plugin = BattlePassPlugin.getInstance();
        double baseXP = DEFAULT_BASE_XP;
        double multiplier = DEFAULT_MULTIPLIER;
        
        if (plugin != null && plugin.getConfig() != null) {
            baseXP = plugin.getConfig().getDouble("xp.base-xp", DEFAULT_BASE_XP);
            multiplier = plugin.getConfig().getDouble("xp.multiplier", DEFAULT_MULTIPLIER);
        }
        
        double xp = baseXP * Math.pow(multiplier, level - 1);
        return (int) Math.floor(xp);
    }

    /**
     * Adds XP to a player and handles level ups
     */
    public static void addXP(BattlePassPlugin plugin, Player player, double amount) {
        if (plugin.isLocked()) {
            return;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(player);
        double xpToAdd = amount;

        // Apply double XP if active
        if (plugin.getDoubleXPManager().isDoubleXPActive()) {
            xpToAdd = amount * 2;
            sendActionBar(player, ChatColor.GOLD + "" + ChatColor.BOLD + "+ " + (int) xpToAdd + " XP! " + 
                         ChatColor.YELLOW + "" + ChatColor.BOLD + "(2X ACTIVE!)");
        } else {
            if (amount >= 1) {
                sendActionBar(player, ChatColor.GOLD + "" + ChatColor.BOLD + "+ " + (int) xpToAdd + " XP!");
            }
        }

        data.addXp(xpToAdd);
        checkLevelUp(plugin, player, data);
        plugin.getDataManager().savePlayerDataAsync(data);
    }

    /**
     * Checks if a player should level up and processes level ups
     */
    public static void checkLevelUp(BattlePassPlugin plugin, Player player, PlayerData data) {
        int currentLevel = data.getLevel();
        int xpNeeded = calculateXPRequired(currentLevel);

        while (data.getXp() >= xpNeeded) {
            data.subtractXp(xpNeeded);
            data.addLevel(1);
            
            // Get configurable messages
            String levelUpMsg = plugin.getConfig().getString("messages.level-up", 
                "&6&lLEVEL UP! &eYou are now level &6%level%&e!");
            String claimMsg = plugin.getConfig().getString("messages.claim-reward", 
                "&7Open &e/bp &7to claim your reward!");
            
            // Replace placeholders and translate color codes
            levelUpMsg = ChatColor.translateAlternateColorCodes('&', 
                levelUpMsg.replace("%level%", String.valueOf(data.getLevel())));
            claimMsg = ChatColor.translateAlternateColorCodes('&', claimMsg);
            
            player.sendMessage(levelUpMsg);
            player.sendMessage(claimMsg);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            // Check for next level
            xpNeeded = calculateXPRequired(data.getLevel());
        }
    }

    /**
     * Sends an action bar message to a player
     */
    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    /**
     * Formats a level display with color based on status
     */
    public static String formatLevelDisplay(int level, int playerLevel, boolean claimed) {
        if (level <= playerLevel) {
            if (claimed) {
                return ChatColor.GREEN + "" + ChatColor.BOLD + "✓ LEVEL " + level;
            } else {
                return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "⚡ LEVEL " + level;
            }
        } else if (level == playerLevel + 1) {
            return ChatColor.YELLOW + "" + ChatColor.BOLD + "⟡ LEVEL " + level;
        } else {
            return ChatColor.GRAY + "" + ChatColor.BOLD + "✖ LEVEL " + level;
        }
    }
}
