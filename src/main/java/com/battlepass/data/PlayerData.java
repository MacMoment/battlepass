package com.battlepass.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a player's battlepass data
 */
public class PlayerData {
    
    private final UUID uuid;
    private int level;
    private double xp;
    private int kills;
    private int blocksMined;
    private Set<Integer> claimedRewards;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.level = 1;
        this.xp = 0;
        this.kills = 0;
        this.blocksMined = 0;
        this.claimedRewards = new HashSet<>();
    }

    public PlayerData(UUID uuid, int level, double xp, int kills, int blocksMined, Set<Integer> claimedRewards) {
        this.uuid = uuid;
        this.level = level;
        this.xp = xp;
        this.kills = kills;
        this.blocksMined = blocksMined;
        this.claimedRewards = claimedRewards != null ? claimedRewards : new HashSet<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public void addLevel(int amount) {
        this.level += amount;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(double xp) {
        this.xp = Math.max(0, xp);
    }

    public void addXp(double amount) {
        this.xp += amount;
    }

    public void subtractXp(double amount) {
        this.xp = Math.max(0, this.xp - amount);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        this.kills++;
    }

    public int getBlocksMined() {
        return blocksMined;
    }

    public void setBlocksMined(int blocksMined) {
        this.blocksMined = blocksMined;
    }

    public void addBlockMined() {
        this.blocksMined++;
    }

    public Set<Integer> getClaimedRewards() {
        return claimedRewards;
    }

    public boolean hasClaimedReward(int level) {
        return claimedRewards.contains(level);
    }

    public void claimReward(int level) {
        claimedRewards.add(level);
    }

    public void resetClaimedRewards() {
        claimedRewards.clear();
    }

    public void reset() {
        this.level = 1;
        this.xp = 0;
        this.kills = 0;
        this.blocksMined = 0;
        this.claimedRewards.clear();
    }
}
