package me.darrionat.commandcooldown.repository;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.PlayerCooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.interfaces.ISavedCooldownsRepository;
import me.darrionat.pluginlib.files.Config;
import me.darrionat.pluginlib.files.ConfigBuilder;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SavedCooldownsRepository implements ISavedCooldownsRepository {
    private final CommandCooldownPlugin plugin;
    private final Config config;
    private FileConfiguration file;
    private static final String LIST_NAME = "savedCooldowns";

    public SavedCooldownsRepository(CommandCooldownPlugin plugin) {
        this.plugin = plugin;
        ConfigBuilder builder = new ConfigBuilder(plugin, SAVED_COOLDOWNS);
        config = builder.build();
        init();
    }

    public void init() {
        file = config.getFileConfiguration();
        if (file.getConfigurationSection(LIST_NAME) == null)
            file.createSection(LIST_NAME);
    }

    @Override
    public void savePlayerCooldown(PlayerCooldown cooldown) {
        List<PlayerCooldown> toSave = loadAllCooldowns();
        toSave.add(cooldown);
        file.set(LIST_NAME, toSave);
        config.save(file);
    }

    @Override
    public void savePlayerCooldowns(Collection<PlayerCooldown> cooldowns) {
        List<PlayerCooldown> toSave = loadAllCooldowns();
        toSave.addAll(cooldowns);
        file.set(LIST_NAME, toSave);
        config.save(file);
    }

    @Override
    public List<PlayerCooldown> loadAllCooldowns() {
        // pc.toString() = uuid/warp/home 2/3456789
        List<PlayerCooldown> toReturn = new ArrayList<>();
        for (String pc : file.getStringList(LIST_NAME)) {
            String[] arr = pc.split(PlayerCooldown.SEP);
            if (arr.length != 4) {
                plugin.getErrorHandler().loadingSavedCooldownsError();
                continue;
            }
            UUID uuid = UUID.fromString(arr[0]);
            String label = arr[1];
            String argsStr = arr[2];
            long end = Long.parseLong(arr[3]);

            SavedCommand command = new SavedCommand(label);
            Cooldown cooldown = new Cooldown(command, argsStr, -1);
            toReturn.add(new PlayerCooldown(uuid, cooldown, end));
        }
        return toReturn;
    }
}