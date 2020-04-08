package test;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StickManager implements Listener {
	private Main plugin;

	public StickManager(Main main) {
		this.plugin = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void PlayerInteract(PlayerInteractEvent e) {
		try {
			Player p = e.getPlayer();
			Block b = p.getTargetBlock(null, 5000);
			if(p.getInventory().getChestplate().getItemMeta().getDisplayName().equals("IronGolem's ChestPlate")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100000, 16), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000, 16), true);
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("번개를 내리는 막대기")) {
				if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					p.getWorld().createExplosion(b.getLocation(), 20);
				}
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					p.getWorld().strikeLightning(b.getLocation());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("액체 설치 막대기")) {
				if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					p.getLocation().getBlock().setType(Material.WATER);
				}
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					p.getLocation().getBlock().setType(Material.LAVA);
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("마녀 소환 막대기")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Witch witch = (Witch) p.getWorld().spawnEntity(p.getLocation(), EntityType.WITCH);
					witch.setVelocity(p.getLocation().getDirection());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("좀비 소환 막대기")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Zombie zombie = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);

					ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
					item.addEnchantment(Enchantment.DAMAGE_ALL, 5);
					item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
					zombie.getEquipment().setItemInHand(item);
					zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000 * 20, 2), true);
					zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100000 * 20, 3), true);
					zombie.setMaxHealth(50);
					zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2), true);
					zombie.setVelocity(p.getLocation().getDirection());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("좀비주민 소환 막대기")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Villager villager = (Villager) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
					Zombie zombie = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
					ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
					item.addEnchantment(Enchantment.DAMAGE_ALL, 5);
					item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
					zombie.getEquipment().setItemInHand(item);
					zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000 * 20, 2));
					zombie.setMaxHealth(50);
					zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2), true);
					villager.setVelocity(p.getLocation().getDirection());
					zombie.setVelocity(p.getLocation().getDirection());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("스켈레톤 소환 막대기")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Skeleton skeleton = (Skeleton) p.getWorld().spawnEntity(p.getLocation(), EntityType.SKELETON);

					ItemStack item = new ItemStack(Material.BOW);
					item.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
					item.addEnchantment(Enchantment.ARROW_FIRE, 1);
					skeleton.getEquipment().setItemInHand(item);
					skeleton.setVelocity(p.getLocation().getDirection());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("크리퍼 소환 막대기")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Creeper creeper = (Creeper) p.getWorld().spawnEntity(p.getLocation(), EntityType.CREEPER);
					creeper.setVelocity(p.getLocation().getDirection());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("철골렘 소환 막대기")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					IronGolem irongolem = (IronGolem) p.getWorld().spawnEntity(p.getLocation(), EntityType.IRON_GOLEM);
					irongolem.setCustomName("특별한 골렘");
					irongolem.setCustomNameVisible(true);
					irongolem.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000 * 20, 3), true);
					irongolem.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100000 * 20, 3),
							true);
					irongolem.setMaxHealth(300);
					irongolem.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 5), true);
					irongolem.setMetadata("GoodGolem", new FixedMetadataValue(plugin, "goodgolem"));
					irongolem.setVelocity(p.getLocation().getDirection());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("주민 소환 막대기")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Villager villager = (Villager) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
					villager.setVelocity(p.getLocation().getDirection());
				}
			}
			if (p.getItemInHand().getItemMeta().getDisplayName().equals("즉시 치료")) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					e.setCancelled(true);
				}
				if (e.getAction() == Action.RIGHT_CLICK_AIR) {
					p.setHealth(20);
					p.setFoodLevel(20);
				}

			}
		} catch (Exception ignored) {

		}

	}

}
