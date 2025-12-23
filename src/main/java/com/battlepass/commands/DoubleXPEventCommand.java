package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to start a double XP event
 */
public class DoubleXPEventCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public DoubleXPEventCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            if (plugin.getDoubleXPManager().isDoubleXPActive()) {
                sender.sendMessage(ChatColor.RED + "Double XP event is already active!");
                sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/doublexpstatus " + 
                                 ChatColor.GRAY + "to check remaining time");
                sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/stopdoublexp " + 
                                 ChatColor.GRAY + "to end it early");
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /doublexpevent <time>");
                sender.sendMessage(ChatColor.GRAY + "Examples:");
                sender.sendMessage(ChatColor.YELLOW + "  /doublexpevent 30m " + ChatColor.GRAY + "- 30 minutes");
                sender.sendMessage(ChatColor.YELLOW + "  /doublexpevent 1h " + ChatColor.GRAY + "- 1 hour");
                sender.sendMessage(ChatColor.YELLOW + "  /doublexpevent 2h30m " + ChatColor.GRAY + "- 2 hours 30 minutes");
            }
            return true;
        }

        String timeInput = args[0];
        int minutes = plugin.getDoubleXPManager().parseTime(timeInput);

        if (minutes <= 0) {
            sender.sendMessage(ChatColor.RED + "Invalid format! Use formats like: 30m, 1h, 2h30m");
            return true;
        }

        if (minutes > 1440) {
            sender.sendMessage(ChatColor.RED + "Maximum duration is 24 hours (1440 minutes)!");
            return true;
        }

        if (plugin.getDoubleXPManager().isDoubleXPActive()) {
            sender.sendMessage(ChatColor.RED + "Double XP event is already active!");
            return true;
        }

        boolean started = plugin.getDoubleXPManager().startDoubleXP(minutes);
        
        if (!started) {
            sender.sendMessage(ChatColor.RED + "Failed to start double XP event!");
        }

        return true;
    }
}
