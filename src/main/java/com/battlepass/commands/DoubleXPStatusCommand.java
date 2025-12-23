package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to check double XP event status
 */
public class DoubleXPStatusCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public DoubleXPStatusCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Double XP Event Status:");
        
        if (plugin.getDoubleXPManager().isDoubleXPActive()) {
            int remaining = plugin.getDoubleXPManager().getRemainingMinutes();
            String timeDisplay = plugin.getDoubleXPManager().formatTime(remaining);
            sender.sendMessage(ChatColor.GRAY + "Active: " + ChatColor.GREEN + "YES");
            sender.sendMessage(ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW + timeDisplay);
        } else {
            sender.sendMessage(ChatColor.GRAY + "Active: " + ChatColor.RED + "NO");
        }

        return true;
    }
}
