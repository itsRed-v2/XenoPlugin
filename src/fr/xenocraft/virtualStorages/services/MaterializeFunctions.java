package fr.xenocraft.virtualStorages.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import fr.xenocraft.Main;

public class MaterializeFunctions {
	
	public static void materialize(Main plugin, Player p, int item_index, int amount, String network) {
		
		ConfigurationSection virtualStorages = plugin.getCustomConfig().getConfigurationSection("virtualStorages");
		if (!virtualStorages.contains(network)) {
			p.sendMessage("§cERROR: The specified network doesn't exist");
			return;
		}
		
		ConfigurationSection materializers_list = plugin.getConfig().getConfigurationSection("materializers");
		if (!materializers_list.contains(network)) {
			p.sendMessage("§cERROR: No materializer matches with this network");
			return;
		}
		
		ConfigurationSection materializer = materializers_list.getConfigurationSection(network);
		List<Double> coords = materializer.getDoubleList("position");
		Location loc = new Location(Bukkit.getWorld(materializer.getString("world")), coords.get(0), coords.get(1), coords.get(2));
		
		loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 2, 2);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> config_items = (List<Map<String, Object>>) virtualStorages.getList(network);
		
		ItemStack item = ItemStack.deserialize((Map<String, Object>) config_items.get(item_index));
		
		int maxStackSize = item.getMaxStackSize();
		
		item.setAmount(item.getAmount() - amount);
		
		if (item.getAmount() == 0) {
			config_items.remove(item_index);
		} else {
			Map<String, Object> new_map = item.serialize();
			config_items.set(item_index, new_map);
		}

		plugin.saveCustomConfig();
		
		for (; amount > 0;) {
			if (amount > maxStackSize) {
				item.setAmount(maxStackSize);
				amount -= maxStackSize;
				
				Item dropItem = loc.getWorld().dropItem(loc, item);
				dropItem.setVelocity(new Vector());
			} else {
				item.setAmount(amount);
				amount = 0;
				
				Item dropItem = loc.getWorld().dropItem(loc, item);
				dropItem.setVelocity(new Vector());
			}
		}
	}
	
	
	
	public static boolean dematerialize(Main plugin, Player p, String network) {
		
		ConfigurationSection virtualStorages = plugin.getCustomConfig().getConfigurationSection("virtualStorages");
		if (!virtualStorages.contains(network)) {
			p.sendMessage("§cERROR: The specified network doesn't exist");
			return false;
		}
		
		ConfigurationSection materializers_list = plugin.getConfig().getConfigurationSection("materializers");
		if (!materializers_list.contains(network)) {
			p.sendMessage("§cERROR: No materializer matches with this network");
			return false;
		}
		
		ConfigurationSection materializer = materializers_list.getConfigurationSection(network);
		List<Double> coords = materializer.getDoubleList("position");
		Location loc = new Location(Bukkit.getWorld(materializer.getString("world")), coords.get(0), coords.get(1), coords.get(2));
		
		Collection<Entity> items = loc.getWorld().getNearbyEntities(loc,.5,.5,.5, (e) -> e.getType() == EntityType.DROPPED_ITEM );
		
		if (items.size() == 0) return false;
		
		loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_DEACTIVATE, 2, 2);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> config_items = (List<Map<String, Object>>) virtualStorages.getList(network);
		
		for (Entity entity : items) {
			entity.remove();
			
			Item item_entity = (Item) entity;
			ItemStack item = item_entity.getItemStack();
			
			boolean stop = false;
			
			for (int i = 0; i < config_items.size() && stop == false; i++) {
				
				Map<String, Object> map = config_items.get(i);
				ItemStack compare_item = ItemStack.deserialize(map);

				if (item.isSimilar(compare_item)) {
				
					compare_item.setAmount(item.getAmount() + compare_item.getAmount());
					
					Map<String, Object> new_map = compare_item.serialize();
					config_items.set(i, new_map);
					
					stop = true;
				}
			}
			if (stop == false) {
				config_items.add(item.serialize());
			}
		}
		
		plugin.saveCustomConfig();
		
		return true;
	}

}
