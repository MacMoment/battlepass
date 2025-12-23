package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import com.battlepass.utils.XPUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Admin commands for managing player battlepass data
 */
public class BPAdminCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public BPAdminCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "give":
                handleGive(sender, args);
                break;
            case "reset":
                handleReset(sender, args);
                break;
            default:
                sendUsage(sender);
                break;
        }

        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /bpadmin give <player> <xp/level> <amount>");
            return;
        }

        String playerName = args[1];
        String type = args[2].toLowerCase();
        int amount;

        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount!");
            return;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(target.getUniqueId());

        if (type.equals("xp")) {
            data.addXp(amount);
            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " XP to " + playerName);
            
            // Check for level up if player is online
            Player onlinePlayer = target.getPlayer();
            if (onlinePlayer != null) {
                XPUtils.checkLevelUp(plugin, onlinePlayer, data);
            }
        } else if (type.equals("level")) {
            data.addLevel(amount);
            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " levels to " + playerName);
        } else {
            sender.sendMessage(ChatColor.RED + "Type must be 'xp' or 'level'!");
            return;
        }

        plugin.getDataManager().savePlayerDataAsync(data);
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /bpadmin reset <player>");
            return;
        }

        String playerName = args[1];
        
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        plugin.getDataManager().resetPlayerData(target.getUniqueId());
        sender.sendMessage(ChatColor.RED + "Reset battlepass data for " + playerName);
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage:");
        sender.sendMessage(ChatColor.GRAY + "/bpadmin give <player> <xp/level> <amount>");
        sender.sendMessage(ChatColor.GRAY + "/bpadmin reset <player>");
        sender.sendMessage(ChatColor.GRAY + "/bpreward <level> <description>");
    }
}
