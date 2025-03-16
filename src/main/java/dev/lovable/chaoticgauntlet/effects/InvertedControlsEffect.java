
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class InvertedControlsEffect implements GauntletEffect, Listener {
    private final ChaoticGauntlet plugin;
    private Player activePlayer;
    
    public InvertedControlsEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Inverted Controls";
    }
    
    @Override
    public int getDuration() {
        return plugin.getConfig().getInt("powers.inverted-controls.duration", 15);
    }
    
    @Override
    public void apply(Player player) {
        this.activePlayer = player;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        player.sendMessage(ChatColor.BLUE + "Your controls have been inverted!");
    }
    
    @Override
    public void remove(Player player) {
        HandlerList.unregisterAll(this);
        this.activePlayer = null;
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "Your controls are back to normal.");
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().equals(activePlayer) && !event.isCancelled()) {
            // Only invert if player is actually trying to move (not just look around)
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getZ() != event.getTo().getZ()) {
                
                // Calculate movement vector
                Vector movement = event.getTo().toVector().subtract(event.getFrom().toVector());
                
                // Invert movement
                movement.multiply(-1.5); // Multiply by -1 to invert, and 1.5 to exaggerate
                
                // Apply inverted movement
                Location newLoc = event.getFrom().clone().add(movement);
                newLoc.setYaw(event.getTo().getYaw());
                newLoc.setPitch(event.getTo().getPitch());
                
                // Teleport instead of cancelling to avoid client-server desync
                event.getPlayer().teleport(newLoc);
            }
        }
    }
}
