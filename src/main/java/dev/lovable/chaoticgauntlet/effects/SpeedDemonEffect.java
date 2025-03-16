
package dev.lovable.chaoticgauntlet.effects;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedDemonEffect implements GauntletEffect {
    private final ChaoticGauntlet plugin;
    
    public SpeedDemonEffect(ChaoticGauntlet plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "Speed Demon";
    }
    
    @Override
    public int getDuration() {
        return 0; // Continuous effect until next power change
    }
    
    @Override
    public void apply(Player player) {
        int speedLevel = plugin.getConfig().getInt("powers.speed-demon.speed-level", 10);
        
        // Speed effect (amplifier is level - 1)
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speedLevel - 1, false, true, true));
        
        // Add particle trail
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false, true));
        
        player.sendMessage(ChatColor.AQUA + "You've become a speed demon!");
    }
    
    @Override
    public void remove(Player player) {
        if (player.isOnline()) {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.GLOWING);
        }
    }
}
