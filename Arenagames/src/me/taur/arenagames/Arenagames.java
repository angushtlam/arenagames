package me.taur.arenagames;

import me.taur.arenagames.admin.CashCommand;
import me.taur.arenagames.admin.CurrencyCommand;
import me.taur.arenagames.admin.PremiumCommand;
import me.taur.arenagames.ffa.FfaDeathListener;
import me.taur.arenagames.ffa.FfaKitSelectorListener;
import me.taur.arenagames.ffa.FfaPlayerListener;
import me.taur.arenagames.ffa.FfaRoomListener;
import me.taur.arenagames.ffa.FfaSignListener;
import me.taur.arenagames.ffa.FfaUtil;
import me.taur.arenagames.fix.ArrowFix;
import me.taur.arenagames.fix.TeleportFix;
import me.taur.arenagames.item.CustomItemListener;
import me.taur.arenagames.item.CustomItemUtil;
import me.taur.arenagames.lfl.LflDeathListener;
import me.taur.arenagames.lfl.LflKitSelectorListener;
import me.taur.arenagames.lfl.LflPlayerListener;
import me.taur.arenagames.lfl.LflRoomListener;
import me.taur.arenagames.lfl.LflSignListener;
import me.taur.arenagames.lfl.LflUtil;
import me.taur.arenagames.perk.Perk;
import me.taur.arenagames.perk.PerkSelectorListener;
import me.taur.arenagames.player.PlayerCommand;
import me.taur.arenagames.player.PlayerDataListener;
import me.taur.arenagames.player.PlayerLoginListener;
import me.taur.arenagames.player.PlayerProfileListener;
import me.taur.arenagames.room.RoomCommand;
import me.taur.arenagames.room.SignCreateListener;
import me.taur.arenagames.room.SignDestroyListener;
import me.taur.arenagames.shop.Shop;
import me.taur.arenagames.shop.ShopSignListener;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Arenagames extends JavaPlugin {
	
	public static Arenagames plugin;

	@Override
	public void onEnable() {
		plugin = this;
		
		Config.startCheck();
		
		regCmd("queue", new RoomCommand());
		regCmd("player", new PlayerCommand());
		regCmd("premium", new PremiumCommand());
		regCmd("curr", new CurrencyCommand());
		regCmd("cash", new CashCommand());
		loadGamemodes();
		
		regEvent(new PlayerLoginListener());
		regEvent(new SignCreateListener());
		regEvent(new SignDestroyListener());
		
		regEvent(new PlayerDataListener());
		regEvent(new PlayerProfileListener());
		
		regEvent(new ShopSignListener());
		Shop.enable();
		
		regEvent(new PerkSelectorListener());
		Perk.enable();
		
		regEvent(new CustomItemListener());
		CustomItemUtil.enable();
		
		regEvent(new ArrowFix());
		regEvent(new TeleportFix());
		
		Scheduler.start();
	}

	@Override
	public void onDisable() {
		
	}
	
	public static void loadGamemodes() {
		String[] gm = Config.gamemode;
		
		for (int i = 0; i < gm.length; i++) {
			if (gm[i].contains("ffa")) {
				FfaUtil.enable();
				regFfa();
				
			}
			
			if (gm[i].contains("lfl")) {
				LflUtil.enable();
				regLfl();
				
			}
		}
	}
	
	public static void regEvent(Listener file) {
		Bukkit.getServer().getPluginManager().registerEvents(file, Arenagames.plugin);
	}

	public static void regCmd(String cmd, CommandExecutor exe) {
		Bukkit.getServer().getPluginCommand(cmd).setExecutor(exe);
	}

	private static void regFfa() {		
		regEvent(new FfaDeathListener());
		regEvent(new FfaKitSelectorListener());
		regEvent(new FfaPlayerListener());
		regEvent(new FfaSignListener());
		regEvent(new FfaRoomListener());
		
	}
	
	private static void regLfl() {
		regEvent(new LflDeathListener());
		regEvent(new LflKitSelectorListener());
		regEvent(new LflPlayerListener());
		regEvent(new LflSignListener());
		regEvent(new LflRoomListener());
		
	}
}