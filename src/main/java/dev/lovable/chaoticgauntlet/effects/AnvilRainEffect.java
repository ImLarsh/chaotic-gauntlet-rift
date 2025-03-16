
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class AnvilRainEffect implements GauntletEffect {
    private final ChaoticGauntlet plugin;
    private BukkitTask anvilTask;
    
    public AnvilRainEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Anvil Rain";
    }
    
    @Override
    public int getDuration() {
        return 0; // Continuous effect until next power change
    }
    
    @Override
    public void apply(Player player) {
        int anvilCount = plugin.getConfig().getInt("powers.anvil-rain.anvil-count", 5);
        int radius = plugin.getConfig().getInt("powers.anvil-rain.radius", 10);
        
        Random random = new Random();
        
        anvilTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                remove(player);
                return;
            }
            
            Location center = player.getLocation();
            
            // Spawn anvils around player
            for (int i = 0; i < anvilCount; i++) {
                int offsetX = random.nextInt(radius * 2) - radius;
                int offsetZ = random.nextInt(radius * 2) - radius;
                
                Location anvilLoc = center.clone().add(offsetX, 15, offsetZ);
                
                FallingBlock anvil = player.getWorld().spawnFallingBlock(anvilLoc, Material.ANVIL.createBlockData());
                anvil.setDropItem(false);
                anvil.setHurtEntities(true);
            }
        }, 0L, 60L); // Every 3 seconds
    }
    
    @Override
    public void remove(Player player) {
        if (anvilTask != null) {
            anvilTask.cancel();
            anvilTask = null;
        }
    }
}
