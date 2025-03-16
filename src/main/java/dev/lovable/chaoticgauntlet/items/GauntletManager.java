
package dev.lovable.chaoticgauntlet.items;

import dev.lovable.chaoticgauntlet.ChaoticGauntlet;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GauntletManager {
    private final ChaoticGauntlet plugin;
    private final NamespacedKey gauntletKey;
    
    @Getter
    private final Set<UUID> gauntletUsers = ConcurrentHashMap.newKeySet();
    
    public GauntletManager(ChaoticGauntlet plugin) {
        this.plugin = plugin;
        this.gauntletKey = new NamespacedKey(plugin, "chaotic_gauntlet");
    }
    
    public ItemStack createGauntlet() {
        ItemStack gauntlet = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = gauntlet.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Chaotic Gauntlet");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "A legendary artifact of unpredictable power");
            lore.add("");
            lore.add(ChatColor.RED + "Warning: " + ChatColor.WHITE + "Effects change every 60 seconds");
            lore.add(ChatColor.DARK_PURPLE + "✧ Wear at your own risk! ✧");
            
            meta.setLore(lore);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            // Mark item as gauntlet
            meta.getPersistentDataContainer().set(gauntletKey, PersistentDataType.BYTE, (byte) 1);
            
            gauntlet.setItemMeta(meta);
        }
        
        return gauntlet;
    }
    
    public boolean isGauntlet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(gauntletKey, PersistentDataType.BYTE);
    }
    
    public void giveGauntlet(Player player) {
        player.getInventory().addItem(createGauntlet());
        player.sendMessage(ChatColor.GOLD + "You have received the Chaotic Gauntlet!");
    }
    
    public void registerGauntletUser(Player player) {
        gauntletUsers.add(player.getUniqueId());
    }
    
    public void unregisterGauntletUser(Player player) {
        gauntletUsers.remove(player.getUniqueId());
    }
    
    public boolean isWearingGauntlet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        return isGauntlet(helmet);
    }
    
    public void reload() {
        // Nothing to reload currently
    }
}
