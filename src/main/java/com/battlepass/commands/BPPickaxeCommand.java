package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import com.battlepass.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Command to give/get the special BattlePass pickaxe
 */
public class BPPickaxeCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public BPPickaxeCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!sender.hasPermission("battlepass.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        Player player = (Player) sender;
        Player target = player;

        // Check if giving to another player
        if (args.length > 0) {
            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
        }

        ItemStack pickaxe = ItemUtils.createSpecialPickaxe();
        target.getInventory().addItem(pickaxe);

        if (target.equals(player)) {
            sender.sendMessage(ChatColor.GREEN + "You received the " + ChatColor.GOLD + "BattlePass Pickaxe" + 
                             ChatColor.GREEN + "!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Gave the " + ChatColor.GOLD + "BattlePass Pickaxe" + 
                             ChatColor.GREEN + " to " + target.getName() + "!");
            target.sendMessage(ChatColor.GREEN + "You received the " + ChatColor.GOLD + "BattlePass Pickaxe" + 
                             ChatColor.GREEN + " from " + player.getName() + "!");
        }

        return true;
    }
}
