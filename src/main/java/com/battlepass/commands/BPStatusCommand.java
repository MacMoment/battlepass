package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to check battlepass status
 */
public class BPStatusCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public BPStatusCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Battlepass Status:");
        
        // Lock status
        if (plugin.isLocked()) {
            sender.sendMessage(ChatColor.GRAY + "Status: " + ChatColor.RED + "LOCKED");
        } else {
            sender.sendMessage(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "UNLOCKED");
        }

        // Double XP status
        if (plugin.getDoubleXPManager().isDoubleXPActive()) {
            int remaining = plugin.getDoubleXPManager().getRemainingMinutes();
            String timeDisplay = plugin.getDoubleXPManager().formatTime(remaining);
            sender.sendMessage(ChatColor.GRAY + "Double XP: " + ChatColor.GREEN + "ACTIVE " + 
                             ChatColor.GRAY + "(" + timeDisplay + " remaining)");
        } else {
            sender.sendMessage(ChatColor.GRAY + "Double XP: " + ChatColor.RED + "INACTIVE");
        }

        return true;
    }
}
