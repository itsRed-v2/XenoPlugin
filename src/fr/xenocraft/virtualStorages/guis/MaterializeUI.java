package fr.xenocraft.virtualStorages.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;
import fr.xenocraft.Utils;
import fr.xenocraft.virtualStorages.VirtualStorageUtils;
import fr.xenocraft.virtualStorages.services.MaterializeFunctions;

public class MaterializeUI implements Listener {
	
	private Main plugin;
	
	public static String inv_name = "VSM > Materialising options";
	public int inv_rows = 5 * 9;
	
	public MaterializeUI(Main plugin) {
		this.plugin = plugin;
	}
	
	public Inventory GUI(String network, int item_index, int page) {
		
		Inventory inv = Bukkit.createInventory(null, inv_rows, inv_name);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) plugin.getCustomConfig().getConfigurationSection("virtualStorages").getList(network).get(item_index);
		ItemStack item = ItemStack.deserialize(map);
		
		int stock = item.getAmount();
		
		item.setAmount(1);
		
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<String>();
		if (meta.hasLore()) lore = meta.getLore();
		
		lore.add(" ");
		lore.add("§9Stock: §a" + stock + "§7 (" + VirtualStorageUtils.inStacks(stock, item.getMaxStackSize()) + ")");
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		inv.setItem(13, item);
		
		inv.setItem(11, Utils.createItem(Material.BARRIER, 1, "§7Amount to materialise: §c0",
				"§cYou can't materialise less than 1 item"));
		inv.setItem(15, Utils.createItem(Material.LIME_DYE, 1, "§7Amount to materialise: §c0", "§eLeft Click to add 1",
				"§eRight Click to add 32"));
		
		ItemStack command_block = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta cmd_block_meta = command_block.getItemMeta();
		
		cmd_block_meta.setDisplayName("§6Materialise");
		cmd_block_meta.setLore(Arrays.asList("§cYou can't materialise §a0 §citem"));
		
		PersistentDataContainer data = cmd_block_meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER, 0);
		data.set(new NamespacedKey(plugin, "stock"), PersistentDataType.INTEGER, stock);
		data.set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, page);
		data.set(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER, item_index);
		data.set(new NamespacedKey(plugin, "network"), PersistentDataType.STRING, network);
		
		command_block.setItemMeta(cmd_block_meta);
		
		inv.setItem(31, command_block);
		inv.setItem(36, Utils.createItem(Material.ARROW, 1, "§r< Back"));
		
		return inv;
	}
	
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(inv_name)) return;
		e.setCancelled(true);

		if(e.getClickedInventory().getType() == InventoryType.PLAYER) return;
		
		int slot = e.getSlot();
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		
		NamespacedKey amount_key = new NamespacedKey(plugin, "amount");
		
		PersistentDataContainer cmd_block_data = inv.getItem(31).getItemMeta().getPersistentDataContainer();
		
		int amount = cmd_block_data.get(amount_key, PersistentDataType.INTEGER);
		int item_index = cmd_block_data.get(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER);
		String network = cmd_block_data.get(new NamespacedKey(plugin, "network"), PersistentDataType.STRING);
		
		if (slot == 15) {
			p.playSound(p.getLocation(), "block.lever.click", 1, 1);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) plugin.getCustomConfig().getConfigurationSection("virtualStorages").getList(network).get(item_index);
			ItemStack item = ItemStack.deserialize(map);
			
			int stock = item.getAmount();
			
			if (e.isLeftClick()) {
				if (amount < stock) updateUI(p, amount + 1);
			} else {
				if (amount + 32 < stock) updateUI(p, amount + 32);
				else updateUI(p, stock);
			}
		}
		
		if (slot == 11) {
			p.playSound(p.getLocation(), "block.lever.click", 1, 1);
			
			if (e.isLeftClick()) {
				if (amount > 1) updateUI(p, amount - 1);
			} else {
				if (amount > 32) updateUI(p, amount - 32);
				else updateUI(p, 1);
			}
		}	
		
		if (slot == 31 || slot == 36) {
			
			if (slot == 36) p.playSound(p.getLocation(), "block.lever.click", 1, 1);
			
			if (slot == 31) {
				if (amount == 0) return;
				
				MaterializeFunctions.materialize(plugin, p, item_index, amount, network);
			}
			
			int page = cmd_block_data.get(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER);
			
			p.openInventory(new VsmUI(plugin).GUI(p, network, page));
		}

	}
	
	
	
	private void updateUI(Player p, int quantity) {
		
		InventoryView inv = p.getOpenInventory();
		
		ItemStack item = inv.getItem(13);
		ItemStack cmd_block = inv.getItem(31);
		
		ItemMeta meta = cmd_block.getItemMeta();
		PersistentDataContainer cmd_block_data = meta.getPersistentDataContainer();
		
		cmd_block_data.set(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER, quantity);
		
		int stock = cmd_block_data.get(new NamespacedKey(plugin, "stock"), PersistentDataType.INTEGER);
		
		List<String> lore = new ArrayList<String>();
		
		if (quantity == 0) {
			lore.add("§cYou can't materialise §a" + quantity + "§c item");
		} else if (quantity == 1) {
			lore.add("§9You will materialise §a" + quantity + "§9 item");
		} else {
			lore.add("§9You will materialise §a" + quantity + "§9 items");
		}
		if (item.getMaxStackSize() == 1 && quantity > 16) {
			lore.add("§c§lWarning:");
			lore.add("§6This will materialise §a" + quantity + " §c§lunstackable§6 items");
		}
		
		meta.setLore(lore);
		
		cmd_block.setItemMeta(meta);
		inv.setItem(31, cmd_block);
		
		String inStacks = VirtualStorageUtils.inStacks(quantity, item.getMaxStackSize());
		
		if (quantity > 1) {
			inv.setItem(11,
					Utils.createItem(Material.RED_DYE, 1,
							"§7Amount to materialise: §a" + quantity + "§7 (" + inStacks + ")",
							"§eLeft Click to substract 1", "§eRight Click to substract 32"));
		} else {
			inv.setItem(11,
					Utils.createItem(Material.BARRIER, 1,
							"§7Amount to materialise: §a" + quantity + "§7 (" + inStacks + ")",
							"§cYou can't materialise less than 1 item"));
		}
		if (quantity < stock) {
			inv.setItem(15,
					Utils.createItem(Material.LIME_DYE, 1,
							"§7Amount to materialise: §a" + quantity + "§7 (" + inStacks + ")    ",
							"§eLeft Click to add 1", "§eRight Click to add 32"));
		} else {
			inv.setItem(15,
					Utils.createItem(Material.BARRIER, 1,
							"§7Amount to materialise: §a" + quantity + "§7 (" + inStacks + ")    ",
							"§cYou reached the amount of items in stock"));
		}
		
	}

}
