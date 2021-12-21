package fr.xenocraft;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapCanvas;

public class Utils {
	
	public static ItemStack createItem(Material material, int amount, String displayName, String... loreString) {
		
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(displayName);
		meta.setLore(Arrays.asList(loreString));
		
		item.setItemMeta(meta);
		return item;
	}
	
	public static void asyncRenderImage(Main plugin, MapCanvas canvas, Player p, String url) {
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				
				try {
					canvas.drawImage(0, 0, ImageIO.read(new URL(url)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});
	}

}
