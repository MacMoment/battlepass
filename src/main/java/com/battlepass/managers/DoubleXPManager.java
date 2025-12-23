package com.battlepass.managers;

import com.battlepass.BattlePassPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages double XP events
 */
public class DoubleXPManager {

    private final BattlePassPlugin plugin;
    private boolean doubleXPActive = false;
    private int remainingMinutes = 0;
    private BukkitTask countdownTask;

    public DoubleXPManager(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts a double XP event for the specified duration
     * @param minutes Duration in minutes
     * @return true if event started successfully
     */
    public boolean startDoubleXP(int minutes) {
        if (doubleXPActive) {
            return false;
        }

        if (minutes <= 0 || minutes > 1440) { // Max 24 hours
            return false;
        }

        doubleXPActive = true;
        remainingMinutes = minutes;

        // Broadcast announcement
        String timeDisplay = formatTime(minutes);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "DOUBLE XP EVENT ACTIVATED!");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GRAY + "Duration: " + ChatColor.GOLD + timeDisplay);
        Bukkit.broadcastMessage("");

        // Play sound to all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 2.0f);
        }

        // Start countdown task (runs every minute)
        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            remainingMinutes--;
            
            if (remainingMinutes <= 0) {
                stopDoubleXP();
            }
        }, 1200L, 1200L); // 1200 ticks = 1 minute

        return true;
    }

    /**
     * Stops the current double XP event
     */
    public void stopDoubleXP() {
        if (!doubleXPActive) {
            return;
        }

        doubleXPActive = false;
        remainingMinutes = 0;

        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "DOUBLE XP EVENT ENDED!");
        Bukkit.broadcastMessage("");
    }

    /**
     * Checks if double XP is currently active
     */
    public boolean isDoubleXPActive() {
        return doubleXPActive;
    }

    /**
     * Gets the remaining minutes for the current event
     */
    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    /**
     * Formats time in minutes to a readable string (e.g., "1h 30m")
     */
    public String formatTime(int minutes) {
        if (minutes >= 60) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            if (mins == 0) {
                return hours + "h";
            } else {
                return hours + "h " + mins + "m";
            }
        } else {
            return minutes + "m";
        }
    }

    /**
     * Parses a time string like "30m", "1h", "2h30m" to minutes
     * @return minutes, or -1 if invalid format
     */
    public int parseTime(String input) {
        if (input == null || input.isEmpty()) {
            return -1;
        }

        input = input.toLowerCase().trim();
        int totalMinutes = 0;

        try {
            if (input.contains("h")) {
                String[] parts = input.split("h");
                int hours = Integer.parseInt(parts[0].trim());
                totalMinutes += hours * 60;

                if (parts.length > 1 && !parts[1].isEmpty()) {
                    String minPart = parts[1].replace("m", "").trim();
                    if (!minPart.isEmpty()) {
                        totalMinutes += Integer.parseInt(minPart);
                    }
                }
            } else if (input.contains("m")) {
                String minPart = input.replace("m", "").trim();
                totalMinutes = Integer.parseInt(minPart);
            } else {
                // Assume it's just a number in minutes
                totalMinutes = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            return -1;
        }

        return totalMinutes;
    }

    /**
     * Calculates XP with double XP modifier if active
     */
    public double calculateXP(double baseXP) {
        return doubleXPActive ? baseXP * 2 : baseXP;
    }
}
