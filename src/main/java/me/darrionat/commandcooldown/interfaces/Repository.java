package me.darrionat.commandcooldown.interfaces;

/**
 * A simple interface used for repositories.
 */
public interface Repository {
    String CONFIG = "config.yml";
    String MESSAGES = "messages.yml";
    String COOLDOWNS = "cooldowns.yml";
    String SAVED_COOLDOWNS = "savedCooldowns.yml";

    void init();
}