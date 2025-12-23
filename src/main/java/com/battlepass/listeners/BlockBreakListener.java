package com.battlepass.listeners;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import com.battlepass.utils.ItemUtils;
import com.battlepass.utils.XPUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Handles block break events for mining XP
 */
public class BlockBreakListener implements Listener {

    private final BattlePassPlugin plugin;
    private final Random random;

    public BlockBreakListener(BattlePassPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Only award XP in survival mode
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        // Track blocks mined
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        data.addBlockMined();

        // Get the tool the player is using
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Check if using the special BattlePass pickaxe
        if (ItemUtils.isSpecialPickaxe(tool)) {
            // 3% chance for bonus XP (8 XP)
            if (random.nextDouble() < 0.03) {
                XPUtils.addXP(plugin, player, 8);
            } else {
                // Normal special pickaxe XP (4 XP)
                XPUtils.addXP(plugin, player, 4);
            }
        } else {
            // Normal mining XP (2 XP)
            XPUtils.addXP(plugin, player, 2);
        }
    }
}
