package com.battlepass.gui;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import com.battlepass.managers.RewardManager;
import com.battlepass.utils.XPUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

/**
 * Manages all GUI interfaces for the BattlePass plugin
 */
public class GUIManager {

    private final BattlePassPlugin plugin;
    private static final int MAX_PAGES = 10;
    private static final int LEVELS_PER_PAGE = 7;
    private static final int MAX_LEVEL = 70;

    // Player metadata for tracking GUI state
    private final Map<UUID, Integer> playerPages = new HashMap<>();
    private final Map<UUID, Integer> rewardSetupPages = new HashMap<>();
    private final Map<UUID, Integer> itemSetupLevels = new HashMap<>();
    private final Map<UUID, Integer> itemSetupReturnPages = new HashMap<>();
    private final Map<UUID, Integer> editingRewardLevel = new HashMap<>();

    public GUIManager(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens the main BattlePass GUI for a player
     */
    public void openBattlePassGUI(Player player, int page) {
        page = Math.max(1, Math.min(page, MAX_PAGES));
        playerPages.put(player.getUniqueId(), page);

        PlayerData data = plugin.getDataManager().getPlayerData(player);
        int currentLevel = data.getLevel();
        int xpNeeded = XPUtils.calculateXPRequired(currentLevel);

        String title = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[ " + 
                      ChatColor.GOLD + "" + ChatColor.BOLD + "BATTLEPASS" + 
                      ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ] " + 
                      ChatColor.GRAY + "Page " + page + "/" + MAX_PAGES;
        
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Fill with black glass
        ItemStack blackGlass = createGlassPane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, blackGlass);
        }

        // Top row - orange glass
        ItemStack orangeGlass = createGlassPane(Material.ORANGE_STAINED_GLASS_PANE, " ");
        for (int i = 0; i <= 8; i++) {
            gui.setItem(i, orangeGlass);
        }

        // Player head with progress (slot 10)
        gui.setItem(10, createPlayerHead(player, data, xpNeeded));

        // How to level up info (slot 16)
        gui.setItem(16, createHowToLevelUpItem(xpNeeded, currentLevel));

