package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GravityFlipEffect implements GauntletEffect {
    private final ChaoticGauntlet plugin;
    private int taskId = -1;
    
    public GravityFlipEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Gravity Flip";
    }
    
    @Override
    public int getDuration() {
        return plugin.getConfig().getInt("powers.gravity-flip.duration", 10);
    }
    
    @Override
    public void apply(Player player) {
        // Launch player into the air
        player.setVelocity(new Vector(0, 3, 0));
        
        // Schedule hovering task
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.isOnline()) {
                // Keep player in the air
                player.setVelocity(new Vector(0, 0.5, 0));
                player.setFallDistance(0); // Prevent fall damage
            }
        }, 20L, 10L).getTaskId();
    }
    
    @Override
    public void remove(Player player) {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        player.setFallDistance(0); // Prevent fall damage when effect ends
    }
}
