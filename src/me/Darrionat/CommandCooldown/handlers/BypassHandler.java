package me.Darrionat.CommandCooldown.handlers;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.Command;

public class BypassHandler {

	public static ArrayList<UUID> bypassList = new ArrayList<UUID>();

	public static boolean playerIsBypassing(Command command, Player p) {

		// Command Bypass
		ArrayList<UUID> bypassList = BypassHandler.bypassList;
		if (bypassList.contains(p.getUniqueId())) {
			return true;
		}

		// Permission bypass
		String message = command.message;
		if (p.hasPermission("commandcooldown.bypass." + message.replace(" ", "_"))) {
			return true;
		}

		return false;
	}
}