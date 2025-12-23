package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to stop a double XP event
 */
public class StopDoubleXPCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public StopDoubleXPCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (!plugin.getDoubleXPManager().isDoubleXPActive()) {
            sender.sendMessage(ChatColor.RED + "No double XP event is currently active!");
            return true;
        }

        plugin.getDoubleXPManager().stopDoubleXP();
        return true;
    }
}
