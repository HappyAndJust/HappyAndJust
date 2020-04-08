package test;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SkeletonManager implements Listener {
	private Main plugin;

	public SkeletonManager(Main main) {
		this.plugin = main;
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType().equals(Material.ICE)) {
			e.setCancelled(true);
			Pig pig = (Pig) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, 0, 0.5),
					EntityType.PIG);
			pig.setCustomName(ChatColor.translateAlternateColorCodes('&', "&f&lHit me!"));
			pig.setCustomNameVisible(true);
			pig.setAI(false);
			pig.setMetadata("FastArrow", new FixedMetadataValue(plugin, "fastarrow"));

		}
		if (e.getBlock().getType().equals(Material.PACKED_ICE)) {
			e.setCancelled(true);
			Skeleton skeleton = (Skeleton) e.getBlock().getWorld()
					.spawnEntity(e.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.SKELETON);
			Pig pig = (Pig) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, 0, 0.5),
					EntityType.PIG);
			skeleton.setTarget(pig);
		}
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity().hasMetadata("FastArrow") && e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			e.getEntity().remove();
			Skeleton skeleton = (Skeleton) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(),
					EntityType.SKELETON);
			skeleton.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			skeleton.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			skeleton.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			skeleton.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			skeleton.setCustomName(ChatColor.AQUA + "Hyper Skeleton");
			skeleton.setCustomNameVisible(true);
			skeleton.setMetadata("NonDamage", new FixedMetadataValue(plugin, "nondamage"));
			StartArrowSpawn(skeleton, e.getDamager());
		}
		if (e.getEntity().hasMetadata("NonDamage") && e.getDamager() instanceof Arrow) {
			e.setCancelled(true);
		}

	}

	private void StartArrowSpawn(Skeleton skeleton, Entity entity) {
		new BukkitRunnable() {

			@SuppressWarnings("unused")
			@Override
			public void run() {
				try {
					if (skeleton.getTarget().isValid()) {
						if (!skeleton.isDead()) {
							Location loc = skeleton.getEyeLocation();
							Vector vec2 = skeleton.getLocation().getDirection();
							Vector vec = skeleton.getLocation().getDirection().add(vec2).add(vec2).add(vec2);
							Entity target = (Entity) skeleton.getTarget();
							Location loc2 = target.getLocation();
							Vector vec3 = new Vector(loc2.getX(), loc2.getY(), loc2.getZ());
							Location front = loc.add(vec2);
							skeleton.getLocation().setDirection(entity.getLocation().getDirection());

							float speed = (float) 1.5;
							float spread = (float) 3;
							Arrow arrow = skeleton.getWorld().spawnArrow(front, vec, speed, spread);
							arrow.setMetadata("HyperArrow", new FixedMetadataValue(plugin, "hyperarrow"));
						} else {
							cancel();
							return;
						}
					}
				} catch (Exception e) {

				}
			}

		}.runTaskTimer(plugin, 0, (long) 1.5);

	}
}
