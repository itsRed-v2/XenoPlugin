package fr.xenocraft.deathRay.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;

public class Modules implements Listener {

	private Main plugin;
	public static ItemStack fireModule;
	public static ItemStack powerModule;
	public static ItemStack efficiencyModule;

	public Modules(Main plugin) {
		this.plugin = plugin;
	}

	public void init() {
		createFireModule();
		createPowerModule();
		createEfficiencyModule();
	}

	private void createFireModule() {
		ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName("§6Fire module");

		List<String> lore = new ArrayList<String>();
		lore.add("§eDeath Ray upgrade");
		lore.add("§7› §6Explosions set fire");
		lore.add("§7› §c+0.5 §bConsumption multiplier");
		lore.add("§7This effect does not stack");
		meta.setLore(lore);

		meta.addEnchant(Enchantment.LUCK, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "rayModule"), PersistentDataType.STRING, "fire");

		item.setItemMeta(meta);
		fireModule = item;

		// Recipe
		ShapedRecipe sr = new ShapedRecipe(new NamespacedKey(plugin, "fire_module"), item);
		sr.shape(" IT", "FNC", " IT");
		sr.setIngredient('I', Material.IRON_INGOT);
		sr.setIngredient('T', Material.IRON_TRAPDOOR);
		sr.setIngredient('F', Material.FLINT_AND_STEEL);
		sr.setIngredient('N', Material.NETHERITE_INGOT);
		sr.setIngredient('C', Material.FIRE_CHARGE);
		Bukkit.addRecipe(sr);
	}

	private void createPowerModule() {

		ItemStack item = new ItemStack(Material.TNT);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName("§9Power module");

		List<String> lore = new ArrayList<String>();
		lore.add("§eDeath Ray upgrade");
		lore.add("§7› §a+5 §6Max power ");
		lore.add("§7› §c+0.5 §bConsumption multiplier");
		lore.add("§7Can stack up to 3 times");
		meta.setLore(lore);

		meta.addEnchant(Enchantment.LUCK, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "rayModule"), PersistentDataType.STRING, "power");

		item.setItemMeta(meta);
		powerModule = item;

		// Recipe
		ShapedRecipe sr = new ShapedRecipe(new NamespacedKey(plugin, "power_module"), item);
		sr.shape(" B ", "FEF", "TNT"); // This "TNT" is a pure coincidence
		sr.setIngredient('B', Material.BLAZE_ROD);
		sr.setIngredient('F', Material.FIRE_CHARGE);
		sr.setIngredient('E', Material.END_CRYSTAL);
		sr.setIngredient('T', Material.TNT);
		sr.setIngredient('N', Material.NETHERITE_INGOT);
		Bukkit.addRecipe(sr);
	}

	private void createEfficiencyModule() {
		ItemStack item = new ItemStack(Material.DAYLIGHT_DETECTOR);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName("§aEfficiency module");

		List<String> lore = new ArrayList<String>();
		lore.add("§eDeath Ray upgrade");
		lore.add("§7› §a-0.5 §bConsumption multiplier");
		lore.add("§7Can stack up to 3 times");
		meta.setLore(lore);

		meta.addEnchant(Enchantment.LUCK, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "rayModule"), PersistentDataType.STRING, "efficiency");

		item.setItemMeta(meta);
		efficiencyModule = item;

		// Recipe
		ShapedRecipe sr = new ShapedRecipe(new NamespacedKey(plugin, "efficiency_module"), item);
		sr.shape(" L ", "CSC", "RNR");
		sr.setIngredient('L', Material.LIGHTNING_ROD);
		sr.setIngredient('C', Material.COPPER_INGOT);
		sr.setIngredient('S', Material.DAYLIGHT_DETECTOR);
		sr.setIngredient('R', Material.REDSTONE_BLOCK);
		sr.setIngredient('N', Material.NETHERITE_INGOT);
		Bukkit.addRecipe(sr);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getItem() != null && (e.getItem().isSimilar(fireModule) || e.getItem().isSimilar(powerModule)
				|| e.getItem().isSimilar(efficiencyModule))) {
			e.setCancelled(true);
		}
	}

	public static String moduleToString(Main plugin, ItemStack item) {
		if (item == null) return null;

		PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
		if (!data.has(new NamespacedKey(plugin, "rayModule"), PersistentDataType.STRING)) return null;
		return data.get(new NamespacedKey(plugin, "rayModule"), PersistentDataType.STRING);
	}

	public static ItemStack stringToModule(String s) {
		if (s.equals("fire")) return fireModule;
		if (s.equals("power")) return powerModule;
		if (s.equals("efficiency")) return efficiencyModule;

		return null;
	}

}
