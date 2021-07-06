package me.darrionat.commandcooldown.services;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.interfaces.IBypassService;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class BypassService implements IBypassService {
    /**
     * A set that contains players that can bypass all cooldowns regardless of permissions.
     */
    private final Set<Player> sudoBypass = new HashSet<>();

    @Override
    public void startBypassing(Player p) {
        sudoBypass.add(p);
    }

    @Override
    public void stopBypassing(Player p) {
        sudoBypass.remove(p);
    }

    @Override
    public boolean playerIsBypassing(Cooldown cooldown, Player p) {
        boolean sudo = sudoBypass.contains(p);
        if (cooldown == null) return sudo;
        if (sudo) return true;
        // Build permission
        String label = cooldown.getCommand().getLabel();
        String argsStr = String.join("_", cooldown.getArgs());
        return p.hasPermission("commandcooldown.bypass." + label + "_" + argsStr);
    }
}