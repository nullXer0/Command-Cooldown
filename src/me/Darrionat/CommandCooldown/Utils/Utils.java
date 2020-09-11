package me.Darrionat.CommandCooldown.utils;

import java.util.regex.PatternSyntaxException;

import org.bukkit.ChatColor;

public class Utils {

	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static String[] getArgs(String message) {
		try {
			return message.split(" ");
		} catch (PatternSyntaxException exe) {
			return null;
		}
	}

	public static String getDurationString(long totalSeconds) {
		double days = Math.floor(totalSeconds / 86400);
		double hours = Math.floor(totalSeconds % 86400 / 3600);
		double minutes = Math.floor(totalSeconds % 3600 / 60);
		double seconds = Math.floor(totalSeconds % 60);

		String duration = days + "d " + hours + "h " + minutes + "m " + seconds + "s";

		if (days < 1) {
			duration = duration.replace(days + "d ", "");
			if (hours < 1) {
				duration = duration.replace(hours + "h ", "");
				if (minutes < 1) {
					duration = duration.replace(minutes + "m ", "");
				}
			}
		}
		return duration;
	}
}