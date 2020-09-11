package me.Darrionat.CommandCooldown.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.Darrionat.CommandCooldown.Command;
import me.Darrionat.CommandCooldown.CommandCooldown;

public class AddCommandEditor implements Listener {

	private CommandCooldown plugin;
	private MessageService messages;

	public AddCommandEditor(CommandCooldown plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		messages = new MessageService(plugin);
	}

	// Sets up the players command for saving
	public static HashMap<UUID, Command> commandUUIDMap = new HashMap<>();

	// Indicator that the player is ready to send a label in chat
	public static Set<UUID> awaitingLabelSet = new HashSet<>();

	// Recieves label /cc add (Part 1)
	@EventHandler
	public void onLabelMessage(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String sentMessage = e.getMessage();

		if (!awaitingLabelSet.contains(uuid))
			return;

		e.setCancelled(true);

		if (sentMessage.contains(" ")) {
			p.sendMessage(messages.getMessage(messages.notACommandLabel));
			return;
		}

		awaitingLabelSet.remove(uuid);
		awaitingCooldownSet.add(uuid);
		commandUUIDMap.put(uuid, new Command(sentMessage, plugin));

		String addCooldownMessage = messages.getMessage(messages.addCooldown);
		addCooldownMessage = addCooldownMessage.replace("%command%", "/" + sentMessage);
		p.sendMessage(addCooldownMessage);

	}

	// Indicator that the player is ready to send a cooldown in chat
	public static Set<UUID> awaitingCooldownSet = new HashSet<>();

	// Recieves cooldown /cc add (Part 2)
	@EventHandler
	public void onCooldownMessage(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String sentMessage = e.getMessage();

		if (!awaitingCooldownSet.contains(uuid))
			return;

		e.setCancelled(true);

		if (sentMessage.contains(" ")) {
			p.sendMessage(messages.getMessage(messages.notACooldown));
			return;
		}

		double cooldown;
		try {
			cooldown = Double.parseDouble(sentMessage);
		} catch (NumberFormatException exe) {
			p.sendMessage(messages.getMessage(messages.notACooldown));
			return;
		}

		Command command = commandUUIDMap.get(uuid);
		command.cooldown = cooldown;
		command.save();

		awaitingCooldownSet.remove(uuid);
		commandUUIDMap.remove(uuid);

		String createdCooldownMessage = messages.getMessage(messages.createdCooldown);
		createdCooldownMessage = createdCooldownMessage.replace("%command%", "/" + command.label);
		createdCooldownMessage = createdCooldownMessage.replace("%cooldown%", String.valueOf(command.cooldown));
		p.sendMessage(createdCooldownMessage);
	}

}
