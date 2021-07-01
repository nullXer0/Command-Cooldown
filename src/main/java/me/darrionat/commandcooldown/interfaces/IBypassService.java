package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import org.bukkit.entity.Player;

/**
 * Represents a collection of players who are able to bypass cooldowns.
 * <p>
 * Players can bypass cooldowns with the bypass command or with a permission.
 * <p>
 * Players who use the bypass command will bypass all cooldowns; otherwise,
 * players need a specific permission to bypass a cooldown.
 */
public interface IBypassService extends Service {
    /**
     * Allows a player to bypass all cooldowns.
     *
     * @param p The player to allow bypassing.
     */
    void startBypassing(Player p);

    /**
     * Stops a player from bypassing all cooldowns.
     *
     * @param p The player to stop bypassing.
     */
    void stopBypassing(Player p);

    /**
     * Determines if a player is bypassing a cooldown by using the bypass command or permission.
     *
     * @param cooldown The cooldown to check if the player is bypassing.
     * @param p        The player.
     * @return {@code true} if the player is bypassing the cooldown; {@code false} otherwise.
     */
    boolean playerIsBypassing(Cooldown cooldown, Player p);
}