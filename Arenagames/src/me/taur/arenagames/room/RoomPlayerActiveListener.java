package me.taur.arenagames.room;

import me.taur.arenagames.Config;
import me.taur.arenagames.ffa.FfaRoom;
import me.taur.arenagames.util.RoomType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class RoomPlayerActiveListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerLoggedIn(PlayerJoinEvent evt) {
		evt.setJoinMessage(""); // I hate this.
		
		Player p = evt.getPlayer();
		p.getInventory().setArmorContents(null);
		p.getInventory().clear();
		
		for (PotionEffect effect : p.getActivePotionEffects()) {
		    p.removePotionEffect(effect.getType());
		}
		
		p.setLevel(0);
		p.setExp((float) 0.0);
		p.teleport(Config.getGlobalLobby()); // Teleport people to lobby when they join
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerLoggedOff(PlayerQuitEvent evt) {
		evt.setQuitMessage(""); // I hate this.
		
		Player p = evt.getPlayer();
		
		if (Room.PLAYERS.containsKey(p)) {
			Room room = Room.ROOMS.get(Room.PLAYERS.get(p));
			
			room.removePlayer(p);
			Room.PLAYERS.remove(p);

			if (room.isGameInProgress()) {
				if (room.getPlayers()[0] != null) {
					for (Player other : room.getPlayers()) {
						other.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + " has left this game.");
					}
				}
				
				if (room.getPlayersInRoom() == 0) {
					Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + room.getRoomId() + " queue has reopened.");
					
					if (room.getRoomType() == RoomType.FFA) {
						FfaRoom r = (FfaRoom) room;
						r.getScoreboard().remove(p);
						r.resetRoom(true);
					}
				}
				
			} else { // Left the queue
				if (room.getPlayers() != null) {
					for (Player other : room.getPlayers()) {
						if (other != null) {
							other.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + p.getName() + " has left this queue.");
						}
					}
				}
				
				// Check if there are enough people in the room.
				int needed = room.getPlayersInRoom();
				if (needed > Config.getMinPlayersInWait(RoomType.FFA) - 1) {
					if (!room.isGameInWaiting()) {
						room.waitStartMessage(RoomType.FFA);
						room.setGameInWaiting(true);
						room.setWaitTimer(Config.getWaitTimer(RoomType.FFA));
					
					} else {
						room.waitStartMessage(p, RoomType.FFA);
						
					}
				} else {
					room.waitCancelledMessage(RoomType.FFA);
					room.setGameInWaiting(false);
					
				}
				
			}
			
			if (room.getRoomType() == RoomType.FFA) {
				FfaRoom r = (FfaRoom) room;
				r.updateSigns();
				r.getScoreboard().remove(p.getName()); // Remove the player from the scoreboard.
				
			}
		}
	}
}
