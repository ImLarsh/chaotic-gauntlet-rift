
package dev.lovable.chaoticgauntlet.effects;

import org.bukkit.entity.Player;

public interface GauntletEffect {
    /**
     * Get the name of the effect
     * @return The effect name
     */
    String getName();
    
    /**
     * Get the duration of the effect in seconds
     * @return Duration in seconds, or 0 if permanent until next effect
     */
    int getDuration();
    
    /**
     * Apply the effect to a player
     * @param player The player to apply the effect to
     */
    void apply(Player player);
    
    /**
     * Remove the effect from a player
     * @param player The player to remove the effect from
     */
    void remove(Player player);
}
