package com.battlepass;

import com.battlepass.commands.*;
import com.battlepass.gui.GUIManager;
import com.battlepass.listeners.BlockBreakListener;
import com.battlepass.listeners.GUIListener;
import com.battlepass.listeners.PlayerDeathListener;
import com.battlepass.listeners.PlayerJoinListener;
import com.battlepass.managers.DataManager;
import com.battlepass.managers.DoubleXPManager;
import com.battlepass.managers.RewardManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for BattlePass
 * A comprehensive battlepass system with XP, levels, rewards, and GUI
 */
public class BattlePassPlugin extends JavaPlugin {

    private static BattlePassPlugin instance;
    private DataManager dataManager;
    private RewardManager rewardManager;
    private DoubleXPManager doubleXPManager;
    private GUIManager guiManager;
    private ResetAllCommand resetAllCommand;
    private boolean locked = false;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        this.dataManager = new DataManager(this);
        this.rewardManager = new RewardManager(this);
        this.doubleXPManager = new DoubleXPManager(this);
        this.guiManager = new GUIManager(this);
        
        // Load data
        dataManager.loadAllData();
        rewardManager.loadRewards();
        
        // Register listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        getLogger().info("BattlePass plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all data
        if (dataManager != null) {
            dataManager.saveAllData();
        }
        if (rewardManager != null) {
            rewardManager.saveRewards();
        }
        
        getLogger().info("BattlePass plugin has been disabled!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    private void registerCommands() {
        getCommand("battlepass").setExecutor(new BattlePassCommand(this));
        getCommand("doublexpevent").setExecutor(new DoubleXPEventCommand(this));
        getCommand("stopdoublexp").setExecutor(new StopDoubleXPCommand(this));
        getCommand("doublexpstatus").setExecutor(new DoubleXPStatusCommand(this));
        getCommand("bprewardsetup").setExecutor(new RewardSetupCommand(this));
        getCommand("bpreward").setExecutor(new SetRewardCommand(this));
        getCommand("bpadmin").setExecutor(new BPAdminCommand(this));
        
        // Store reference to ResetAllCommand for use by ConfirmResetCommand
        this.resetAllCommand = new ResetAllCommand(this);
        getCommand("resetbattlepassall").setExecutor(resetAllCommand);
        getCommand("confirmresetall").setExecutor(new ConfirmResetCommand(this));
        
        getCommand("bptop").setExecutor(new BPTopCommand(this));
        getCommand("lockbp").setExecutor(new LockBPCommand(this));
        getCommand("unlockbp").setExecutor(new UnlockBPCommand(this));
        getCommand("bpstatus").setExecutor(new BPStatusCommand(this));
        getCommand("bppickaxe").setExecutor(new BPPickaxeCommand(this));
    }

    public static BattlePassPlugin getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public DoubleXPManager getDoubleXPManager() {
        return doubleXPManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public ResetAllCommand getResetAllCommand() {
        return resetAllCommand;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
