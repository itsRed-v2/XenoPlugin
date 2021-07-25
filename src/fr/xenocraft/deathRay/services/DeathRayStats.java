package fr.xenocraft.deathRay.services;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;

public class DeathRayStats {

	private Main plugin;

	public int power;
	public int maxPower = 5;
	public boolean setFire = false;
	public int energy;
	public float consumptionMultiplier = 2;
	public int energyPerShot;
	public boolean destroyBlocks;
	public List<String> modules = new ArrayList<String>();

	public DeathRayStats(Main plugin, ItemStack raygun) {
		this.plugin = plugin;

		ItemMeta meta = raygun.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		for (int i = 0; i < 4; i++) {
			String moduleID = data.get(new NamespacedKey(plugin, "module" + i), PersistentDataType.STRING);
			modules.add(moduleID);

			if (moduleID.equals("power") && maxPower < 20) {
				maxPower += 5;
				consumptionMultiplier += .5;
			}

			if (moduleID.equals("fire") && setFire == false) {
				setFire = true;
				consumptionMultiplier += .5;
			}

			if (moduleID.equals("efficiency") && consumptionMultiplier > .5) {
				consumptionMultiplier -= .5;
			}
		}

		power = data.get(new NamespacedKey(plugin, "power"), PersistentDataType.INTEGER);
		if (power > maxPower) {
			power = maxPower;
			data.set(new NamespacedKey(plugin, "power"), PersistentDataType.INTEGER, maxPower);
			raygun.setItemMeta(meta);
		}

		energy = data.get(new NamespacedKey(plugin, "energy"), PersistentDataType.INTEGER);
		energyPerShot = Math.round(power * consumptionMultiplier);

		String destroyMode = data.get(new NamespacedKey(plugin, "destroymode"), PersistentDataType.STRING);
		if (destroyMode.equals("entities")) {
			destroyBlocks = false;
			setFire = false;
		} else {
			destroyBlocks = true;
		}

		// Rendering lore
		List<String> lore = new ArrayList<String>();
		lore.add("§eEnergy: §b" + energy + "§e ⚡");
		lore.add("§6Max power: §c" + maxPower);
		lore.add("§bConsumption multiplier: §6" + consumptionMultiplier);
		lore.add("");
		lore.add("§3Modules:");
		for (String module : modules) {
			if (module.equals("fire")) lore.add("§7- §6Fire module");
			if (module.equals("power")) lore.add("§7- §9Power module");
			if (module.equals("efficiency")) lore.add("§7- §aEfficiency module");
			if (module.equals("empty")) lore.add("§7- §8None");
		}
		lore.add("");
		lore.add("§7Press F when in hand to open the menu!");
		meta.setLore(lore);

		raygun.setItemMeta(meta);
	}

	public ItemStack renderLore(ItemStack raygun) {
		new DeathRayStats(plugin, raygun);
		return raygun;
	}
}
