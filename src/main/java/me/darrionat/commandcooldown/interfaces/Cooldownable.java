package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.Cooldown;

import java.util.List;

/**
 * Represents an object that is able to have a {@link Cooldown}.
 */
public interface Cooldownable {
    /**
     * Gets the base {@link Cooldown}.
     *
     * @return Returns the default cooldown.
     */
    Cooldown getBaseCooldown();

    /**
     * Add a cooldown.
     *
     * @param cooldown The cooldown to add.
     */
    void addCooldown(Cooldown cooldown);

    /**
     * Removes a cooldown.
     *
     * @param cooldown The cooldown to remove.
     */
    void removeCooldown(Cooldown cooldown);

    /**
     * Gets all cooldowns, including the base cooldown.
     *
     * @return The cooldowns.
     * @see #getBaseCooldown()
     */
    List<Cooldown> getCooldowns();
}