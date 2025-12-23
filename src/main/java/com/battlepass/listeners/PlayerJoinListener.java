package com.battlepass.listeners;

import com.battlepass.BattlePassPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join and quit events
 */
public class PlayerJoinListener implements Listener {

    private final BattlePassPlugin plugin;

    public PlayerJoinListener(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Initialize player data if not exists
        plugin.getDataManager().initializePlayer(player);
        
        // Load player data from file if exists
        plugin.getDataManager().loadPlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Save player data
        plugin.getDataManager().savePlayerDataAsync(plugin.getDataManager().getPlayerData(player));
        
        // Clear GUI metadata
        plugin.getGUIManager().clearPlayerData(player.getUniqueId());
    }
}
