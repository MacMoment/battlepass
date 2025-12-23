package com.battlepass.listeners;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import com.battlepass.utils.ItemUtils;
import com.battlepass.utils.XPUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

/**
 * Handles block break events for mining XP
 * XP is only awarded for mining ores
 */
public class BlockBreakListener implements Listener {

    private final BattlePassPlugin plugin;
    private final Random random;
    
    // Set of all ore blocks that award XP
    private static final Set<Material> ORES = EnumSet.of(
        // Overworld ores
        Material.COAL_ORE,
        Material.DEEPSLATE_COAL_ORE,
        Material.IRON_ORE,
        Material.DEEPSLATE_IRON_ORE,
        Material.COPPER_ORE,
        Material.DEEPSLATE_COPPER_ORE,
        Material.GOLD_ORE,
        Material.DEEPSLATE_GOLD_ORE,
        Material.REDSTONE_ORE,
        Material.DEEPSLATE_REDSTONE_ORE,
        Material.EMERALD_ORE,
        Material.DEEPSLATE_EMERALD_ORE,
        Material.LAPIS_ORE,
        Material.DEEPSLATE_LAPIS_ORE,
        Material.DIAMOND_ORE,
        Material.DEEPSLATE_DIAMOND_ORE,
        // Nether ores
        Material.NETHER_GOLD_ORE,
        Material.NETHER_QUARTZ_ORE,
        Material.ANCIENT_DEBRIS
    );

    public BlockBreakListener(BattlePassPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Only award XP in survival mode
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        // Track blocks mined (all blocks count for statistics)
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        data.addBlockMined();

        // Only award XP for mining ores
        Block block = event.getBlock();
        if (!isOre(block.getType())) {
            return;
        }

        // Get configurable XP values
        double normalXP = plugin.getConfig().getDouble("xp.rewards.block-mine", 2);
        double specialPickaxeXP = plugin.getConfig().getDouble("xp.rewards.special-pickaxe", 4);
        double specialPickaxeBonusXP = plugin.getConfig().getDouble("xp.rewards.special-pickaxe-bonus", 8);
        double bonusChance = plugin.getConfig().getDouble("xp.rewards.special-pickaxe-bonus-chance", 0.03);

        // Get the tool the player is using
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Check if using the special BattlePass pickaxe
        if (ItemUtils.isSpecialPickaxe(tool)) {
            // Chance for bonus XP
            if (random.nextDouble() < bonusChance) {
                XPUtils.addXP(plugin, player, specialPickaxeBonusXP);
            } else {
                // Normal special pickaxe XP
                XPUtils.addXP(plugin, player, specialPickaxeXP);
            }
        } else {
            // Normal mining XP
            XPUtils.addXP(plugin, player, normalXP);
        }
    }

    /**
     * Checks if the given material is an ore
     * @param material The material to check
     * @return true if the material is an ore
     */
    public static boolean isOre(Material material) {
        return ORES.contains(material);
    }
}
