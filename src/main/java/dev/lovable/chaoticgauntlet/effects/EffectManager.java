
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EffectManager {
    private final ChaoticGauntlet plugin;
    private BukkitTask mainTask;
    
    @Getter
    private final List<GauntletEffect> availableEffects = new ArrayList<>();
    
    private final Map<UUID, GauntletEffect> activeEffects = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> effectDurations = new ConcurrentHashMap<>();
    
    public EffectManager(ChaoticGauntlet plugin) {
        this.plugin = plugin;
        registerEffects();
        startEffectTask();
    }
    
    private void registerEffects() {
        // Register all available effects
        availableEffects.add(new GravityFlipEffect(plugin));
        availableEffects.add(new AnvilRainEffect(plugin));
        availableEffects.add(new LavaWalkerEffect(plugin));
        availableEffects.add(new MidasTouchEffect(plugin));
        availableEffects.add(new ExplosiveFartsEffect(plugin));
        availableEffects.add(new DimensionGlitchEffect(plugin));
        availableEffects.add(new InvertedControlsEffect(plugin));
        availableEffects.add(new SpeedDemonEffect(plugin));
    }
    
    private void startEffectTask() {
        int interval = plugin.getConfig().getInt("power-change-interval", 60) * 20; // Convert to ticks
        
        mainTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID playerId : plugin.getGauntletManager().getGauntletUsers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline() && plugin.getGauntletManager().isWearingGauntlet(player)) {
                    assignRandomEffect(player);
                }
            }
        }, interval, interval);
    }
    
    public void assignRandomEffect(Player player) {
        // Remove current effect if exists
        removeEffect(player);
        
        // Select a random effect from available effects
        if (!availableEffects.isEmpty()) {
            Random random = new Random();
            GauntletEffect effect = availableEffects.get(random.nextInt(availableEffects.size()));
            
            // Apply the effect
            effect.apply(player);
            activeEffects.put(player.getUniqueId(), effect);
            
            // Notify player
            player.sendMessage(ChatColor.MAGIC + "xxxx " + ChatColor.RESET + 
                    ChatColor.GOLD + "The gauntlet grants you: " + 
                    ChatColor.LIGHT_PURPLE + effect.getName() + 
                    ChatColor.MAGIC + " xxxx");
            
            // Check if it has a duration
            if (effect.getDuration() > 0) {
                effectDurations.put(player.getUniqueId(), effect.getDuration());
                
                // Schedule removal
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (activeEffects.containsKey(player.getUniqueId()) && 
                            activeEffects.get(player.getUniqueId()) == effect) {
                        removeEffect(player);
                    }
                }, effect.getDuration() * 20L); // Convert to ticks
            }
        }
    }
    
    public void removeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        if (activeEffects.containsKey(playerId)) {
            GauntletEffect effect = activeEffects.get(playerId);
            effect.remove(player);
            activeEffects.remove(playerId);
            effectDurations.remove(playerId);
        }
    }
    
    public GauntletEffect getActiveEffect(Player player) {
        return activeEffects.get(player.getUniqueId());
    }
    
    public void shutdown() {
        if (mainTask != null) {
            mainTask.cancel();
        }
        
        // Remove effects from all players
        for (UUID playerId : activeEffects.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                removeEffect(player);
            }
        }
        
        activeEffects.clear();
        effectDurations.clear();
    }
    
    public void reload() {
        shutdown();
        registerEffects();
        startEffectTask();
    }
}
