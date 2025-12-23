package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Command to initiate a full battlepass reset
 */
public class ResetAllCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;
    private final Map<UUID, Long> pendingResets = new HashMap<>();
    private static final long CONFIRM_TIMEOUT = 10000; // 10 seconds

    public ResetAllCommand(BattlePassPlugin plugin) {
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
        UUID uuid = player.getUniqueId();

        pendingResets.put(uuid, System.currentTimeMillis());

        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "WARNING: " + 
                         ChatColor.GRAY + "This will wipe ALL battlepass data for ALL players!");
        sender.sendMessage(ChatColor.GRAY + "Type " + ChatColor.RED + "/confirmresetall " + 
                         ChatColor.GRAY + "to confirm or wait 10 seconds to cancel.");

        // Schedule timeout removal
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Long timestamp = pendingResets.get(uuid);
            if (timestamp != null && System.currentTimeMillis() - timestamp >= CONFIRM_TIMEOUT) {
                pendingResets.remove(uuid);
                player.sendMessage(ChatColor.GREEN + "Reset cancelled (timed out)");
            }
        }, 200L); // 10 seconds = 200 ticks

        return true;
    }

    public boolean hasPendingReset(UUID uuid) {
        Long timestamp = pendingResets.get(uuid);
        if (timestamp == null) return false;
        
        if (System.currentTimeMillis() - timestamp >= CONFIRM_TIMEOUT) {
            pendingResets.remove(uuid);
            return false;
        }
        
        return true;
    }

    public void clearPendingReset(UUID uuid) {
        pendingResets.remove(uuid);
    }
}
