package test;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.netty.util.internal.ThreadLocalRandom;

public class FoodManager implements Listener {

	@EventHandler
	public void EntityDeath(EntityDeathEvent e) {
		Entity en = e.getEntity();
		if (en.getType() == EntityType.COW) {
			e.getDrops().clear();
			int random = ThreadLocalRandom.current().nextInt(100) + 1;
			ItemStack item = new ItemStack(Material.BREAD);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "빵");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
			if (random >= 65) {
				meta.setLore(Arrays.asList(ChatColor.AQUA + "이 빵을먹으면 -5칸에서 10칸까지 랜덤의 포화를 얻을수있다"));
				item.setItemMeta(meta);
				e.setDroppedExp(100);
				e.getDrops().add(item);
			} else if (random <= 5) {
				meta.setLore(Arrays.asList(ChatColor.AQUA + "이 빵을먹으면 100% 확률로 배고픔이 모두 차게된다"));
				item.setItemMeta(meta);
				e.setDroppedExp(5000);
				e.getDrops().add(item);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void PlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		try {
			if ((p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "빵")) && p.getItemInHand()
					.getItemMeta().getLore().get(0).equals(ChatColor.AQUA + "이 빵을먹으면 -5칸에서 10칸까지 랜덤의 포화를 얻을수있다")) {
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 10, 1);
				int food = ThreadLocalRandom.current().nextInt(16) - 5;
				if ((p.getFoodLevel() + food) <= 0) {
					p.setFoodLevel(0);
				} else if ((p.getFoodLevel() + (food * 2)) >= 20) {
					p.setFoodLevel(20);
				} else {
					p.setFoodLevel(p.getFoodLevel() + food);
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "빵") && p.getItemInHand()
					.getItemMeta().getLore().get(0).equals(ChatColor.AQUA + "이 빵을먹으면 100% 확률로 배고픔이 모두 차게된다")) {
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 10, 1);
				p.setFoodLevel(20);
			}
		} catch (Exception e1) {

		}
	}

}
