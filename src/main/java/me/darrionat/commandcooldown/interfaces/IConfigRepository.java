package me.darrionat.commandcooldown.interfaces;

public interface IConfigRepository extends Repository {
    /**
     * Determines if to check for available updates.
     *
     * @return {@code true} if the plugin should check for available updates; {@code false} otherwise.
     */
    boolean checkForUpdates();

    /**
     * Determines if to send a message to a player who bypasses cooldowns.
     *
     * @return {@code true} if a player should be informed when they bypass a cooldown;
     * {@code false} otherwise.
     */
    boolean sendBypassMessage();
}