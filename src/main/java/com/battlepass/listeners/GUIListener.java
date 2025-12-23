package com.battlepass.listeners;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import com.battlepass.utils.XPUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all GUI click events
 */
public class GUIListener implements Listener {

    private final BattlePassPlugin plugin;
    private static final int LEVELS_PER_PAGE = 7;

    public GUIListener(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Handle BattlePass main GUI
        if (title.contains("BATTLEPASS")) {
            handleBattlePassClick(event, player);
            return;
        }

        // Handle Reward Setup GUI
        if (title.contains("Reward Setup")) {
            handleRewardSetupClick(event, player);
            return;
        }

        // Handle Item Setup GUI
        if (title.contains("BP Items Level")) {
            handleItemSetupClick(event, player);
            return;
        }
    }

    private void handleBattlePassClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot > 53) return;

        int currentPage = plugin.getGUIManager().getPlayerPage(player.getUniqueId());

        // Previous page (slot 46)
        if (slot == 46) {
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().contains("Previous")) {
                    plugin.getGUIManager().openBattlePassGUI(player, currentPage - 1);
                }
            }
            return;
        }

        // Next page (slot 52)
        if (slot == 52) {
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().contains("Next")) {
                    plugin.getGUIManager().openBattlePassGUI(player, currentPage + 1);
                }
            }
            return;
        }

        // Playtime (slot 48)
        if (slot == 48) {
            player.closeInventory();
            player.performCommand("playtime");
            return;
        }

        // Stats (slot 50)
        if (slot == 50) {
            player.closeInventory();
            PlayerData data = plugin.getDataManager().getPlayerData(player);
            player.sendMessage(ChatColor.GOLD + "Your Statistics:");
            player.sendMessage(ChatColor.GRAY + "  " + ChatColor.DARK_GRAY + "▸ " + ChatColor.YELLOW + "Level: " + ChatColor.GOLD + data.getLevel());
            player.sendMessage(ChatColor.GRAY + "  " + ChatColor.DARK_GRAY + "▸ " + ChatColor.YELLOW + "XP: " + ChatColor.GOLD + (int) data.getXp() + ChatColor.GRAY + "/" + ChatColor.GOLD + XPUtils.calculateXPRequired(data.getLevel()));
            player.sendMessage(ChatColor.GRAY + "  " + ChatColor.DARK_GRAY + "▸ " + ChatColor.YELLOW + "Kills: " + ChatColor.GOLD + data.getKills());
            player.sendMessage(ChatColor.GRAY + "  " + ChatColor.DARK_GRAY + "▸ " + ChatColor.YELLOW + "Blocks Mined: " + ChatColor.GOLD + data.getBlocksMined());
            
            // Reopen GUI after a moment
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGUIManager().openBattlePassGUI(player, currentPage);
            }, 40L);
            return;
        }

        // Rewards setup (slot 49)
        if (slot == 49) {
            if (player.hasPermission("battlepass.admin")) {
                player.closeInventory();
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getGUIManager().openRewardSetupGUI(player, 1);
                }, 2L);
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to setup rewards!");
            }
            return;
        }

        // Level items (slots 28-34)
        if (slot >= 28 && slot <= 34) {
            int startLevel = ((currentPage - 1) * LEVELS_PER_PAGE) + 1;
            int clickedLevel = startLevel + (slot - 28);

            PlayerData data = plugin.getDataManager().getPlayerData(player);

            // Check if player has reached this level
            if (clickedLevel <= data.getLevel()) {
                // Check if already claimed
                if (!data.hasClaimedReward(clickedLevel)) {
                    // Claim the reward
                    data.claimReward(clickedLevel);
                    
                    String rewardText = plugin.getRewardManager().getRewardDisplayText(clickedLevel);
                    if (rewardText.isEmpty()) {
                        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "✓ CLAIMED! " + 
                                         ChatColor.GRAY + "Level " + clickedLevel + " reward claimed!");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "✓ CLAIMED! " + 
                                         ChatColor.GRAY + "Level " + clickedLevel + " reward: " + rewardText);
                    }
                    
                    // Give item rewards
                    List<ItemStack> rewardItems = plugin.getRewardManager().getRewardItems(clickedLevel);
                    for (ItemStack item : rewardItems) {
                        player.getInventory().addItem(item.clone());
                    }
                    
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    plugin.getDataManager().savePlayerDataAsync(data);
                    
                    // Refresh GUI
                    plugin.getGUIManager().openBattlePassGUI(player, currentPage);
                } else {
                    player.sendMessage(ChatColor.RED + "You already claimed this reward!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You haven't reached this level yet!");
            }
        }
    }

    private void handleRewardSetupClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot > 53) return;

        int currentPage = plugin.getGUIManager().getRewardSetupPage(player.getUniqueId());

        // Previous page (slot 48)
        if (slot == 48) {
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().contains("Previous")) {
                    plugin.getGUIManager().openRewardSetupGUI(player, currentPage - 1);
                }
            }
            return;
        }

        // Next page (slot 50)
        if (slot == 50) {
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().contains("Next")) {
                    plugin.getGUIManager().openRewardSetupGUI(player, currentPage + 1);
                }
            }
            return;
        }

        // Level items (slots 10-43, excluding borders)
        if (slot >= 10 && slot <= 43) {
            // Skip border slots
            if (slot == 17 || slot == 18 || slot == 26 || slot == 27 || slot == 35 || slot == 36 || slot == 44) {
                return;
            }

            int startLevel = ((currentPage - 1) * 21) + 1;
            int adjustedSlot = slot - 10;
            
            // Calculate which level was clicked by counting skipped slots
            // Skipped slots: 17-18 (2 slots), 26-27 (2 slots), 35-36 (2 slots)
            int skippedSlots = 0;
            if (slot >= 19) skippedSlots += 2; // slots 17-18 skipped
            if (slot >= 28) skippedSlots += 2; // slots 26-27 skipped
            if (slot >= 37) skippedSlots += 2; // slots 35-36 skipped
            
            int clickedLevel = startLevel + adjustedSlot - skippedSlots;
            
            if (clickedLevel > 70) return;

            ClickType clickType = event.getClick();
            
            if (clickType == ClickType.LEFT) {
                // Set reward text
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Type the reward description for level " + 
                                 ChatColor.GOLD + clickedLevel + ChatColor.YELLOW + " in chat:");
                player.sendMessage(ChatColor.GRAY + "Example: " + ChatColor.YELLOW + "" + 
                                 ChatColor.GOLD + "1x Legendary Crate Key");
                player.sendMessage(ChatColor.GRAY + "Type " + ChatColor.RED + "cancel " + 
                                 ChatColor.GRAY + "to cancel");
                
                plugin.getGUIManager().setEditingRewardLevel(player.getUniqueId(), clickedLevel);
            } else if (clickType == ClickType.RIGHT) {
                // Open item setup
                player.closeInventory();
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getGUIManager().openItemSetupGUI(player, clickedLevel, currentPage);
                }, 2L);
            }
        }
    }

    private void handleItemSetupClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();
        
        // Allow interaction with item slots (18-44)
        if (slot >= 18 && slot <= 44) {
            // Allow item placement/removal
            return;
        }

        // Confirm button (slot 49)
        if (slot == 49) {
            event.setCancelled(true);
            
            Integer level = plugin.getGUIManager().getItemSetupLevel(player.getUniqueId());
            Integer returnPage = plugin.getGUIManager().getItemSetupReturnPage(player.getUniqueId());
            
            if (level == null) return;

            // Save items
            List<ItemStack> items = new ArrayList<>();
            Inventory inv = event.getInventory();
            for (int i = 18; i <= 44; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    items.add(item.clone());
                }
            }

            plugin.getRewardManager().setRewardItems(level, items);
            player.sendMessage(ChatColor.GREEN + "Successfully saved items for level " + level + "!");
            
            plugin.getGUIManager().clearItemSetupData(player.getUniqueId());
            player.closeInventory();
            
            // Return to reward setup
            int page = returnPage != null ? returnPage : 1;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGUIManager().openRewardSetupGUI(player, page);
            }, 2L);
            return;
        }

        // Cancel any other clicks
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();

        // Handle item setup GUI close without saving
        if (title.contains("BP Items Level")) {
            // Clear item setup data on close (items not saved)
            plugin.getGUIManager().clearItemSetupData(player.getUniqueId());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Integer editingLevel = plugin.getGUIManager().getEditingRewardLevel(player.getUniqueId());
        
        if (editingLevel == null) return;
        
        event.setCancelled(true);
        String message = event.getMessage();
        
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.RED + "Cancelled editing.");
            plugin.getGUIManager().clearEditingRewardLevel(player.getUniqueId());
            return;
        }
        
        // Set the reward text (translate color codes)
        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);
        plugin.getRewardManager().setRewardText(editingLevel, coloredMessage);
        player.sendMessage(ChatColor.GREEN + "Set level " + editingLevel + " reward to: " + coloredMessage);
        
        plugin.getGUIManager().clearEditingRewardLevel(player.getUniqueId());
        
        // Reopen reward setup GUI
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getGUIManager().openRewardSetupGUI(player, 1);
        });
    }
}
