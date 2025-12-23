package com.battlepass.managers;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player data storage and retrieval
 */
public class DataManager {

    private final BattlePassPlugin plugin;
    private final Map<UUID, PlayerData> playerDataCache;
    private File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(BattlePassPlugin plugin) {
        this.plugin = plugin;
        this.playerDataCache = new ConcurrentHashMap<>();
        setupDataFile();
    }

    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml!");
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadAllData() {
        if (dataConfig.getConfigurationSection("players") == null) {
            return;
        }

        for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            loadPlayerData(uuid);
        }
        plugin.getLogger().info("Loaded data for " + playerDataCache.size() + " players.");
    }

    public void loadPlayerData(UUID uuid) {
        String path = "players." + uuid.toString();
        
        if (!dataConfig.contains(path)) {
            playerDataCache.put(uuid, new PlayerData(uuid));
            return;
        }

        int level = dataConfig.getInt(path + ".level", 1);
        double xp = dataConfig.getDouble(path + ".xp", 0);
        int kills = dataConfig.getInt(path + ".kills", 0);
        int blocksMined = dataConfig.getInt(path + ".blocksMined", 0);
        
        Set<Integer> claimedRewards = new HashSet<>();
        List<Integer> claimedList = dataConfig.getIntegerList(path + ".claimedRewards");
        claimedRewards.addAll(claimedList);

        PlayerData data = new PlayerData(uuid, level, xp, kills, blocksMined, claimedRewards);
        playerDataCache.put(uuid, data);
    }

    public void saveAllData() {
        for (PlayerData data : playerDataCache.values()) {
            savePlayerData(data);
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
            e.printStackTrace();
        }
        plugin.getLogger().info("Saved data for " + playerDataCache.size() + " players.");
    }

    public void savePlayerData(PlayerData data) {
        String path = "players." + data.getUuid().toString();
        
        dataConfig.set(path + ".level", data.getLevel());
        dataConfig.set(path + ".xp", data.getXp());
        dataConfig.set(path + ".kills", data.getKills());
        dataConfig.set(path + ".blocksMined", data.getBlocksMined());
        dataConfig.set(path + ".claimedRewards", new ArrayList<>(data.getClaimedRewards()));
    }

    public void savePlayerDataAsync(PlayerData data) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            savePlayerData(data);
            try {
                dataConfig.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save playerdata.yml!");
            }
        });
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.computeIfAbsent(uuid, PlayerData::new);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public void initializePlayer(Player player) {
        if (!playerDataCache.containsKey(player.getUniqueId())) {
            playerDataCache.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        }
    }

    public void resetAllData() {
        playerDataCache.clear();
        dataConfig = new YamlConfiguration();
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not reset playerdata.yml!");
        }
    }

    public void resetPlayerData(UUID uuid) {
        PlayerData data = getPlayerData(uuid);
        data.reset();
        savePlayerDataAsync(data);
    }

    public List<PlayerData> getTopPlayers(int limit) {
        List<PlayerData> allPlayers = new ArrayList<>(playerDataCache.values());
        allPlayers.sort((a, b) -> {
            int levelCompare = Integer.compare(b.getLevel(), a.getLevel());
            if (levelCompare != 0) return levelCompare;
            return Double.compare(b.getXp(), a.getXp());
        });
        return allPlayers.subList(0, Math.min(limit, allPlayers.size()));
    }

    public Map<UUID, PlayerData> getAllPlayerData() {
        return Collections.unmodifiableMap(playerDataCache);
    }
}
