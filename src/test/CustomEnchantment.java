package test;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantment extends Enchantment implements Listener{
	
	public CustomEnchantment(int id) {
		super(id);
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e)  {
		if(e.getDamager() instanceof Player) {
		Player p = (Player) e.getDamager();
		Entity en = e.getEntity();
		ItemStack mainhand = p.getInventory().getItemInMainHand();
		if(mainhand.containsEnchantment(this)) {
			try {
			p.getWorld().strikeLightning(en.getLocation());
			}catch(Exception e1) {
				e1.printStackTrace();
			}
		}
		}
	}
	
	@Override
	public int getId() {
		return 101;
	}
	
	@Override
	public boolean canEnchantItem(ItemStack arg0) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment arg0) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "Lightning Touch";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}
}
