
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class LavaWalkerEffect implements GauntletEffect {
    private final ChaoticGauntlet plugin;
    private BukkitTask lavaTask;
    private final List<Location> changedBlocks = new ArrayList<>();
    
    public LavaWalkerEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Lava Walker";
    }
    
    @Override
    public int getDuration() {
        return 0; // Continuous effect until next power change
    }
    
    @Override
    public void apply(Player player) {
        int radius = plugin.getConfig().getInt("powers.lava-walker.radius", 2);
        int duration = plugin.getConfig().getInt("powers.lava-walker.duration", 5);
        
        lavaTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                remove(player);
                return;
            }
            
            Location playerLoc = player.getLocation();
            
            // Turn blocks under and around player into lava
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = playerLoc.getBlock().getRelative(x, -1, z);
                    if (block.getType().isSolid() && block.getType() != Material.BEDROCK && 
                            block.getType() != Material.LAVA && block.getType() != Material.AIR) {
                        changedBlocks.add(block.getLocation().clone());
                        block.setType(Material.LAVA);
                        
                        // Schedule block restoration
                        plugin.getServer().getScheduler().runTaskLater(plugin, 
                            () -> {
                                if (block.getType() == Material.LAVA) {
                                    block.setType(Material.STONE);
                                }
                            }, duration * 20L);
                    }
                }
            }
        }, 0L, 10L);
    }
    
    @Override
    public void remove(Player player) {
        if (lavaTask != null) {
            lavaTask.cancel();
            lavaTask = null;
        }
        
        // Restore any remaining lava blocks to stone
        for (Location loc : changedBlocks) {
            Block block = loc.getBlock();
            if (block.getType() == Material.LAVA) {
                block.setType(Material.STONE);
            }
        }
        changedBlocks.clear();
    }
}
