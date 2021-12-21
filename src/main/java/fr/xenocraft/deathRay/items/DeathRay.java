package fr.xenocraft.deathRay.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;
import fr.xenocraft.deathRay.services.DeathRayStats;

public class DeathRay {

	private Main plugin;
	public static ItemStack deathRay;

	public DeathRay(Main plugin) {
		this.plugin = plugin;
	}

	public void init() {
		createDeathRay();
	}

	private void createDeathRay() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName("§cDeath Ray");
		meta.setCustomModelData(15742400);

		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "power"), PersistentDataType.INTEGER, 0);
		data.set(new NamespacedKey(plugin, "energy"), PersistentDataType.INTEGER, 0);
		data.set(new NamespacedKey(plugin, "destroymode"), PersistentDataType.STRING, "entities");
		data.set(new NamespacedKey(plugin, "module0"), PersistentDataType.STRING, "empty");
		data.set(new NamespacedKey(plugin, "module1"), PersistentDataType.STRING, "empty");
		data.set(new NamespacedKey(plugin, "module2"), PersistentDataType.STRING, "empty");
		data.set(new NamespacedKey(plugin, "module3"), PersistentDataType.STRING, "empty");

		item.setItemMeta(meta);

		deathRay = new DeathRayStats(plugin, item).renderLore(item);

		// Recipe
		ShapedRecipe sr = new ShapedRecipe(new NamespacedKey(plugin, "death_ray"), deathRay);
		sr.shape("NNI", "SAG", "NNI");
		sr.setIngredient('N', Material.NETHERITE_INGOT);
		sr.setIngredient('I', Material.IRON_INGOT);
		sr.setIngredient('S', Material.NETHER_STAR);
		sr.setIngredient('A', Material.AMETHYST_SHARD);
		sr.setIngredient('G', Material.SPYGLASS);
		Bukkit.addRecipe(sr);
	}

}