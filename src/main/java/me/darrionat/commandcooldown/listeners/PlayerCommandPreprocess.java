package me.darrionat.commandcooldown.listeners;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.interfaces.IBypassService;
import me.darrionat.commandcooldown.interfaces.ICooldownService;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocess implements Listener {
    private final ICooldownService cooldownService;
    private final IBypassService bypassService;
    private final IMessageService messageService;

    public PlayerCommandPreprocess(CommandCooldownPlugin plugin, ICooldownService cooldownService, IBypassService bypassService, IMessageService messageService) {
        this.cooldownService = cooldownService;
        this.bypassService = bypassService;
        this.messageService = messageService;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSentCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage().replaceFirst("/", "");
        Cooldown cooldown = cooldownService.parseCooldown(message);
        if (cooldown == null) return; // No saved cooldown exists for the sent command.

        Player p = e.getPlayer();
        if (bypassService.playerIsBypassing(cooldown, p)) { // Player is bypassing with command or permission
            messageService.sendBypassMessage(p);
            return;
        }

        if (cooldownService.playerHasCooldown(p, cooldown)) {
            messageService.sendCooldownMessage(p, cooldown, cooldownService.getRemainingCooldown(p, cooldown));
            e.setCancelled(true);
        } else { // Allow execution and give cooldown.
            cooldownService.giveCooldown(p, cooldownService.permissionCooldownChange(p, cooldown));
        }
    }
}