package me.Darrionat.CommandCooldown.Utils;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class Utils {

	static ArrayList<String> bypassList = new ArrayList<String>();
	
	public static ArrayList<String> getBypassList() {
		return bypassList;
	}
	
	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);

	}

}