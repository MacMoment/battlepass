package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to confirm a full battlepass reset
 */
public class ConfirmResetCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public ConfirmResetCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        
        // Get the ResetAllCommand instance to check pending resets
        ResetAllCommand resetCommand = (ResetAllCommand) plugin.getCommand("resetbattlepassall").getExecutor();
        
        if (!resetCommand.hasPendingReset(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "No reset pending. Use /resetbattlepassall first.");
            return true;
        }

        // Clear pending reset
        resetCommand.clearPendingReset(player.getUniqueId());

        // Perform the reset
        plugin.getDataManager().resetAllData();
        
        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ALL BATTLEPASS DATA HAS BEEN WIPED!");

        return true;
    }
}
