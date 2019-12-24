package me.Darrionat.CommandCooldown;

import org.bukkit.plugin.java.JavaPlugin;

import me.Darrionat.CommandCooldown.Commands.CommandControl;
import me.Darrionat.CommandCooldown.Listeners.CommandProcess;
import me.Darrionat.CommandCooldown.bStats.Metrics;

public class Main extends JavaPlugin {

	public void onEnable() {
		new CommandProcess(this);
		new CommandControl(this);
		saveDefaultConfig();
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
	}

	public void onDisable() {

	}

}
