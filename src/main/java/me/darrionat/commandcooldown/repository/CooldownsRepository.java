package me.darrionat.commandcooldown.repository;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.interfaces.ICooldownsRepository;
import me.darrionat.commandcooldown.utils.Duration;
import me.darrionat.pluginlib.files.Config;
import me.darrionat.pluginlib.files.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CooldownsRepository implements ICooldownsRepository {
    private final CommandCooldownPlugin plugin;
    private final Config config;
    private final List<SavedCommand> savedCommands = new ArrayList<>();
    private FileConfiguration file;

    public CooldownsRepository(CommandCooldownPlugin plugin) {
        this.plugin = plugin;
        ConfigBuilder builder = new ConfigBuilder(plugin, COOLDOWNS);
        builder.useBuiltInFile();
        config = builder.build();
        init();
    }

    public void init() {
        file = config.getFileConfiguration();
        savedCommands.clear();
    }

    public List<SavedCommand> getCommandCooldowns() {
        if (!savedCommands.isEmpty())
            return savedCommands;
        for (String label : file.getKeys(false))
            savedCommands.add(getSavedCommand(label));
        return savedCommands;
    }

    /**
     * Loads a saved command from the config.
     *
     * @param label The command to fetch.
     * @return The loaded {@code SavedCommand}.
     */
    private SavedCommand getSavedCommand(String label) {
        SavedCommand command = new SavedCommand(label);

        ConfigurationSection section = file.getConfigurationSection(label);
        for (String args : section.getConfigurationSection("cooldowns").getKeys(false)) {
            double duration;
            try {
                duration = Duration.parseDuration(section.getString("cooldowns." + args));
            } catch (NumberFormatException e) {
                plugin.log("&cInvalid cooldown for /" + label + " " + args);
                continue;
            }
            Cooldown cd = new Cooldown(command, args, duration);
            command.addCooldown(cd);
        }
        section.getStringList("aliases").forEach(command::addAlias);
        return command;
    }

    @Override
    public void addCommandCooldown(SavedCommand command) {
        savedCommands.add(command);
        String label = command.getLabel();
        file.createSection(label);
        ConfigurationSection section = file.getConfigurationSection(label);
        // Add cooldowns to the section
        for (Cooldown cooldown : command.getCooldowns()) {
            String args = String.join("", cooldown.getArgs());
            section.set("cooldowns." + args, cooldown.getDuration());
        }
        // Add the aliases
        section.set("aliases", command.getAliases());
        config.save(file);
    }

    @Override
    public void removeCommandCooldown(SavedCommand command) {
        savedCommands.remove(command);
        file.set(command.getLabel(), null);
        config.save(file);
    }
}