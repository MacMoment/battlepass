package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to unlock the battlepass
 */
public class UnlockBPCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public UnlockBPCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        plugin.setLocked(false);

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "✓ BATTLEPASS UNLOCKED ✓");
        Bukkit.broadcastMessage(ChatColor.GRAY + "Players can now gain XP and level up again!");
        Bukkit.broadcastMessage("");

        sender.sendMessage(ChatColor.GREEN + "Successfully unlocked the battlepass!");
        return true;
    }
}
