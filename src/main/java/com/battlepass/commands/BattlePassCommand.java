package com.battlepass.commands;

import com.battlepass.BattlePassPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Main BattlePass command - opens the GUI
 */
public class BattlePassCommand implements CommandExecutor {

    private final BattlePassPlugin plugin;

    public BattlePassCommand(BattlePassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        plugin.getGUIManager().openBattlePassGUI(player, page);
        return true;
    }
}
