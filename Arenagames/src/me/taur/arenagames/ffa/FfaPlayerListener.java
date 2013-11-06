package me.taur.arenagames.ffa;

import me.taur.arenagames.Config;
import me.taur.arenagames.room.Room;
import me.taur.arenagames.util.RoomType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FfaPlayerListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerLoggedOff(PlayerQuitEvent evt) {
		Player p = evt.getPlayer();

		if (Room.PLAYERS.containsKey(p)) {
			Room room = Room.ROOMS.get(Room.PLAYERS.get(p));

			if (room.getRoomType() == RoomType.FFA) {
				room.removePlayer(p); // Only remove the gamemode's own players
				Room.PLAYERS.remove(p);
				
				FfaRoom r = (FfaRoom) room;
				if (room.isGameInProgress()) {
					if (room.getPlayers()[0] != null) {
						for (Player other : room.getPlayers()) {
							other.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + " has left this game.");
						}
					}

					if (room.getPlayersInRoom() == 0) {
						Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + room.getRoomId() + " queue has reopened.");
						
						r.getScoreboard().remove(p);
						r.resetRoom(true);
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
				
				r.updateSigns();
				r.getScoreboard().remove(p.getName()); // Remove the player from the scoreboard.

			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerDropItem(PlayerDropItemEvent evt) {
		Player p = evt.getPlayer();
		
		if (!Room.PLAYERS.containsKey(p)) {
			return;
			
		}
		
		Room room = Room.ROOMS.get(Room.PLAYERS.get(p));
		if (room != null) {
			if (room.getRoomType() == RoomType.FFA) {
				evt.setCancelled(true);
				
			}
			
		}
	}
}