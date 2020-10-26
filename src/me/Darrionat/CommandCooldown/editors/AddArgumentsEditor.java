package me.Darrionat.CommandCooldown.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.Darrionat.CommandCooldown.Command;
import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.handlers.MessageService;
import me.Darrionat.CommandCooldown.utils.Utils;

public class AddArgumentsEditor implements Listener, Editor {

	private CommandCooldown plugin;
	private MessageService messages;

	public AddArgumentsEditor(CommandCooldown plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		messages = new MessageService(plugin);

		queueSetsList.add(awaitingLabelSet);
		queueSetsList.add(awaitingArgumentsSet);
		queueSetsList.add(awaitingCooldownSet);
	}

	// Contains all queue sets
	private List<Set<UUID>> queueSetsList = new ArrayList<>();

	// Sets up the players command for saving
	public static HashMap<UUID, Command> commandUUIDMap = new HashMap<>();

	// Indicator that the player is ready to send a label in chat
	public static Set<UUID> awaitingLabelSet = new HashSet<>();

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
		awaitingArgumentsSet.add(uuid);
		commandUUIDMap.put(uuid, new Command(sentMessage, plugin));

		String addArugmentsMessage = messages.getMessage(messages.addArguments);
		addArugmentsMessage = addArugmentsMessage.replace("%command%", "/" + sentMessage);
		p.sendMessage(addArugmentsMessage);
		addedLabelTime = System.currentTimeMillis();
	}

	/**
	 * Used to prevent the AsyncPlayerChatEvent from firing both in
	 * onLabelMessage(AsyncPlayerChatEvent) and onArgumentsMessage()
	 */
	private long addedLabelTime = 0;
	// Indicator that the player is ready to send the arguments in chat
	public static Set<UUID> awaitingArgumentsSet = new HashSet<>();

	@EventHandler
	public void onArgumentsMessage(AsyncPlayerChatEvent e) {
		if (addedLabelTime + 5 > System.currentTimeMillis()) {
			return;
		}
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String sentMessage = e.getMessage();

		if (!awaitingArgumentsSet.contains(uuid))
			return;

		e.setCancelled(true);

		String[] args = Utils.getArgs(sentMessage);
		if (args == null) {
			p.sendMessage(messages.getMessage(messages.notArguments));
			return;
		}

		awaitingArgumentsSet.remove(uuid);
		awaitingCooldownSet.add(uuid);

		Command command = commandUUIDMap.get(uuid);
		command.args = args;
		commandUUIDMap.put(uuid, command);

		String addCooldownToArgsMessage = messages.getMessage(messages.addCooldownToArguments);
		addCooldownToArgsMessage = addCooldownToArgsMessage.replace("%label%", command.label);
		addCooldownToArgsMessage = addCooldownToArgsMessage.replace("%arguments%", sentMessage);
		p.sendMessage(addCooldownToArgsMessage);
	}

	// Indicator that the player is ready to send a cooldown in chat
	public static Set<UUID> awaitingCooldownSet = new HashSet<>();

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

		String argsString = String.join(" ", command.args);

		String createdArgsCooldownMessage = messages.getMessage(messages.createdArgsCooldown);
		createdArgsCooldownMessage = createdArgsCooldownMessage.replace("%arguments%", argsString);
		createdArgsCooldownMessage = createdArgsCooldownMessage.replace("%label%", command.label);
		createdArgsCooldownMessage = createdArgsCooldownMessage.replace("%cooldown%", String.valueOf(command.cooldown));
		p.sendMessage(createdArgsCooldownMessage);
	}

	@Override
	public List<Set<UUID>> getQueueSets() {
		return queueSetsList;
	}

	@Override
	public HashMap<UUID, Command> getCommandMap() {
		return commandUUIDMap;
	}

}