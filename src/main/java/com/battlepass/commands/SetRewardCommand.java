package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to set reward text via command line
 */
public class SetRewardCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public SetRewardCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /bpreward <level> <text>");
            sender.sendMessage(ChatColor.GRAY + "Example: /bpreward 5 " + ChatColor.GOLD + "Diamond Sword");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid level number!");
            return true;
        }

        if (level < 1 || level > 70) {
            sender.sendMessage(ChatColor.RED + "Level must be between 1 and 70!");
            return true;
        }

        // Combine remaining args as reward text
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) textBuilder.append(" ");
            textBuilder.append(args[i]);
        }

        String rewardText = ChatColor.translateAlternateColorCodes('&', textBuilder.toString());
        plugin.getRewardManager().setRewardText(level, rewardText);

        sender.sendMessage(ChatColor.GREEN + "Set level " + level + " reward to: " + rewardText);
        return true;
    }
}
