package me.taur.arenagames.room;

import me.taur.arenagames.ffa.FfaConfig;
import me.taur.arenagames.ffa.FfaRoom;
import me.taur.arenagames.util.RoomType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void adminPlaceSign(SignChangeEvent evt) {
		Block b = evt.getBlock();
		Player p = evt.getPlayer();
		String l0 = evt.getLine(0);
		
		if (l0.equals("[FFA]")) {
			if (!p.hasPermission("arenagames.admin")) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have no permission.");
				b.breakNaturally();
				return;
				
			}
			String l1 = evt.getLine(1).toLowerCase();
			if (!Room.ROOMS.containsKey(l1)) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have inserted an incorrect queue ID.");
				b.breakNaturally();
				return;
				
			}
			
			if (!l1.startsWith("ffa-")) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have inserted an non-FFA queue ID.");
				b.breakNaturally();
				return;
				
			}
			
			Room r = Room.ROOMS.get(l1);
			if (r == null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "An error occurred: Queue does not exist.");
				b.breakNaturally();
				return;
				
			}
			
			if (r.getRoomType() != RoomType.FFA) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "An error occurred: Queue is not a FFA queue.");
				b.breakNaturally();
				return;
				
			}
			
			Location signloc = b.getLocation();
			Location[] locs = FfaConfig.getSignsStored(r.getRoomId());
			boolean match = false;
			
			if (locs != null) {
				for (Location loc : locs) {
					if (loc != null) {
						Block lb = loc.getBlock();
						if (lb.getType().name().contains("SIGN")) {
							Sign sign = (Sign) b.getState();
							
							if (!sign.getLine(0).contains("[FFA]")) {
								continue;
								
							}
							
							if (sign.getLine(1).equals(l1)) {
								match = true;
								break;
								
							}
						}
					}
				}
			}
			
			if (!match) { // If the sign doesn't exist in the config
				ConfigurationSection signs = FfaConfig.getSigns(r.getRoomId());
				if (signs != null) {
					int size = signs.getKeys(false).size(); // Size is 1 higher of the sign's id
					
					World world = signloc.getWorld();
					double x = signloc.getBlockX();
					double y = signloc.getBlockY();
					double z = signloc.getBlockZ();
					Location saveloc = new Location(world, x, y, z);
					
					FfaConfig.setSignLocation(r.getRoomId(), size, saveloc);
					
				} else {
					World world = signloc.getWorld();
					double x = signloc.getBlockX();
					double y = signloc.getBlockY();
					double z = signloc.getBlockZ();
					Location saveloc = new Location(world, x, y, z);
					
					FfaConfig.setSignLocation(r.getRoomId(), 0, saveloc);
					
				}
			}
			
			p.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "You have created a queue sign for " + r.getRoomId() + ".");
			
			FfaRoom room = (FfaRoom) r;
			room.updateSigns();
			
		}
		
	}

}
