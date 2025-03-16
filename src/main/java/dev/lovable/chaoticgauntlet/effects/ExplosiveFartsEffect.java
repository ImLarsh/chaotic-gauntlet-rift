
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExplosiveFartsEffect implements GauntletEffect, Listener {
    private final ChaoticGauntlet plugin;
    private Player activePlayer;
    private Location lastLocation;
    private boolean wasOnGround;
    
    public ExplosiveFartsEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Explosive Farts";
    }
    
    @Override
    public int getDuration() {
        return 0; // Continuous effect until next power change
    }
    
    @Override
    public void apply(Player player) {
        this.activePlayer = player;
        this.lastLocation = player.getLocation().clone();
        this.wasOnGround = player.isOnGround();
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        player.sendMessage(ChatColor.RED + "Warning: Jumping causes explosive flatulence!");
    }
    
    @Override
    public void remove(Player player) {
        HandlerList.unregisterAll(this);
        this.activePlayer = null;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().equals(activePlayer)) {
            // Check if player just jumped (was on ground, but isn't now)
            if (wasOnGround && !event.getPlayer().isOnGround()) {
                // Create "fart" explosion when player jumps
                float power = (float) plugin.getConfig().getDouble("powers.explosive-farts.explosion-power", 1.0);
                
                Location explosionLoc = event.getPlayer().getLocation().clone().subtract(0, 0.5, 0);
                event.getPlayer().getWorld().createExplosion(explosionLoc, power, false, true, activePlayer);
                
                // Visual effect
                event.getPlayer().getWorld().spawnParticle(org.bukkit.Particle.SMOKE_NORMAL, explosionLoc, 30, 0.5, 0.5, 0.5, 0.1);
            }
            
            // Update state for next movement check
            wasOnGround = event.getPlayer().isOnGround();
        }
    }
}
