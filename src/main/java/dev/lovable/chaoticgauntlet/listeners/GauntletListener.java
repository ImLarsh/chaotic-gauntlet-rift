
package dev.lovable.chaoticgauntlet.listeners;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GauntletListener implements Listener {
    private final ChaoticGauntlet plugin;
    
    public GauntletListener(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is wearing the gauntlet
        if (plugin.getGauntletManager().isWearingGauntlet(player)) {
            plugin.getGauntletManager().registerGauntletUser(player);
            plugin.getEffectManager().assignRandomEffect(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove effects and unregister player
        plugin.getEffectManager().removeEffect(player);
        plugin.getGauntletManager().unregisterGauntletUser(player);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // Check if this is a helmet slot operation
        if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR && event.getRawSlot() == 5) {
            ItemStack currentHelmet = event.getCurrentItem();
            ItemStack cursor = event.getCursor();
            
            // Player is putting on the gauntlet
            if (currentHelmet == null || currentHelmet.getType() == Material.AIR) {
                if (plugin.getGauntletManager().isGauntlet(cursor)) {
                    player.sendMessage(ChatColor.GOLD + "You feel chaotic energy flowing through you as you put on the gauntlet!");
                    
                    // Schedule this for next tick to ensure helmet is equipped
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getGauntletManager().registerGauntletUser(player);
                        plugin.getEffectManager().assignRandomEffect(player);
                    });
                }
            }
            // Player is removing the gauntlet
            else if (plugin.getGauntletManager().isGauntlet(currentHelmet)) {
                player.sendMessage(ChatColor.GOLD + "The chaotic energy dissipates as you remove the gauntlet.");
                
                plugin.getEffectManager().removeEffect(player);
                plugin.getGauntletManager().unregisterGauntletUser(player);
            }
        }
        
        // Also check if player is shift-clicking a gauntlet onto their head
        else if (event.isShiftClick() && plugin.getGauntletManager().isGauntlet(event.getCurrentItem())) {
            PlayerInventory inv = player.getInventory();
            
            if (inv.getHelmet() == null || inv.getHelmet().getType() == Material.AIR) {
                // Schedule this for next tick to ensure helmet is equipped
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (plugin.getGauntletManager().isWearingGauntlet(player)) {
                        player.sendMessage(ChatColor.GOLD + "You feel chaotic energy flowing through you as you put on the gauntlet!");
                        plugin.getGauntletManager().registerGauntletUser(player);
                        plugin.getEffectManager().assignRandomEffect(player);
                    }
                });
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Check for PvP mode - transfer gauntlet on hit
        if (plugin.getConfig().getBoolean("pvp-mode-enabled", true)) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player victim = (Player) event.getEntity();
                Player attacker = (Player) event.getDamager();
                
                // Check if victim has the gauntlet
                if (plugin.getGauntletManager().isWearingGauntlet(victim)) {
                    // 20% chance to transfer
                    if (Math.random() < 0.2) {
                        // Remove from victim
                        ItemStack gauntlet = victim.getInventory().getHelmet();
                        victim.getInventory().setHelmet(null);
                        plugin.getEffectManager().removeEffect(victim);
                        plugin.getGauntletManager().unregisterGauntletUser(victim);
                        
                        victim.sendMessage(ChatColor.RED + "The gauntlet has been knocked off your head!");
                        
                        // Give to attacker if they don't have a helmet
                        if (attacker.getInventory().getHelmet() == null || 
                                attacker.getInventory().getHelmet().getType() == Material.AIR) {
                            attacker.getInventory().setHelmet(gauntlet);
                            attacker.sendMessage(ChatColor.GOLD + "The gauntlet flies onto your head!");
                            
                            plugin.getGauntletManager().registerGauntletUser(attacker);
                            plugin.getEffectManager().assignRandomEffect(attacker);
                        } else {
                            // Drop the gauntlet if attacker already has a helmet
                            victim.getWorld().dropItemNaturally(victim.getLocation(), gauntlet);
                            attacker.sendMessage(ChatColor.GOLD + "The gauntlet falls to the ground!");
                        }
                    }
                }
            }
        }
    }
}
