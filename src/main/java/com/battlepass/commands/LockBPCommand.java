package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to lock the battlepass (prevent XP gain)
 */
public class LockBPCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public LockBPCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        plugin.setLocked(true);

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "⚠ BATTLEPASS LOCKED ⚠");
        Bukkit.broadcastMessage(ChatColor.GRAY + "Players can no longer gain XP or level up!");
        Bukkit.broadcastMessage("");

        sender.sendMessage(ChatColor.GREEN + "Successfully locked the battlepass!");
        return true;
    }
}
