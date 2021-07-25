package fr.xenocraft.deathRay.listeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;
import fr.xenocraft.deathRay.guis.DeathRayMenu;
import fr.xenocraft.deathRay.items.Modules;
import fr.xenocraft.deathRay.services.DeathRayStats;

public class ClickRayMenu implements Listener {

	private Main plugin;
	public static String inv_name = "Death Ray settings";

	public ClickRayMenu(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(inv_name)) return;
		e.setCancelled(true);

		// Do nothing if player clicked outside of the inventory
		if (e.getClickedInventory() == null) return;

		Player p = (Player) e.getWhoClicked();
		ItemStack raygun = p.getInventory().getItemInMainHand();
		ItemMeta meta = raygun.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		DeathRayStats stats = new DeathRayStats(plugin, raygun);

		// What to do when player clicks his inventory
		if (e.getClickedInventory().getType() == InventoryType.PLAYER) {

			// If player shift-clicks a module, add it to the raygun
			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

				if (Modules.moduleToString(plugin, e.getCurrentItem()) != null) {

					for (int i = 0; i < stats.modules.size(); i++) {

						if (stats.modules.get(i).equals("empty")) {
							data.set(new NamespacedKey(plugin, "module" + i), PersistentDataType.STRING,
									Modules.moduleToString(plugin, e.getCurrentItem()));

							// Removing one item of the clicked stack
							ItemStack newCurrentItem = e.getCurrentItem();
							newCurrentItem.setAmount(e.getCurrentItem().getAmount() - 1);
							e.setCurrentItem(newCurrentItem);

							raygun.setItemMeta(meta);
							stats.renderLore(raygun);

							new DeathRayMenu(plugin).renderGUI(p.getOpenInventory().getTopInventory(), raygun);

							p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
							return;
						}
					}
				}

			}
			// Player can use its own inventory
			else if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
				// Player cannot do the action COLLECT_TO_CURSOR on modules because it would
				// take placed modules
				if (Modules.moduleToString(plugin, e.getCursor()) == null) e.setCancelled(false);
			} else {
				e.setCancelled(false);
			}
		}

		// What to do when player clicks the ui
		if (e.getClickedInventory().getType() == InventoryType.CHEST) {

			int slot = e.getSlot();

			// Power selecting
			if (slot == 10) {
				if (stats.power < stats.maxPower) {
					p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
					data.set(new NamespacedKey(plugin, "power"), PersistentDataType.INTEGER, stats.power += 1);
					raygun.setItemMeta(meta);
					new DeathRayMenu(plugin).renderGUI(p.getOpenInventory().getTopInventory(), raygun);
				}
			}

			if (slot == 28) {
				if (stats.power > 0) {
					p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
					data.set(new NamespacedKey(plugin, "power"), PersistentDataType.INTEGER, stats.power -= 1);
					raygun.setItemMeta(meta);
					new DeathRayMenu(plugin).renderGUI(p.getOpenInventory().getTopInventory(), raygun);
				}
			}

			// Battery refilling system
			if (e.getCursor() != null && e.getCursor().getType() == Material.REDSTONE_BLOCK && e.getSlot() >= 5 * 9) {

				if (stats.energy == 1000) return;

				int redstoneAmount;
				if (e.getClick() == ClickType.RIGHT)
					redstoneAmount = 1;
				else
					redstoneAmount = e.getCursor().getAmount();

				int newEnergy = stats.energy + redstoneAmount * 10;
				if (newEnergy > 1000) newEnergy = 1000;

				data.set(new NamespacedKey(plugin, "energy"), PersistentDataType.INTEGER, newEnergy);
				raygun.setItemMeta(meta);

				ItemStack newCursor = e.getCursor();
				newCursor.setAmount(e.getCursor().getAmount() - (int) Math.ceil((newEnergy - stats.energy) / 10f));
				p.setItemOnCursor(newCursor);

				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

				new DeathRayMenu(plugin).renderGUI(p.getOpenInventory().getTopInventory(), raygun);
			}

			// Module placing system
			if (e.getSlot() >= 13 && e.getSlot() < 17) {

				int index = e.getSlot() - 13;

				if (stats.modules.get(index).equals("empty")) {
					// If the player places a module in the slot
					if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {

						String moduleID = Modules.moduleToString(plugin, e.getCursor());
						data.set(new NamespacedKey(plugin, "module" + (index)), PersistentDataType.STRING, moduleID);

						// Removing one item of the cursor
						ItemStack newCursor = e.getCursor();
						newCursor.setAmount(e.getCursor().getAmount() - 1);
						p.setItemOnCursor(newCursor);

						p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
					}
				} else {
					// If the player takes a module from the gui
					if (e.getAction() == InventoryAction.PICKUP_ALL) {

						data.set(new NamespacedKey(plugin, "module" + (index)), PersistentDataType.STRING, "empty");
						p.setItemOnCursor(Modules.stringToModule(stats.modules.get(index)));

						p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
					}
					// If the player shift-clicks a placed module
					if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

						data.set(new NamespacedKey(plugin, "module" + (index)), PersistentDataType.STRING, "empty");
						p.getInventory().addItem(Modules.stringToModule(stats.modules.get(index)));

						p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
					}
					// If the player exchanges two modules
					if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
						if (e.getCursor().getAmount() != 1) return;

						String moduleID = Modules.moduleToString(plugin, e.getCursor());
						data.set(new NamespacedKey(plugin, "module" + (index)), PersistentDataType.STRING, moduleID);

						p.setItemOnCursor(Modules.stringToModule(stats.modules.get(index)));

						p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
					}
				}

				raygun.setItemMeta(meta);
				stats.renderLore(raygun);

				new DeathRayMenu(plugin).renderGUI(p.getOpenInventory().getTopInventory(), raygun);
			}

			// Mode switching
			if (e.getSlot() == 34) {
				String destroymode = data.get(new NamespacedKey(plugin, "destroymode"), PersistentDataType.STRING);

				if (destroymode.equals("entities"))
					data.set(new NamespacedKey(plugin, "destroymode"), PersistentDataType.STRING, "all");
				else
					data.set(new NamespacedKey(plugin, "destroymode"), PersistentDataType.STRING, "entities");

				raygun.setItemMeta(meta);
				new DeathRayMenu(plugin).renderGUI(p.getOpenInventory().getTopInventory(), raygun);

				p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
			}
		}

	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if (e.getView().getTitle().equals(inv_name)) {

			Set<Integer> slots = e.getRawSlots();

			for (Integer slot : slots) {
				if (slot < 54) e.setCancelled(true);
			}
		}
	}

}