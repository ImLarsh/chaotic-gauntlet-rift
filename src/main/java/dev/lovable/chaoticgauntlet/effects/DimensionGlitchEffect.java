
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class DimensionGlitchEffect implements GauntletEffect {
    private final ChaoticGauntlet plugin;
    private BukkitTask teleportTask;
    
    public DimensionGlitchEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Dimension Glitch";
    }
    
    @Override
    public int getDuration() {
        return 0; // Continuous effect until next power change
    }
    
    @Override
    public void apply(Player player) {
        int teleportDistance = plugin.getConfig().getInt("powers.dimension-glitch.teleport-distance", 50);
        
        teleportTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                remove(player);
                return;
            }
            
            Random random = new Random();
            Location currentLoc = player.getLocation();
            
            // Calculate random direction
            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = random.nextInt(teleportDistance) + 10; // Between 10 and max distance
            
            // Calculate new position
            int newX = currentLoc.getBlockX() + (int)(Math.cos(angle) * distance);
            int newZ = currentLoc.getBlockZ() + (int)(Math.sin(angle) * distance);
            
            // Find safe Y position
            Location tpLoc = new Location(player.getWorld(), newX, 0, newZ);
            tpLoc.setY(player.getWorld().getHighestBlockYAt(newX, newZ) + 1);
            
            // Visual effect before teleport
            player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, player.getLocation(), 50, 0.5, 1, 0.5, 0.1);
            
            // Teleport player
            player.teleport(tpLoc);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Reality glitches around you!");
            
            // Visual effect after teleport
            player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, tpLoc, 50, 0.5, 1, 0.5, 0.1);
            
        }, 100L, 200L); // Every 10 seconds
    }
    
    @Override
    public void remove(Player player) {
        if (teleportTask != null) {
            teleportTask.cancel();
            teleportTask = null;
        }
    }
}
