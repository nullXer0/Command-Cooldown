package me.darrionat.commandcooldown.listeners;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.interfaces.IConfigRepository;
import me.darrionat.pluginlib.utils.SpigotMCUpdateHandler;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final CommandCooldownPlugin plugin;
    private final SpigotMCUpdateHandler updater;

    public PlayerJoin(CommandCooldownPlugin plugin, IConfigRepository configRepo) {
        this.plugin = plugin;
        this.updater = plugin.getUpdater();
        if (!configRepo.checkForUpdates()) return;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.isOp()) return;

        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            if (!updater.updateAvailable()) return;
            p.sendMessage(Utils.toColor("&eUpdate available! &b" + plugin.getName() + " is currently on " + plugin.getDescription().getVersion()));
            p.sendMessage(Utils.toColor("&bDownload the newest version here!"));
            p.sendMessage(Utils.toColor(updater.getResourceURL()));
        }, 30L);// 30 ticks delay

    }
}