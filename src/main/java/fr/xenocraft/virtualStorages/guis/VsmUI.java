package fr.xenocraft.virtualStorages.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;
import fr.xenocraft.Utils;
import fr.xenocraft.virtualStorages.VirtualStorageUtils;
import fr.xenocraft.virtualStorages.services.MaterializeFunctions;

public class VsmUI implements Listener {
	
	private Main plugin;
	
	public static String inv_name = "Virtual Storage Manager";
	public int inv_size = 6 * 9;
	public int inv_slots = inv_size - 9;
	
	public VsmUI(Main plugin) {
		this.plugin = plugin;
	}
	
	public Inventory GUI(Player p, String network, int page) {
		
		Inventory inv = Bukkit.createInventory(null, inv_size, inv_name);
		
		String sort_method = p.getPersistentDataContainer().get(new NamespacedKey(plugin, "vsm.sort"), PersistentDataType.STRING);
		
		for (int i = 0; i < 9; i++) {
			inv.setItem(i, Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
		}
		
		ConfigurationSection virtualStorages = plugin.getCustomConfig().getConfigurationSection("virtualStorages");
		
		if(virtualStorages.contains(network)) {
			
			inv.setItem(2, Utils.createItem(Material.BEACON, 1, "§6Dematerialise",
					"§eClick to dematerialise items on the dematerialiser"));
			
			ItemStack hopper = Utils.createItem(Material.HOPPER, 1, "§9Sort by:");
			ItemMeta hopper_meta = hopper.getItemMeta();
			
			List<String> lore = new ArrayList<String>();
			lore.add("§7› order added");
			lore.add("§7› amount");
			
			if (sort_method.equals("amount")) lore.set(1, "§b§l›§b amount");
			if (sort_method.equals("add_order")) lore.set(0, "§b§l›§b order added");
			
			hopper_meta.setLore(lore);
			
			PersistentDataContainer hopper_data = hopper_meta.getPersistentDataContainer();
			hopper_data.set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, page);
			hopper_data.set(new NamespacedKey(plugin, "network"), PersistentDataType.STRING, network);
			
			hopper.setItemMeta(hopper_meta);
			inv.setItem(6, hopper);
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> config_items = ((List<Map<String, Object>>) virtualStorages.getList(network));
			
			List<Map<String, Object>> sorted_items = new ArrayList<Map<String, Object>>(config_items);
			
			if (sort_method.equals("amount")) {
				sorted_items.sort(new Comparator<Map<String, Object>>() {
					
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return getAmount(o2) - getAmount(o1);
					}
					private int getAmount(Map<String, Object> map) {
						if (map.containsKey("amount")) return (int) map.get("amount");
						return 1;		 
					}
				});
			}
			else sorted_items = config_items;
			
			if ((page + 1) * inv_slots < sorted_items.size())
				inv.setItem(8, Utils.createItem(Material.ARROW, 1, "§rNext Page >"));
			if (page > 0) inv.setItem(0, Utils.createItem(Material.ARROW, 1, "§r< Previous Page"));
			
			for (int i = 0; i + (page * inv_slots) < sorted_items.size() && i < inv_slots; i++) {
				Map<String, Object> map = sorted_items.get(i + (page * inv_slots));
				ItemStack item = ItemStack.deserialize(map);
				int amount = item.getAmount();
				
				int stack = (int) Math.floor(amount / 64);
				
				ItemMeta meta = item.getItemMeta();

				if (meta.hasLore()) lore = meta.getLore();
				else lore = new ArrayList<String>(); 
				
				lore.add(" ");
				lore.add("§9Stock: §a" + amount + "§7 (" + VirtualStorageUtils.inStacks(amount, item.getMaxStackSize()) + ")");
				lore.add("§eClick for more info");
				
				
				meta.setLore(lore);
				meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER, config_items.indexOf(map));
				item.setItemMeta(meta);
				
				if (stack < 1) stack = 1;
				item.setAmount(stack);
				
  				inv.setItem(i + 9, item);
			}
		} else {
			p.sendMessage("§cERROR: The specified network doesn't exist");
		}
		
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
		ItemStack clicked = e.getCurrentItem();
		
		PersistentDataContainer player_data = p.getPersistentDataContainer();
		
		int page = inv.getItem(6).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER);
		String network = inv.getItem(6).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "network"), PersistentDataType.STRING);
		
		if (slot > 8) {
			p.playSound(p.getLocation(), "block.lever.click", 1, 1);
			
			int index = clicked.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER);
			
			p.openInventory(new MaterializeUI(plugin).GUI(network, index, page));
		}
		
		if (slot == 2) {
			if (MaterializeFunctions.dematerialize(plugin, p, network)) {
				p.openInventory(GUI(p, network, page));
			} else {
				p.playSound(p.getLocation(), "block.lever.click", 1, 1);
				
				ItemStack bedrock = new ItemStack(Material.BEDROCK);
				ItemMeta meta = bedrock.getItemMeta();
				
				meta.setDisplayName("§6Dematerialise");
				meta.setLore(Arrays.asList("§cThere is no items on the dematerializer!"));
				
				bedrock.setItemMeta(meta);
				
				p.getOpenInventory().setItem(2, bedrock);
			}
		}
		
		if (slot == 6) {
			p.playSound(p.getLocation(), "block.lever.click", 1, 1);
			
			String sort = player_data.get(new NamespacedKey(plugin, "vsm.sort"), PersistentDataType.STRING);
			
			if (sort.equals("add_order")) player_data.set(new NamespacedKey(plugin, "vsm.sort"), PersistentDataType.STRING, "amount");
			else if (sort.equals("amount")) player_data.set(new NamespacedKey(plugin, "vsm.sort"), PersistentDataType.STRING, "add_order");
			
			p.openInventory(GUI(p, network, page));
		}
		
		if ((slot == 0 || slot == 8) && clicked.getType() == Material.ARROW) {
			p.playSound(p.getLocation(), "block.lever.click", 1, 1);
			
			if (slot == 8) p.openInventory(GUI(p, network, page + 1));
			if (slot == 0) p.openInventory(GUI(p, network, page - 1));
			
		}
	}
	
}
