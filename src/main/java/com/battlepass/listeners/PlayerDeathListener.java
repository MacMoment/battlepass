package com.battlepass.listeners;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import com.battlepass.utils.XPUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Handles player death events for kill tracking
 */
public class PlayerDeathListener implements Listener {

    private final BattlePassPlugin plugin;

    public PlayerDeathListener(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Only award XP if the killer is a player (not the same player)
        if (killer != null && !killer.equals(victim)) {
            // Track the kill
            PlayerData killerData = plugin.getDataManager().getPlayerData(killer);
            killerData.addKill();

            // Award XP for the kill (configurable)
            double killXP = plugin.getConfig().getDouble("xp.rewards.player-kill", 3);
            XPUtils.addXP(plugin, killer, killXP);
        }
    }
}
