
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MidasTouchEffect implements GauntletEffect, Listener {
    private final ChaoticGauntlet plugin;
    private Player activePlayer;
    
    public MidasTouchEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Midas Touch";
    }
    
    @Override
    public int getDuration() {
        return plugin.getConfig().getInt("powers.midas-touch.duration", 30);
    }
    
    @Override
    public void apply(Player player) {
        this.activePlayer = player;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        player.sendMessage(ChatColor.GOLD + "Everything you touch turns to gold!");
    }
    
    @Override
    public void remove(Player player) {
        HandlerList.unregisterAll(this);
        this.activePlayer = null;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().equals(activePlayer) && event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            
            // Skip certain blocks that shouldn't be transformed
            if (block.getType() != Material.BEDROCK && !block.getType().toString().contains("AIR") && 
                    !block.getType().toString().contains("WATER") && !block.getType().toString().contains("LAVA")) {
                block.setType(Material.GOLD_BLOCK);
            }
        }
    }
}
