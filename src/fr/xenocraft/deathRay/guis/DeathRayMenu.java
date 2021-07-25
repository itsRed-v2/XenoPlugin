package fr.xenocraft.deathRay.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.xenocraft.Main;
import fr.xenocraft.Utils;
import fr.xenocraft.deathRay.items.Modules;
import fr.xenocraft.deathRay.services.DeathRayStats;

public class DeathRayMenu {

	private Main plugin;
	public static String inv_name = "Death Ray settings";

	public DeathRayMenu(Main plugin) {
		this.plugin = plugin;
	}

	public Inventory openGUI(ItemStack raygun) {
		Inventory inv = Bukkit.createInventory(null, 6 * 9, inv_name);
		renderGUI(inv, raygun);
		return inv;
	}

	public Inventory renderGUI(Inventory inv, ItemStack raygun) {

		DeathRayStats stats = new DeathRayStats(plugin, raygun);

		// Power selector
		if (stats.power == stats.maxPower) {
			inv.setItem(10, Utils.createItem(Material.RED_DYE, 1, "§cIncrease power §7(§e" + stats.power + "§7)",
					"§c" + stats.maxPower + "§6 is the max value"));
		} else {
			inv.setItem(10,
					Utils.createItem(Material.RED_DYE, 1, "§cIncrease power §7(§e" + stats.power + "§7)",
							"§7Max value: §6" + stats.maxPower,
					"§eClick to add 1"));
		}

		if (stats.power > 0) {
			inv.setItem(19,
					Utils.createItem(Material.TNT, stats.power, "§6Power:§e " + stats.power,
							"§eEnergy comsumption per shot: §b" + stats.energyPerShot + "§e ⚡",
							"§bConsumption multiplier: §6" + stats.consumptionMultiplier,
							"§8The energy needed per shot is equal to",
							"§8power × consumption multiplier (rounded)"));
		} else {
			inv.setItem(19, Utils.createItem(Material.BARRIER, 1, "§6Power:§e " + stats.power,
					"§aPower is set to §20§a so nothing will be destroyed",
					"§eEnergy comsumption per shot: §b" + stats.energyPerShot + "§e ⚡",
					"§bConsumption multiplier: §6" + stats.consumptionMultiplier,
					"§8The energy needed per shot is equal to",
					"§8power × consumption multiplier (rounded)"));
		}

		if (stats.power == 0) {
			inv.setItem(28, Utils.createItem(Material.LIME_DYE, 1, "§aDecrease power §7(§e" + stats.power + "§7)",
					"§c0 §6is the min value"));
		} else {
			inv.setItem(28, Utils.createItem(Material.LIME_DYE, 1, "§aDecrease power §7(§e" + stats.power + "§7)",
					"§eClick to remove 1"));
		}

		// Modules
		for (int i = 0; i < stats.modules.size(); i++) {
			if (stats.modules.get(i).equals("empty"))
				inv.setItem(13 + i, Utils.createItem(Material.ORANGE_STAINED_GLASS_PANE, 1, "§6Module slot",
						"§7A module can be placed here", "§7to upgrade the death ray"));
			else
				inv.setItem(13 + i, Modules.stringToModule(stats.modules.get(i)));
		}

		// Mode
		ItemStack modeItem = Utils.createItem(Material.GLASS, 1, "§6Destroy mode");
		ItemMeta modeMeta = modeItem.getItemMeta();

		List<String> modeLore = new ArrayList<String>();
		modeLore.add("§8› Only entities");
		modeLore.add("§8› Everything");
		modeLore.add("");

		if (stats.destroyBlocks == false) {
			modeItem.setType(Material.YELLOW_CONCRETE);

			modeLore.set(0, "§b§l› §bOnly entities");

			modeLore.add("§eOnly entities will be affected in this mode");
			modeLore.add("§eBlocks will not be destroyed");
			modeLore.add("§eFire module has no effect in this mode");
			modeLore.add("§6Note: §ePaintings, item frames, minecarts, etc");
			modeLore.add("§eare entities and will be destroyed");

		} else if (stats.destroyBlocks == true) {
			modeItem.setType(Material.REDSTONE_BLOCK);

			modeLore.set(1, "§b§l› §bEverything");

			modeLore.add("§eLike a normal explosion,");
			modeLore.add("§eblocks will be destroyed and entities will  ");
			modeLore.add("§etake damage");
		}

		modeMeta.setLore(modeLore);
		modeItem.setItemMeta(modeMeta);
		inv.setItem(34, modeItem);

		// Battery
		inv.setItem(45, Utils.createItem(Material.BEACON, 1, "§bBattery",
				"§eEnergy: §6" + stats.energy + "§e ⚡ §7(" + Math.round(stats.energy / 10f) + "%)",
				"§eMax capacity: 1000 ⚡"));

		Material glassPaneMaterial = Material.LIME_STAINED_GLASS_PANE;
		for (int i = 0; i < 8; i++) {
			if (i >= Math.round(stats.energy / 1000f * 8)) glassPaneMaterial = Material.BLACK_STAINED_GLASS_PANE;
			inv.setItem(i + 46, Utils.createItem(glassPaneMaterial, 1, "§eEnergy: §6" + stats.energy + "§e/1000 ⚡",
					"§7Drag & drop redstone blocks here", "§7to refill the battery (1 block = 10 ⚡)"));
		}
		
		return inv;
	}

}