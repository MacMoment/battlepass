package com.battlepass.managers;

import com.battlepass.BattlePassPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages battlepass rewards - both text descriptions and item rewards
 */
public class RewardManager {

    private final BattlePassPlugin plugin;
    private final Map<Integer, String> rewardTexts;
    private final Map<Integer, List<ItemStack>> rewardItems;
    private File rewardsFile;
    private FileConfiguration rewardsConfig;

    public RewardManager(BattlePassPlugin plugin) {
        this.plugin = plugin;
        this.rewardTexts = new HashMap<>();
        this.rewardItems = new HashMap<>();
        setupRewardsFile();
    }

    private void setupRewardsFile() {
        rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        if (!rewardsFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                rewardsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create rewards.yml!");
                e.printStackTrace();
            }
        }
        rewardsConfig = YamlConfiguration.loadConfiguration(rewardsFile);
    }

    public void loadRewards() {
        rewardTexts.clear();
        rewardItems.clear();

        if (rewardsConfig.getConfigurationSection("rewards") != null) {
            for (String levelStr : rewardsConfig.getConfigurationSection("rewards").getKeys(false)) {
                int level = Integer.parseInt(levelStr);
                String path = "rewards." + levelStr;

                // Load text reward
                if (rewardsConfig.contains(path + ".text")) {
                    rewardTexts.put(level, rewardsConfig.getString(path + ".text"));
                }

                // Load item rewards
                if (rewardsConfig.contains(path + ".items")) {
                    List<?> itemsList = rewardsConfig.getList(path + ".items");
                    if (itemsList != null) {
                        List<ItemStack> items = new ArrayList<>();
                        for (Object obj : itemsList) {
                            if (obj instanceof ItemStack) {
                                items.add((ItemStack) obj);
                            }
                        }
                        if (!items.isEmpty()) {
                            rewardItems.put(level, items);
                        }
                    }
                }
            }
        }
        plugin.getLogger().info("Loaded rewards for " + (rewardTexts.size() + rewardItems.size()) + " levels.");
    }

    public void saveRewards() {
        rewardsConfig = new YamlConfiguration();

        Set<Integer> allLevels = new HashSet<>();
        allLevels.addAll(rewardTexts.keySet());
        allLevels.addAll(rewardItems.keySet());

        for (int level : allLevels) {
            String path = "rewards." + level;

            if (rewardTexts.containsKey(level)) {
                rewardsConfig.set(path + ".text", rewardTexts.get(level));
            }

            if (rewardItems.containsKey(level)) {
                rewardsConfig.set(path + ".items", rewardItems.get(level));
            }
        }

        try {
            rewardsConfig.save(rewardsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save rewards.yml!");
            e.printStackTrace();
        }
    }

    public void setRewardText(int level, String text) {
        rewardTexts.put(level, text);
        saveRewards();
    }

    public String getRewardText(int level) {
        return rewardTexts.get(level);
    }

    public void setRewardItems(int level, List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            rewardItems.remove(level);
        } else {
            rewardItems.put(level, new ArrayList<>(items));
        }
        saveRewards();
    }

    public List<ItemStack> getRewardItems(int level) {
        return rewardItems.getOrDefault(level, Collections.emptyList());
    }

    public boolean hasReward(int level) {
        return rewardTexts.containsKey(level) || rewardItems.containsKey(level);
    }

    public boolean hasTextReward(int level) {
        return rewardTexts.containsKey(level);
    }

    public boolean hasItemReward(int level) {
        return rewardItems.containsKey(level) && !rewardItems.get(level).isEmpty();
    }

    /**
     * Gets a display text for the reward at the given level
     */
    public String getRewardDisplayText(int level) {
        if (rewardTexts.containsKey(level)) {
            return rewardTexts.get(level);
        } else if (rewardItems.containsKey(level)) {
            int count = rewardItems.get(level).size();
            return "§e" + count + (count == 1 ? " Item" : " Items");
        }
        return "";
    }

    public void clearRewardText(int level) {
        rewardTexts.remove(level);
        saveRewards();
    }

    public void clearRewardItems(int level) {
        rewardItems.remove(level);
        saveRewards();
    }

    public void clearAllRewards(int level) {
        rewardTexts.remove(level);
        rewardItems.remove(level);
        saveRewards();
    }
}
