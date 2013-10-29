package me.taur.arenagames.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import me.taur.arenagames.Arenagames;
import me.taur.arenagames.Config;
import me.taur.arenagames.ffa.FfaConfig;
import me.taur.arenagames.ffa.FfaRoom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RoomScheduler {
	
	public static void start() {
		
		Bukkit.getScheduler().runTaskLater(Arenagames.plugin, new Runnable() {
			public void run() {
				start(); // Loops the scheduler.
				
				// Get each room in stored Rooms.
				for (String s : Room.ROOMS.keySet()) {
					Room room = Room.ROOMS.get(s);
					
					if (room.isGameInProgress()) {
						int countdown = room.getCountdownTimer();
						
						if (countdown > -1) {
							room.setCountdownTimer(countdown - 1);
							
							if (countdown == 0) {
								if (room.getRoomType() == RoomType.FFA) {
									FfaRoom vroom = (FfaRoom) room;
									vroom.gameOverMessage(vroom.getWinningPlayer());
									
									for (Player p : vroom.getPlayers()) {
										p.teleport(FfaConfig.getLobby());
										Room.PLAYERS.remove(p);
										
									}
									
									vroom.resetRoom(true);
									
								}
								
								continue;
							}
						}
						
						// Message players about time remaining every minute
						if (countdown % 60 == 0) {
							if (countdown != 0) { // If the game isn't over
								for (Player p : room.getPlayers()) {
									int minute = countdown / 60;
									p.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + minute + " minute" + (minute == 1 ? "" : "s") + " remaining.");
									
								}
							}
						}
					}
					
					if (room.isGameInWaiting()) {
						int waitcount = room.getWaitTimer();
						
						if (waitcount > -1) {
							room.setWaitTimer(waitcount - 1);
							
							if (waitcount == 0) { // Game start
								// What happens when the room is Free-For-All
								if (room.getRoomType() == RoomType.FFA) {
									FfaRoom r = (FfaRoom) room;
									
									ConfigurationSection cs = FfaConfig.get().getConfigurationSection("ffa.maps");
									if (cs != null) {
										Set<String> maps = cs.getKeys(false);
										
										// Fancy loop to make sure rooms are available and only 1 queue can join 1 arena.
										int tries = 0;
										Set<Integer> alreadyused = new HashSet<Integer>();
										
										boolean breakloop = false;
										while (!breakloop) {
											if (tries == maps.size()) {
												room.setGameInWaiting(false);
												room.setGameInProgress(false);
												
												for (Player p : r.getPlayers()) {
													p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Currently all of the Free For All arenas are in progress.");
													p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Please wait until an arena frees up.");
													
												}
												
												// Restart the wait timer.
												int needed = room.getPlayersInRoom();
												if (needed > Config.getMinPlayersInWait(RoomType.FFA)) {
													if (!room.isGameInWaiting()) {
														room.waitStartMessage(RoomType.FFA);
														room.setGameInWaiting(true);
														room.setWaitTimer(Config.getWaitTimer(RoomType.FFA));
													
													}
												} else {
													room.waitCancelledMessage(RoomType.FFA);
													
												}
												
												breakloop = true;
												
											}
											
											Random rand = new Random();
											int map = 0;
											
											boolean tryfornew = true;
											while (tryfornew) {
												map = rand.nextInt(maps.size());
												
												if (!alreadyused.contains(map)) { // If the number is already checked, loop again.
													boolean premium = r.isPremium();
													String mapname = ((String) maps.toArray()[map]);
													
													if (premium) { // If the queue is premium
														if (!FfaConfig.canPremiumPlayMap(mapname)) { // If the premium room cannot play the map
															alreadyused.add(map);
															tries++;
															continue;
															
														}
													} else {
														if (!FfaConfig.canNormalPlayMap(mapname)) { // If the normal room cannot play the map
															alreadyused.add(map);
															tries++;
															continue;
															
														}
													}
													
													boolean match = false; // Make sure arenas are not used by more than 1 queue
													for (Room vroom : Room.ROOMS.values()) {
														if (vroom.getRoomType() == RoomType.FFA) {
															FfaRoom froom = (FfaRoom) vroom;
															if (froom.getMapName() == maps.toArray()[map]) {
																match = true;
																
															}
														}
													}
													
													if (!match) { // Block of code to run when the game is really starting.
														r.setMapName((String) maps.toArray()[map]);
														r.startGame();
														room.setGameInWaiting(false);
														room.setGameInProgress(true);
														breakloop = true;
														tryfornew = false;
														
													}
													
													alreadyused.add(map);
													tries++;
													
												}
											}
										}
										
									} else {
										Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.ITALIC + "An error has occured in " + room.getRoomId() + ": Maps cannot be loaded.");
										Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Free For All match " + room.getRoomId() + " has ended.");
										
										r.resetRoom(true);
										
									}
								}
								
								continue;
								
							}
						}
						
						// Message players about time remaining after 30 seconds every 10 seconds.
						if (waitcount < 31 && waitcount % 10 == 0) {
							if (waitcount != 0) { // If the game isn't over
								for (Player p : room.getPlayers()) {
									p.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + waitcount + " second" + (waitcount == 1 ? "" : "s") + " until game starts.");
									
								}
							}
						}
					}
				}
			}
		}, 20L);
	}
}
