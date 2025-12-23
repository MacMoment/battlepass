package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import com.battlepass.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command to show the top battlepass players
 */
public class BPTopCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public BPTopCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "                                        ");
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "BATTLEPASS TOP 5 " + 
                         ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + 
                         ChatColor.GRAY + "" + ChatColor.BOLD + "LEVELS" + 
                         ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "]");
        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "                                        ");

        List<PlayerData> topPlayers = plugin.getDataManager().getTopPlayers(5);

        if (topPlayers.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "No players found!");
        } else {
            int rank = 1;
            for (PlayerData data : topPlayers) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(data.getUuid());
                String playerName = player.getName() != null ? player.getName() : "Unknown";
                
                sender.sendMessage(ChatColor.GRAY + "#" + rank + " " + playerName + " " + 
                                 ChatColor.DARK_GRAY + "» " + 
                                 ChatColor.YELLOW + "Level " + ChatColor.GOLD + data.getLevel() + " " + 
                                 ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + (int) data.getXp() + " XP" + 
                                 ChatColor.DARK_GRAY + ")");
                rank++;
            }
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "                                        ");
        return true;
    }
}
