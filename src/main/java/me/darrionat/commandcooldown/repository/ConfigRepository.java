package me.darrionat.commandcooldown.repository;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.interfaces.IConfigRepository;
import me.darrionat.pluginlib.files.Config;
import me.darrionat.pluginlib.files.ConfigBuilder;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigRepository implements IConfigRepository {
    private final Config config;
    private FileConfiguration file;

    public ConfigRepository(CommandCooldownPlugin plugin) {
        ConfigBuilder builder = new ConfigBuilder(plugin, CONFIG);
        builder.useBuiltInFile();
        builder.updateConfig();
        config = builder.build();
        init();
    }

    public void init() {
        file = config.getFileConfiguration();
    }

    @Override
    public boolean checkForUpdates() {
        return file.getBoolean("checkUpdates");
    }

    @Override
    public boolean sendBypassMessage() {
        return file.getBoolean("sendBypassMessage");
    }
}