        // Gray glass row (slots 18-26)
        ItemStack grayGlass = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 18; i <= 26; i++) {
            gui.setItem(i, grayGlass);
        }

        // Level items (slots 28-34)
        int startLevel = ((page - 1) * LEVELS_PER_PAGE) + 1;
        int slot = 28;
        for (int i = 0; i < LEVELS_PER_PAGE; i++) {
            int displayLevel = startLevel + i;
            gui.setItem(slot, createLevelItem(displayLevel, currentLevel, data, xpNeeded));
            slot++;
        }

        // Gray glass row (slots 36-44)
        for (int i = 36; i <= 44; i++) {
            gui.setItem(i, grayGlass);
        }

        // Bottom row items
        gui.setItem(45, createFreeRewardsItem());
        gui.setItem(48, createPlaytimeItem());
        gui.setItem(49, createRewardsSetupItem());
        gui.setItem(50, createStatsItem());
        gui.setItem(53, orangeGlass);

        // Navigation arrows
        if (page > 1) {
            gui.setItem(46, createNavigationArrow(true, page));
        }
        if (page < MAX_PAGES) {
            gui.setItem(52, createNavigationArrow(false, page));
        }

        player.openInventory(gui);
    }

    /**
     * Opens the reward setup GUI for admins
     */
    public void openRewardSetupGUI(Player player, int page) {
        int maxPage = (int) Math.ceil(MAX_LEVEL / 21.0);
        page = Math.max(1, Math.min(page, maxPage));
        rewardSetupPages.put(player.getUniqueId(), page);

        String title = ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Setup " + 
                      ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Page " + page + "/" + maxPage;
        
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Fill with black glass
        ItemStack blackGlass = createGlassPane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, blackGlass);
        }

        // Top and bottom rows - orange glass
        ItemStack orangeGlass = createGlassPane(Material.ORANGE_STAINED_GLASS_PANE, " ");
        for (int i = 0; i <= 8; i++) {
            gui.setItem(i, orangeGlass);
        }
        for (int i = 45; i <= 53; i++) {
            gui.setItem(i, orangeGlass);
        }

        // Info nether star (slot 4)
        gui.setItem(4, createRewardManagerInfo());

        // Level items (slots 10-43, 21 slots)
        int startLevel = ((page - 1) * 21) + 1;
        int slot = 10;
        for (int i = 0; i < 21; i++) {
            int level = startLevel + i;
            if (level > MAX_LEVEL) break;
            
            // Skip certain slots (left and right edges)
            while (slot == 17 || slot == 18 || slot == 26 || slot == 27 || slot == 35 || slot == 36 || slot == 44) {
                slot++;
            }
            
            gui.setItem(slot, createRewardSetupItem(level));
            slot++;
        }

        // Quick command book (slot 49)
        gui.setItem(49, createQuickCommandBook());

        // Navigation arrows
        if (page > 1) {
            gui.setItem(48, createNavigationArrow(true, page));
        }
        if (page < maxPage) {
            gui.setItem(50, createNavigationArrow(false, page));
        }

        player.openInventory(gui);
    }

    /**
     * Opens the item setup GUI for a specific level
     */
    public void openItemSetupGUI(Player player, int level, int returnPage) {
        itemSetupLevels.put(player.getUniqueId(), level);
        itemSetupReturnPages.put(player.getUniqueId(), returnPage);

        String title = ChatColor.GOLD + "" + ChatColor.BOLD + "BP Items Level " + level;
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Fill with black glass
        ItemStack blackGlass = createGlassPane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, blackGlass);
        }

        // Info nether star (slot 4)
        ItemStack info = new ItemStack(Material.NETHER_STAR);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Item Reward Setup");
        infoMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Place items in the slots below",
            ChatColor.GRAY + "All items will be given to the player",
            ChatColor.GRAY + "when they claim this level",
            "",
            ChatColor.YELLOW + "Click " + ChatColor.GREEN + "Confirm " + ChatColor.YELLOW + "to save"
        ));
        info.setItemMeta(infoMeta);
        gui.setItem(4, info);

        // Load existing items (slots 18-44)
        List<ItemStack> existingItems = plugin.getRewardManager().getRewardItems(level);
        int itemSlot = 18;
        for (ItemStack item : existingItems) {
            if (itemSlot <= 44) {
                gui.setItem(itemSlot, item);
                itemSlot++;
            }
        }

        // Confirm button (slot 49)
        ItemStack confirm = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm");
        confirmMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Save these items as rewards",
            ChatColor.GRAY + "for level " + level
        ));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(49, confirm);

        player.openInventory(gui);
    }

    // Helper methods for creating items

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    private ItemStack createPlayerHead(Player player, PlayerData data, int xpNeeded) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + player.getName() + "'s Progress");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━",
            ChatColor.YELLOW + "Level: " + ChatColor.GOLD + data.getLevel(),
            ChatColor.YELLOW + "⚡ XP: " + ChatColor.GREEN + (int) data.getXp() + 
                ChatColor.GRAY + " / " + ChatColor.RED + xpNeeded,
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━"
        ));
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createHowToLevelUpItem(int xpNeeded, int currentLevel) {
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = star.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "✦ HOW TO LEVEL UP");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━",
            ChatColor.WHITE + "➥ " + ChatColor.AQUA + "Mine blocks " + ChatColor.GRAY + "(+2.0 XP)",
            ChatColor.WHITE + "➥ " + ChatColor.GOLD + "Mine with BP Pickaxe " + ChatColor.GRAY + "(+4 to +8 XP)",
            ChatColor.WHITE + "➥ " + ChatColor.RED + "Kill players " + ChatColor.GRAY + "(+3 XP)",
            "",
            ChatColor.GRAY + "Need " + ChatColor.GREEN + xpNeeded + " XP " + 
                ChatColor.GRAY + "for level " + ChatColor.YELLOW + (currentLevel + 1),
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━"
        ));
        star.setItemMeta(meta);
        return star;
    }

    private ItemStack createLevelItem(int level, int currentLevel, PlayerData data, int xpNeeded) {
        ItemStack item;
        String name;
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━");

        RewardManager rewards = plugin.getRewardManager();
        String rewardText = rewards.getRewardDisplayText(level);
        int levelXP = XPUtils.calculateXPRequired(level);

        if (level <= currentLevel) {
            // Unlocked level
            if (data.hasClaimedReward(level)) {
                item = new ItemStack(Material.MINECART);
                name = ChatColor.GREEN + "" + ChatColor.BOLD + "✓ LEVEL " + level;
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "✓ Claimed!");
            } else {
                item = new ItemStack(Material.CHEST_MINECART);
                name = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "⚡ LEVEL " + level;
                lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "▶ CLICK TO CLAIM!");
            }
        } else if (level == currentLevel + 1) {
            // Next level
            item = new ItemStack(Material.CHEST_MINECART);
            name = ChatColor.YELLOW + "" + ChatColor.BOLD + "⟡ LEVEL " + level;
            lore.add(ChatColor.GOLD + "◆ Next Level");
            if (!rewardText.isEmpty()) {
                lore.add("");
                lore.add(ChatColor.GRAY + "Reward: " + rewardText);
            }
            lore.add(ChatColor.GRAY + "Need: " + ChatColor.RED + xpNeeded + " XP");
        } else {
            // Locked level
            item = new ItemStack(Material.FURNACE_MINECART);
            name = ChatColor.GRAY + "" + ChatColor.BOLD + "✖ LEVEL " + level;
            lore.add(ChatColor.RED + "✖ Locked");
            if (!rewardText.isEmpty()) {
                lore.add("");
                lore.add(ChatColor.GRAY + "Reward: " + rewardText);
            }
            lore.add(ChatColor.GRAY + "Need: " + ChatColor.RED + levelXP + " XP");
        }

        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━");

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createFreeRewardsItem() {
        ItemStack emerald = new ItemStack(Material.EMERALD);
        ItemMeta meta = emerald.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "★ FREE REWARDS");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "All rewards are " + ChatColor.GREEN + "100% free" + ChatColor.GRAY + "!",
            ChatColor.GRAY + "Just level up and claim them!",
            "",
            ChatColor.YELLOW + "➜ No payments required!"
        ));
        emerald.setItemMeta(meta);
        return emerald;
    }

    private ItemStack createPlaytimeItem() {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "⌚ PLAYTIME");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "Click to view your playtime",
            ChatColor.GRAY + "and statistics!"
        ));
        clock.setItemMeta(meta);
        return clock;
    }

    private ItemStack createRewardsSetupItem() {
        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "⚙ REWARDS SETUP");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "Import all battlepass rewards",
            ChatColor.YELLOW + "(Admin Only)"
        ));
        chest.setItemMeta(meta);
        return chest;
    }

    private ItemStack createStatsItem() {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "📊 STATS");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "Click to view your",
            ChatColor.GRAY + "battlepass statistics!"
        ));
        paper.setItemMeta(meta);
        return paper;
    }

    private ItemStack createNavigationArrow(boolean previous, int currentPage) {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        if (previous) {
            meta.setDisplayName(ChatColor.YELLOW + "◀ Previous Page");
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Go to page " + ChatColor.YELLOW + (currentPage - 1)));
        } else {
            meta.setDisplayName(ChatColor.YELLOW + "Next Page ▶");
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Go to page " + ChatColor.YELLOW + (currentPage + 1)));
        }
        arrow.setItemMeta(meta);
        return arrow;
    }

    private ItemStack createRewardManagerInfo() {
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = star.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "⚙ Battlepass Reward Manager");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━",
            ChatColor.WHITE + "➥ " + ChatColor.YELLOW + "Left Click: " + ChatColor.GRAY + "Set reward text",
            ChatColor.WHITE + "➥ " + ChatColor.YELLOW + "Right Click: " + ChatColor.GRAY + "Set reward items",
            "",
            ChatColor.GRAY + "Click any level below to configure",
            ChatColor.GRAY + "its rewards for players to claim!",
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━"
        ));
        star.setItemMeta(meta);
        return star;
    }

    private ItemStack createRewardSetupItem(int level) {
        RewardManager rewards = plugin.getRewardManager();
        boolean hasText = rewards.hasTextReward(level);
        boolean hasItems = rewards.hasItemReward(level);

        ItemStack item;
        String name;
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━");

        if (hasText || hasItems) {
            if (hasText && hasItems) {
                item = new ItemStack(Material.GOLD_INGOT);
            } else if (hasItems) {
                item = new ItemStack(Material.EMERALD);
            } else {
                item = new ItemStack(Material.GOLD_INGOT);
            }
            name = ChatColor.GOLD + "" + ChatColor.BOLD + "✦ Level " + level;
            lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "✓ CONFIGURED");
            lore.add("");
            lore.add(ChatColor.GRAY + "Text: " + (hasText ? ChatColor.RESET + rewards.getRewardText(level) : ChatColor.GRAY + "None"));
            lore.add(ChatColor.GRAY + "Items: " + (hasItems ? ChatColor.GREEN + "✓ Set" : ChatColor.GRAY + "None"));
        } else {
            item = new ItemStack(Material.COAL);
            name = ChatColor.GRAY + "" + ChatColor.BOLD + "○ Level " + level;
            lore.add(ChatColor.RED + "" + ChatColor.BOLD + "✖ NOT CONFIGURED");
            lore.add("");
            lore.add(ChatColor.GRAY + "Text: " + ChatColor.RED + "Not Set");
            lore.add(ChatColor.GRAY + "Items: " + ChatColor.RED + "Not Set");
        }

        lore.add("");
        lore.add(ChatColor.YELLOW + "▸ Left Click: " + ChatColor.GRAY + (hasText ? "Edit" : "Set") + " text");
        lore.add(ChatColor.YELLOW + "▸ Right Click: " + ChatColor.GRAY + (hasItems ? "Edit" : "Set") + " items");
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━");

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createQuickCommandBook() {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "📋 Quick Command");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━",
            ChatColor.GRAY + "Quick text setup command:",
            "",
            ChatColor.YELLOW + "/bpreward <level> <text>",
            "",
            ChatColor.GRAY + "Example:",
            ChatColor.YELLOW + "/bpreward 5 " + ChatColor.GOLD + "Diamond Sword",
            ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━"
        ));
        book.setItemMeta(meta);
        return book;
    }

    // Getters for GUI state

    public int getPlayerPage(UUID uuid) {
        return playerPages.getOrDefault(uuid, 1);
    }

    public int getRewardSetupPage(UUID uuid) {
        return rewardSetupPages.getOrDefault(uuid, 1);
    }

    public Integer getItemSetupLevel(UUID uuid) {
        return itemSetupLevels.get(uuid);
    }

    public Integer getItemSetupReturnPage(UUID uuid) {
        return itemSetupReturnPages.get(uuid);
    }

    public Integer getEditingRewardLevel(UUID uuid) {
        return editingRewardLevel.get(uuid);
    }

    public void setEditingRewardLevel(UUID uuid, int level) {
        editingRewardLevel.put(uuid, level);
    }

    public void clearEditingRewardLevel(UUID uuid) {
        editingRewardLevel.remove(uuid);
    }

    public void clearItemSetupData(UUID uuid) {
        itemSetupLevels.remove(uuid);
        itemSetupReturnPages.remove(uuid);
    }

    public void clearPlayerData(UUID uuid) {
        playerPages.remove(uuid);
        rewardSetupPages.remove(uuid);
        itemSetupLevels.remove(uuid);
        itemSetupReturnPages.remove(uuid);
        editingRewardLevel.remove(uuid);
    }
}
