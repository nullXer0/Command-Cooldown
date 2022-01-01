package me.darrionat.commandcooldown.listeners;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.interfaces.IBypassService;
import me.darrionat.commandcooldown.interfaces.IConfigRepository;
import me.darrionat.commandcooldown.interfaces.ICooldownService;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocess implements Listener {
    private final ICooldownService cooldownService;
    private final IConfigRepository configRepo;
    private final IBypassService bypassService;
    private final IMessageService messageService;

    public PlayerCommandPreprocess(CommandCooldownPlugin plugin, IConfigRepository configRepo, ICooldownService cooldownService, IBypassService bypassService, IMessageService messageService) {
        this.cooldownService = cooldownService;
        this.configRepo = configRepo;
        this.bypassService = bypassService;
        this.messageService = messageService;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSentCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage().replaceFirst("/", "");
        Cooldown cooldown = cooldownService.parseCooldown(message);
        if (cooldown == null) return; // No saved cooldown exists for the sent command.

        Player p = e.getPlayer();
        // Player is bypassing with command or permission
        if (bypassService.playerIsBypassing(cooldown, p)) {
            if (configRepo.sendBypassMessage())
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