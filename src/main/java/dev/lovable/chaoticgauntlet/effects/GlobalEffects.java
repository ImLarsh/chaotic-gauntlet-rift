
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

public class GlobalEffects implements Listener {
    private final ChaoticGauntlet plugin;
    private BukkitTask checkTask;
    
    private BukkitTask meteorTask;
    private BukkitTask creeperTask;
    
    private boolean lightningEffectActive = false;
    
    public GlobalEffects(ChaoticGauntlet plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startCheckTask();
    }
    
    private void startCheckTask() {
        checkTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkForGlobalEffect, 20L, 200L); // Check every 10 seconds
    }
    
    private void checkForGlobalEffect() {
        int minPlayers = plugin.getConfig().getInt("global-effect-min-players", 3);
        
        if (plugin.getGauntletManager().getGauntletUsers().size() >= minPlayers) {
            // Trigger a random global effect
            Random random = new Random();
            int effect = random.nextInt(3);
            
            switch (effect) {
                case 0:
                    if (plugin.getConfig().getBoolean("global-effects.meteor-shower.enabled", true)) {
                        startMeteorShower();
                    }
                    break;
                case 1:
                    if (plugin.getConfig().getBoolean("global-effects.charged-creepers.enabled", true)) {
                        startChargedCreepers();
                    }
                    break;
                case 2:
                    if (plugin.getConfig().getBoolean("global-effects.lightning-break.enabled", true)) {
                        startLightningBreak();
                    }
                    break;
            }
        }
    }
    
    private void startMeteorShower() {
        Bukkit.broadcastMessage(ChatColor.RED + "[Chaotic Gauntlet] " + ChatColor.GOLD + "A meteor shower has begun!");
        
        int duration = plugin.getConfig().getInt("global-effects.meteor-shower.duration", 30);
        int meteorCount = plugin.getConfig().getInt("global-effects.meteor-shower.meteor-count", 20);
        
        Random random = new Random();
        
        meteorTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Find a random player to center the meteor shower on
            if (!plugin.getGauntletManager().getGauntletUsers().isEmpty()) {
                // Get random player with gauntlet
                Player randomPlayer = null;
                for (UUID id : plugin.getGauntletManager().getGauntletUsers()) {
                    Player player = Bukkit.getPlayer(id);
                    if (player != null && player.isOnline()) {
                        randomPlayer = player;
                        break;
                    }
                }
                
                if (randomPlayer != null) {
                    Location center = randomPlayer.getLocation();
                    World world = randomPlayer.getWorld();
                    
                    // Spawn meteors (falling blocks)
                    for (int i = 0; i < 5; i++) { // Spawn 5 meteors per cycle
                        int offsetX = random.nextInt(100) - 50;
                        int offsetZ = random.nextInt(100) - 50;
                        
                        Location meteorLoc = center.clone().add(offsetX, 40, offsetZ);
                        
                        FallingBlock meteor = world.spawnFallingBlock(meteorLoc, Material.MAGMA_BLOCK.createBlockData());
                        meteor.setDropItem(false);
                        meteor.setHurtEntities(true);
                        meteor.setVelocity(new Vector(0, -1, 0));
                        
                        // Add explosion effect when the meteor lands
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (!meteor.isDead()) {
                                world.createExplosion(meteor.getLocation(), 2F, false, true);
                            }
                        }, 40L);
                    }
                }
            }
        }, 0L, 20L);
        
        // Stop meteor shower after duration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (meteorTask != null) {
                meteorTask.cancel();
                meteorTask = null;
                Bukkit.broadcastMessage(ChatColor.RED + "[Chaotic Gauntlet] " + ChatColor.GOLD + "The meteor shower has ended!");
            }
        }, duration * 20L);
    }
    
    private void startChargedCreepers() {
        Bukkit.broadcastMessage(ChatColor.RED + "[Chaotic Gauntlet] " + ChatColor.GOLD + "All mobs are now charged creepers!");
        
        int duration = plugin.getConfig().getInt("global-effects.charged-creepers.duration", 60);
        int creeperCount = plugin.getConfig().getInt("global-effects.charged-creepers.creeper-count", 10);
        
        Random random = new Random();
        
        creeperTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Spawn charged creepers around random players
            for (UUID id : plugin.getGauntletManager().getGauntletUsers()) {
                Player player = Bukkit.getPlayer(id);
                if (player != null && player.isOnline()) {
                    Location playerLoc = player.getLocation();
                    World world = player.getWorld();
                    
                    // Spawn 2-3 creepers per player
                    int spawnCount = random.nextInt(2) + 2;
                    for (int i = 0; i < spawnCount; i++) {
                        int offsetX = random.nextInt(40) - 20;
                        int offsetZ = random.nextInt(40) - 20;
                        
                        Location spawnLoc = playerLoc.clone().add(offsetX, 0, offsetZ);
                        // Find safe Y position
                        spawnLoc.setY(world.getHighestBlockYAt(spawnLoc));
                        
                        Creeper creeper = (Creeper) world.spawnEntity(spawnLoc, EntityType.CREEPER);
                        creeper.setPowered(true);
                    }
                }
            }
        }, 0L, 100L); // Spawn every 5 seconds
        
        // Stop creeper spawning after duration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (creeperTask != null) {
                creeperTask.cancel();
                creeperTask = null;
                Bukkit.broadcastMessage(ChatColor.RED + "[Chaotic Gauntlet] " + ChatColor.GOLD + "The charged creeper invasion has ended!");
            }
        }, duration * 20L);
    }
    
    private void startLightningBreak() {
        Bukkit.broadcastMessage(ChatColor.RED + "[Chaotic Gauntlet] " + ChatColor.GOLD + "Breaking blocks now causes lightning strikes!");
        
        int duration = plugin.getConfig().getInt("global-effects.lightning-break.duration", 30);
        
        lightningEffectActive = true;
        
        // Stop effect after duration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            lightningEffectActive = false;
            Bukkit.broadcastMessage(ChatColor.RED + "[Chaotic Gauntlet] " + ChatColor.GOLD + "The lightning storm has ended!");
        }, duration * 20L);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (lightningEffectActive) {
            // Strike lightning when a block is broken
            event.getBlock().getWorld().strikeLightning(event.getBlock().getLocation());
        }
    }
    
    public void shutdown() {
        if (checkTask != null) {
            checkTask.cancel();
        }
        
        if (meteorTask != null) {
            meteorTask.cancel();
        }
        
        if (creeperTask != null) {
            creeperTask.cancel();
        }
        
        HandlerList.unregisterAll(this);
    }
    
    public void reload() {
        shutdown();
        startCheckTask();
    }
}
