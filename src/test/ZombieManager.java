package test;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieManager implements Listener {
	private Main plugin;

	public ZombieManager(Main main) {
		this.plugin = main;
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType().equals(Material.SPONGE)) {
			e.setCancelled(true);
			Pig pig = (Pig) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, 0, 0.5),
					EntityType.PIG);
			pig.setCustomName(ChatColor.translateAlternateColorCodes('&', "&f&lHit me!"));
			pig.setCustomNameVisible(true);
			pig.setMetadata("Angry", new FixedMetadataValue(plugin, "angry"));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamageEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Pig && e.getDamager() instanceof Player) {
			if (e.getEntity().hasMetadata("Angry")) {
				Zombie zombie = (Zombie) e.getEntity().getLocation().getWorld().spawnEntity(e.getEntity().getLocation(),
						EntityType.ZOMBIE);
				ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, true);
				item.setItemMeta(meta);
				zombie.getEquipment().setChestplate(item);
				zombie.setMaxHealth(300);
				zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100000 * 20, 5));
				zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
				zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000 * 20, 2));
				zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 3));
				zombie.setAI(false);
				zombie.setBaby(false);
				e.getEntity().remove();
				startMinionSpawn(zombie);
			}
		}

		if (e.getDamager().hasMetadata("ExplodingZombie")) {
			e.getDamager().getWorld().createExplosion(e.getDamager().getLocation(), 1, true);
		}
	}

	private void startMinionSpawn(Zombie zombie) {
		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (zombie.isDead()) {
					cancel();
					return;
				}
				Zombie babyzombie = (Zombie) zombie.getLocation().getWorld().spawnEntity(zombie.getLocation(),
						EntityType.ZOMBIE);
				babyzombie.setBaby(true);
				babyzombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
				babyzombie.getEquipment().setItemInHand(new ItemStack(Material.WOOD_SWORD));
				babyzombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000 * 20, 3));
				babyzombie.setMetadata("ExplodingZombie", new FixedMetadataValue(plugin, "explodingzombie"));
			}
		}.runTaskTimer(plugin, 50, 20);
	}
}
