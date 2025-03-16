
package dev.lovable.chaoticgauntlet;

import dev.lovable.chaoticgauntlet.commands.GauntletCommand;
import dev.lovable.chaoticgauntlet.effects.EffectManager;
import dev.lovable.chaoticgauntlet.effects.GlobalEffects;
import dev.lovable.chaoticgauntlet.items.GauntletManager;
import dev.lovable.chaoticgauntlet.listeners.GauntletListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChaoticGauntlet extends JavaPlugin {
    
    @Getter
    private static ChaoticGauntlet instance;
    
    @Getter
    private GauntletManager gauntletManager;
    
    @Getter
    private EffectManager effectManager;
    
    @Getter
    private GlobalEffects globalEffects;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        this.gauntletManager = new GauntletManager(this);
        this.effectManager = new EffectManager(this);
        this.globalEffects = new GlobalEffects(this);
        
        // Register commands
        getCommand("gauntlet").setExecutor(new GauntletCommand(this));
        
        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new GauntletListener(this), this);
        
        getLogger().info("ChaoticGauntlet has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Clean up tasks
        if (effectManager != null) {
            effectManager.shutdown();
        }
        
        if (globalEffects != null) {
            globalEffects.shutdown();
        }
        
        getLogger().info("ChaoticGauntlet has been disabled!");
    }
    
    public void reload() {
        reloadConfig();
        
        // Reload managers
        if (effectManager != null) {
            effectManager.reload();
        }
        
        if (globalEffects != null) {
            globalEffects.reload();
        }
        
        if (gauntletManager != null) {
            gauntletManager.reload();
        }
    }
}
