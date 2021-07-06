package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.PlayerCooldown;

import java.util.Collection;

public interface ISavedCooldownsRepository extends Repository {
    /**
     * Saves a {@code PlayerCooldown} for later accessing.
     *
     * @param cooldown The cooldown to save.
     */
    void savePlayerCooldown(PlayerCooldown cooldown);

    /**
     * Saves multiple {@code PlayerCooldown} for later accessing.
     *
     * @param cooldowns The cooldowns to save.
     */
    void savePlayerCooldowns(Collection<PlayerCooldown> cooldowns);

    /**
     * Loads all saved cooldowns.
     * <p>
     * If a saved cooldown does not have the proper format, an error will be printed to console.
     *
     * @return All stored cooldowns.
     */
    Collection<PlayerCooldown> loadAllCooldowns();
}