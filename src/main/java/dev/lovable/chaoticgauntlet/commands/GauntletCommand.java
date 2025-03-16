
package dev.lovable.chaoticgauntlet.commands;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GauntletCommand implements CommandExecutor {
    private final ChaoticGauntlet plugin;
    
    public GauntletCommand(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "give":
                if (!sender.hasPermission("chaoticgauntlet.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                
                Player target;
                if (args.length > 1) {
                    target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                        return true;
                    }
                } else if (sender instanceof Player) {
                    target = (Player) sender;
                } else {
                    sender.sendMessage(ChatColor.RED + "Please specify a player.");
                    return true;
                }
                
                plugin.getGauntletManager().giveGauntlet(target);
                sender.sendMessage(ChatColor.GREEN + "Gave the Chaotic Gauntlet to " + target.getName());
                return true;
                
            case "reload":
                if (!sender.hasPermission("chaoticgauntlet.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "ChaoticGauntlet configuration reloaded.");
                return true;
                
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===== Chaotic Gauntlet Commands =====");
        if (sender.hasPermission("chaoticgauntlet.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/gauntlet give [player]" + ChatColor.GRAY + " - Give a gauntlet to yourself or another player.");
            sender.sendMessage(ChatColor.YELLOW + "/gauntlet reload" + ChatColor.GRAY + " - Reload the plugin configuration.");
        }
    }
}
