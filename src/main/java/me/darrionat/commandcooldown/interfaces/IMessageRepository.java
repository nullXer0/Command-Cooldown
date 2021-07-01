package me.darrionat.commandcooldown.interfaces;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface IMessageRepository extends Repository {
    /**
     * Gets a message from the messages config.
     *
     * @param key The key of the message in the config.
     * @return The message.
     */
    String getMessage(String key);

    ConfigurationSection getConfigurationSection(String key);

    List<String> getList(String key);
}