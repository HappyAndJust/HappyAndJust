package test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
	public CustomEnchantment ENCH = new CustomEnchantment(101);

	private static Material mat1;
	private boolean isStart = false;

	Configuration CONFIG = this.getConfig();
	public static boolean PVP = true;

	private void s() {
		saveConfig();
	}

	public static Material getMat1() {
		return mat1;
	}

	public static void setMat1(Material mat2) {
		mat1 = mat2;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public void onEnable() {
		System.out.println(ChatColor.GREEN + "Idea Plugin has Enabled");
		Bukkit.getPluginManager().registerEvents(new PacketListener(), this);
		s();
		recipe();
		LoadEnchantment();
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(ENCH, this);
		Bukkit.getPluginManager().registerEvents(new Event(this), this);
		Bukkit.getPluginManager().registerEvents(new SkeletonManager(this), this);
		Bukkit.getPluginManager().registerEvents(new StickManager(this), this);
		getServer().getPluginManager().registerEvents(new ZombieManager(this), this);
		this.getServer().getPluginManager().registerEvents(new FoodManager(), this);
	}

	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	@Override
	public void onDisable() {
		System.out.println(ChatColor.RED + "Idea Plugin has Disabled.");
		try {
			Field byIdField = Enchantment.class.getDeclaredField("byId");
			Field byNameField = Enchantment.class.getDeclaredField("byName");
			byIdField.setAccessible(true);
			byNameField.setAccessible(true);

			HashMap<Integer, Enchantment> byId = (HashMap<Integer, Enchantment>) byIdField.get(null);
			HashMap<Integer, Enchantment> byName = (HashMap<Integer, Enchantment>) byNameField.get(null);

			if (byId.containsKey(ENCH.getId())) {
				byId.remove(ENCH.getId());
			}

			if (byName.containsKey(ENCH.getName())) {
				byName.remove(ENCH.getName());
			}
		} catch (Exception ignored) {
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pvp")) {

			PVP = !PVP;
			sender.sendMessage(ChatColor.RED + "pvp가능 상태를 " + PVP + "으로 바꾸었습니다");
		}
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("exp")) {
			if (args.length == 2) {
				try {
					Player target = Bukkit.getPlayer(args[0]);
					try {
						int xp = Integer.parseInt(args[1]);
						target.setLevel(xp);
						target.setExp(0);
					} catch (NumberFormatException e) {
						p.sendMessage("'" + args[1] + "'은 올바른 수량이아닙니다.");
					}
				} catch (NullPointerException e) {
					p.sendMessage(ChatColor.RED + args[0] + "이라는 이름의 사람이 존재하지않습니다");
				}
			} else {
				p.sendMessage(ChatColor.RED + "사용법: /exp [플레이어] <수량>");
			}
		}
		if (cmd.getName().equalsIgnoreCase("food")) {
			p.sendMessage(ChatColor.BLUE + "당신의 허기는 " + ((double) p.getFoodLevel() / 2) + "칸 입니다");
		}
		if (cmd.getName().equalsIgnoreCase("mute")) {
			if (args.length == 1) {
				String name = args[0];
				Player onlinePlayer = Bukkit.getPlayer(name);
				if (onlinePlayer == null) {
					Player offlinePlayer = (Player) Bukkit.getOfflinePlayer(name);
					if (offlinePlayer == null) {
						p.sendMessage(ChatColor.RED + "플레이어가 존재하지 않습니다");
					} else {
						CONFIG.set("World." + "Chat." + "Mute." + offlinePlayer.getName() + "."
								+ offlinePlayer.getUniqueId(), 1);
						s();
						p.sendMessage(ChatColor.GREEN + "해당 플레이어를 뮤트했습니다");
					}
				} else {
					CONFIG.set("World." + "Chat." + "Mute." + onlinePlayer.getName() + "." + onlinePlayer.getUniqueId(),
							1);
					p.sendMessage(ChatColor.GREEN + "해당 플레이어를 뮤트했습니다");
					s();
				}
			} else {
				p.sendMessage(ChatColor.RED + "사용법 : /mute [PlayerName]");
			}
		}
		if (cmd.getName().equalsIgnoreCase("unmute")) {
			if (args.length == 1) {
				String name = args[0];
				Player onlinePlayer = Bukkit.getPlayer(name);
				if (onlinePlayer == null) {
					Player offlinePlayer = (Player) Bukkit.getOfflinePlayer(name);
					if (offlinePlayer == null) {
						p.sendMessage(ChatColor.RED + "플레이어가 존재하지 않습니다");
					} else {
						CONFIG.set("World." + "Chat." + "Mute." + offlinePlayer.getName() + "."
								+ offlinePlayer.getUniqueId(), 0);
						p.sendMessage(ChatColor.GREEN + "해당 플레이어를 언뮤트했습니다");
						s();
					}
				} else {
					CONFIG.set("World." + "Chat." + "Mute." + onlinePlayer.getName() + "." + onlinePlayer.getUniqueId(),
							0);
					p.sendMessage(ChatColor.GREEN + "해당 플레이어를 언뮤트했습니다");
					s();
				}
			} else {
				p.sendMessage(ChatColor.RED + "사용법 : /unmute [PlayerName]");
			}
		}
		if (cmd.getName().equalsIgnoreCase("axe")) {

			ItemStack item = new ItemStack(Material.DIAMOND_AXE);
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + ENCH.getName() + " I");
			meta.setUnbreakable(true);
			meta.setDisplayName(ChatColor.GOLD + "Lightning Axe");
			meta.setLore(lore);
			item.setItemMeta(meta);

			item.addUnsafeEnchantment(ENCH, 1);

			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("item")) {
			ItemStack item = new ItemStack(Material.DIAMOND);
			ItemStack item2 = new ItemStack(Material.TNT);
			ItemMeta meta2 = item.getItemMeta();
			ItemMeta meta = item.getItemMeta();
			meta2.setDisplayName(ChatColor.AQUA + "무한 TNT");
			meta2.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
			meta.setDisplayName(ChatColor.DARK_PURPLE + "보스 소환");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
			item.setItemMeta(meta);
			item2.setItemMeta(meta2);
			p.getInventory().addItem(item2);
			p.getInventory().addItem(item);
			ItemStack item3 = new ItemStack(Material.SNOW_BLOCK);
			ItemMeta meta3 = item.getItemMeta();
			meta3.setDisplayName(ChatColor.AQUA + "눈사람");
			meta3.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
			item3.setItemMeta(meta3);
			p.getInventory().addItem(item3);

		}
		if (cmd.getName().equalsIgnoreCase("bstart")) {
			BlockStart(p);
			setStart(true);
		}
		if (cmd.getName().equalsIgnoreCase("bstop")) {
			setStart(false);

		}
		if (cmd.getName().equalsIgnoreCase("health")) {
			if (args.length == 2) {
				try {
					try {
						Player p1 = Bukkit.getPlayer(args[0]);
						int health = Integer.parseInt(args[1]);
						if (health >= 0 && health <= 2048) {
							if (health >= 20) {
								p1.setMaxHealth(health);
								p1.setHealth(health);
							} else {
								p1.setHealth(health);
							}
						} else {
							p.sendMessage(ChatColor.RED + "Health must be between 0 and 2048");
						}
					} catch (NullPointerException ignored) {

					}
				} catch (NumberFormatException ignored) {

				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("slime")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("슬라임 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick")) {

			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("번개를 내리는 막대기");
			meta.setLore(Arrays.asList("호바호바호바호바", "한우한우한우"));
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);

		}
		if (cmd.getName().equalsIgnoreCase("block")) {

			ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("다블");
			meta.setLore(Arrays.asList("호바호바호바"));
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick2")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("액체 설치 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick3")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("스켈레톤 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick4")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("좀비주민 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick5")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("마녀 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick6")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("좀비 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick7")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("좋은스켈레톤 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick8")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("철골렘 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("stick9")) {
			ItemStack item = new ItemStack(Material.STICK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("주민 소환 막대기");
			meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("heal")) {
			ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName("즉시 치료 ");
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
		}
		if (cmd.getName().equalsIgnoreCase("skeleton")) {
			Vector vec = p.getLocation().getDirection().multiply(2.0D);
			p.launchProjectile(Arrow.class, vec);
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	private void recipe() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_PURPLE + "호바");
		item.setItemMeta(meta);
		ShapedRecipe r = new ShapedRecipe(item);
		r.shape("&##", "&%#", "&%&");
		r.setIngredient('#', Material.DIAMOND);
		r.setIngredient('%', Material.STICK);
		r.setIngredient('&', Material.IRON_INGOT);
		Bukkit.getServer().addRecipe(r);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Break(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		Material mat = b.getType();
		try {
			if (p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "호바")) {
				if (mat == Material.LOG) {
					for (int y = -255; y <= 255; y++) {
						for (int x = -2; x <= 2; x++) {
							for (int z = -2; z <= 2; z++) {
								Location loc = b.getLocation().add(x, y, z);
								if (loc.getBlock().getType() == mat) {
									loc.getBlock().breakNaturally();
								}
							}
						}
					}
				}
			}
		} catch (NullPointerException ignored) {

		}
	}

	@EventHandler
	public void Chat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		int muteCheck = CONFIG.getInt("World." + "Chat." + "Mute." + name + "." + uuid);
		if (muteCheck == 1) {
			p.sendMessage(ChatColor.RED + "당신은 채팅을 할수없습니다");
			e.setCancelled(true);
		}
	}

	private void LoadEnchantment() {
		try {
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Enchantment.registerEnchantment(ENCH);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void BlockStart(Player p) {
		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (!isStart()) {
					cancel();
					return;
				}
				Material mat = p.getInventory().getItemInHand().getType();
				setMat1(mat);

				Location loc = p.getEyeLocation();
				Vector vec = p.getLocation().getDirection();
				Vector vec2 = vec.add(vec);
				Location front = loc.add(vec).add(vec);
				FallingBlock fall = p.getWorld().spawnFallingBlock(front, new MaterialData(mat));
				fall.setVelocity(vec2);
				fall.setGravity(true);
				fall.setDropItem(false);
				fall.setInvulnerable(true);
				fall.setHurtEntities(false);
				fall.setMetadata("FallingBlock",
						new FixedMetadataValue(Main.getProvidingPlugin(this.getClass()), "fallingblock"));

			}

		}.runTaskTimer(this, 0, 0);
	}
}
