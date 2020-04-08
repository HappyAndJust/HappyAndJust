package test;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;

public class Event implements Listener {
	private int cooltime;
	private Main plugin;
	private boolean isStart = false;
	private boolean isTNT = false;
	private boolean isSnow = false;
	private Zombie zombie;

	public Zombie getZombie() {
		return zombie;
	}

	public void setZombie(Zombie zombie) {
		this.zombie = zombie;
	}

	public Vector vector(Player p) {
		return p.getLocation().getDirection().multiply(2);
	}

	public boolean isSnow() {
		return isSnow;
	}

	public void setSnow(boolean isSnow) {
		this.isSnow = isSnow;
	}

	public boolean isTNT() {
		return isTNT;
	}

	public void setTNT(boolean isTNT) {
		this.isTNT = isTNT;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public Event(Main main) {
		this.plugin = main;
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void PlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = p.getTargetBlock(null, 50);
		ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
		Material mat = p.getInventory().getItemInMainHand().getType();
		try {
			if (e.getAction() == Action.RIGHT_CLICK_AIR) {
				if (meta.getDisplayName().equals("강한 나무검") && mat.equals(Material.WOOD_SWORD)) {
					if (cooltime == 0) {
						p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 1000);
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 100, p.getLocation().getPitch());
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 7, 3), false);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 7, 2), false);
						p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 7, 5), false);
						cooltime = 20;
						StartCooltime();
					} else {
						p.sendMessage(ChatColor.DARK_RED + "쿨타임이 " + cooltime + " 초 남았습니다.");
					}
				}
				if (mat == Material.FIREBALL) {
					Vector vec = vector(p);
					Fireball fire = (Fireball) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.FIREBALL);
					fire.setGravity(false);
					fire.setDirection(vec);
				}
				if (meta.getDisplayName().equals(ChatColor.DARK_PURPLE + "보스 소환")) {
					setStart(true);
					StartZombieSpawn(p);
				}
				if (meta.getDisplayName().equals(ChatColor.AQUA + "무한 TNT")) {
					setTNT(true);
					StartTNTSpawn(p);
				}
				if (meta.getDisplayName().equals(ChatColor.AQUA + "눈사람")) {
					setSnow(true);
					StartSnowSpawn(p);
				}

			}
			if (e.getAction() == Action.LEFT_CLICK_AIR) {
				if (meta.getDisplayName().equals(ChatColor.DARK_PURPLE + "보스 소환")) {
					setStart(false);
				}
				if (meta.getDisplayName().equals(ChatColor.AQUA + "무한 TNT")) {
					setTNT(false);
				}
				if (meta.getDisplayName().equals(ChatColor.AQUA + "눈사람")) {
					setSnow(false);
				}
			}
		} catch (Exception ignored) {

		}
	}

	private void StartSnowSpawn(Player p) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!isSnow()) {
					cancel();
					return;
				}
				Snowman snow = (Snowman) p.getWorld().spawnEntity(p.getLocation(), EntityType.SNOWMAN);
				snow.setVelocity(vector(p));
			}
		}.runTaskTimer(plugin, 0, 0);
	}

	private void StartTNTSpawn(Player p) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!isTNT()) {
					cancel();
					return;
				}
				TNTPrimed tnt = (TNTPrimed) p.getWorld().spawnEntity(p.getLocation(), EntityType.PRIMED_TNT);
				tnt.setVelocity(vector(p));
				tnt.setFuseTicks(20 * 5);
			}
		}.runTaskTimer(plugin, 0, 0);
	}

	private void StartCooltime() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (cooltime == 0) {
					cancel();
					return;
				}
				cooltime -= 1;
			}
		}.runTaskTimer(plugin, 0, 20);
	}

	private void StartZombieSpawn(Player p) {
		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				if (!isStart()) {
					cancel();
					return;
				}
				ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
				ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
				ItemStack leg = new ItemStack(Material.DIAMOND_LEGGINGS);
				ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
				ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
				ItemMeta hmeta = helmet.getItemMeta();
				ItemMeta cmeta = chest.getItemMeta();
				ItemMeta lmeta = leg.getItemMeta();
				ItemMeta bmeta = boots.getItemMeta();
				ItemMeta smeta = sword.getItemMeta();
				smeta.addEnchant(Enchantment.DAMAGE_ALL, 7, true);
				hmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
				cmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
				lmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
				bmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
				hmeta.addEnchant(Enchantment.PROTECTION_FIRE, 7, true);
				cmeta.addEnchant(Enchantment.PROTECTION_FIRE, 7, true);
				lmeta.addEnchant(Enchantment.PROTECTION_FIRE, 7, true);
				bmeta.addEnchant(Enchantment.PROTECTION_FIRE, 7, true);
				helmet.setItemMeta(hmeta);
				chest.setItemMeta(cmeta);
				leg.setItemMeta(lmeta);
				boots.setItemMeta(bmeta);
				sword.setItemMeta(smeta);
				Zombie zombie = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
				EntityEquipment equip = zombie.getEquipment();
				equip.setHelmet(helmet);
				equip.setChestplate(chest);
				equip.setLeggings(leg);
				equip.setBoots(boots);
				equip.setItemInHand(sword);
				equip.setHelmetDropChance(0);
				equip.setChestplateDropChance(0);
				equip.setLeggingsDropChance(0);
				equip.setBootsDropChance(0);
				equip.setItemInHandDropChance(0);
				zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000 * 20, 5), false);
				zombie.setCustomName(ChatColor.DARK_PURPLE + "Zombie Boss");
				zombie.setCustomNameVisible(true);
				zombie.setMetadata("ZombieKing", new FixedMetadataValue(plugin, "zombieking"));
				zombie.setBaby(false);
				zombie.setVelocity(vector(p));
				setZombie(zombie);

				StartMinionSpawn(zombie, p);
			}
		}.runTaskTimer(plugin, 0, 0);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		int random2 = ThreadLocalRandom.current().nextInt(10);
		if (e.getEntity().hasMetadata("ZombieKing") && e.getEntity() instanceof Zombie) {
			if (random2 <= 5) {
				e.setCancelled(true);
			}
		}
		if (e.getEntity() instanceof Player && e.getDamager().hasMetadata("ZombieKing")) {
			Player p = (Player) e.getEntity();
			int random3 = ThreadLocalRandom.current().nextInt(10);
			if (random3 <= 3) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 20 * 1, 2), false);
			}
			if (random3 <= 4) {
				Location loc = e.getEntity().getLocation();
				e.getEntity().teleport(new Location(e.getEntity().getWorld(), loc.getX(), loc.getY() + 5, loc.getZ(),
						loc.getYaw(), loc.getPitch()));
			}
		}
		if (e.getDamager().hasMetadata("Baby")) {
			try {
				Zombie zom = (Zombie) e.getDamager();
				zom.getWorld().strikeLightning(e.getEntity().getLocation());
				zom.remove();
			} catch (Exception ignored) {

			}
		}
		try {
			if (e.getDamager() instanceof Player
					&& ((HumanEntity) e.getDamager()).getItemInHand().getItemMeta().getDisplayName().equals("강한 나무검")) {
				Player p = (Player) e.getDamager();
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 3), false);
				p.getWorld().createExplosion(e.getEntity().getLocation(), 10, false);
				;
			}
		} catch (Exception ignored) {

		}
	}

	private void StartMinionSpawn(Zombie zombie, Player p) {
		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				int i;
				try {
					for (i = 1; i <= 3; i++) {
						if (zombie.getTarget().isValid()) {
							if (!zombie.isDead()) {
								Zombie zom = (Zombie) p.getWorld().spawnEntity(zombie.getLocation(), EntityType.ZOMBIE);
								zom.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000 * 20, 2), false);
								zom.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
								zom.getEquipment().setHelmet(new ItemStack(Material.ICE));
								zom.setMetadata("Baby", new FixedMetadataValue(plugin, "baby"));
								zom.setBaby(true);
							}
						}
					}
				} catch (Exception e) {

				}
				i = 1;
			}
		}.runTaskTimer(plugin, 20 * 10, 20 * 14);
	}

	@EventHandler
	public void EntityDeath(EntityDeathEvent e) {
		ItemStack item = new ItemStack(Material.WOOD_SWORD);
		ItemStack item2 = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		ItemMeta meta2 = item2.getItemMeta();
		meta2.setUnbreakable(true);
		meta2.setDisplayName(ChatColor.AQUA + "IronGolem's ChestPlate");
		meta2.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 100, true);
		meta.setLore(Arrays.asList(ChatColor.BLUE + "입고있을시 엄청난 효과를 받게된다"));
		item2.setItemMeta(meta2);
		meta.setUnbreakable(true);
		meta.setDisplayName("강한 나무검");
		meta.addEnchant(Enchantment.DAMAGE_ALL, 70, true);
		meta.setLore(Arrays.asList(ChatColor.RED + "이 검을 사용하는자는 엄청난 능력을 얻을수있다.", ChatColor.AQUA + "스킬 사용법:우클릭"));
		item.setItemMeta(meta);
		if (e.getEntity().hasMetadata("FallingBlock")) {
			Location loc = e.getEntity().getLocation();
			loc.getBlock().setType(Main.getMat1());

		}
		if (e.getEntity().hasMetadata("ZombieKing")) {
			e.setDroppedExp(100);
			e.getDrops().clear();
			int random = ThreadLocalRandom.current().nextInt(10);
			if (random <= 4) {
				e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), item);
			}
		}
		if (e.getEntity().hasMetadata("baby")) {
			e.getDrops().clear();
			e.setDroppedExp(0);
		}
		if(e.getEntity().hasMetadata("GoodGolem")) {
			e.setDroppedExp(500);
			e.getDrops().clear();
			int random = ThreadLocalRandom.current().nextInt(10);
			if(random <= 2) {
				e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), item2);
			}
		}
	}
}
