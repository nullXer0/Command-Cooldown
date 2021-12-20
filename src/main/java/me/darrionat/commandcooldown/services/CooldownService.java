package me.darrionat.commandcooldown.services;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.PlayerCooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.interfaces.ICooldownService;
import me.darrionat.commandcooldown.interfaces.ICooldownsRepository;
import me.darrionat.commandcooldown.interfaces.ISavedCooldownsRepository;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

public class CooldownService implements ICooldownService {
    private final ICooldownsRepository cooldownsRepo;
    private final ISavedCooldownsRepository savedCooldownsRepo;
    /**
     * The active cooldowns for players.
     * <p>
     * The key is the player's UUID and the value is the time that the cooldown expires.
     */
    private final Set<PlayerCooldown> cooldowns = new HashSet<>();

    public CooldownService(ICooldownsRepository cooldownsRepo, ISavedCooldownsRepository savedCooldownsRepo) {
        this.cooldownsRepo = cooldownsRepo;
        this.savedCooldownsRepo = savedCooldownsRepo;
    }

    private PlayerCooldown buildPlayerCooldown(Player p, Cooldown cooldown, long end) {
        return new PlayerCooldown(p.getUniqueId(), cooldown, end);
    }

    @Override
    public Cooldown parseCooldown(String s) {
        // take into account "warp shop a aa a"
        String[] message = s.toLowerCase().split(" ");
        String label = message[0];
        List<String> args = new ArrayList<>(Arrays.asList(message));
        // Remove label
        args.remove(0);
        for (SavedCommand savedCommand : cooldownsRepo.getCommandCooldowns()) {
            // If the label and aliases don't contain the sent label, continue
            if (!label.equalsIgnoreCase(savedCommand.getLabel())
                    && !savedCommand.getAliases().contains(label)) continue;

            Cooldown cooldown = getClosestCooldown(savedCommand, args);
            if (cooldown == null)
                return savedCommand.getBaseCooldown();
            return cooldown;
        }
        // No saved command exists
        return null;
    }

    /**
     * Gets the closest cooldown to the passed arguments.
     *
     * @param savedCommand The command with cooldowns to compare against.
     * @param args         The arguments that were sent.
     * @return The closest matching arguments and cooldown to the sent arguments; the base cooldown if no matches.
     */
    private Cooldown getClosestCooldown(SavedCommand savedCommand, List<String> args) {
        Cooldown mostMatching = null;
        int mostMatchingAmt = 0;

        for (Cooldown cooldown : savedCommand.getCooldowns()) {
            if (cooldown.isBaseCooldown()) continue;
            // Track if matched and the amount of matched arguments
            boolean matched = true;
            int amtMatched = 0;
            List<String> cooldownArgs = cooldown.getArgs();
            // Loop over arguments. All need to match to be valid
            for (int i = 0; i < cooldownArgs.size() && i < args.size(); i++) {
                String arg = cooldownArgs.get(i);
                // Sent arguments don't match defined cooldown arguments
                // Incompatible cooldown arguments

                if (!arg.equalsIgnoreCase(args.get(i)) && !arg.equalsIgnoreCase("*")) {
                    matched = false;
                    break;
                }
                amtMatched++;
            }
            // If all arguments of the cooldown are contained and
            // the amount matched is greater, than it's the best match.
            if (matched && amtMatched > mostMatchingAmt) {
                mostMatching = cooldown;
                mostMatchingAmt = amtMatched;
            }
        }
        return mostMatching;
    }

    @Override
    public void giveCooldown(Player p, Cooldown cooldown) {
        if (cooldown == null) return;
        long current = System.currentTimeMillis();
        long cooldownMS = (long) (cooldown.getDuration() * 1000);
        long end = current + cooldownMS;
        cooldowns.add(buildPlayerCooldown(p, cooldown, end));
    }

    @Override
    public void removePlayerCooldowns(Player p) {
        List<PlayerCooldown> toRemove = new ArrayList<>();
        for (PlayerCooldown cooldown : cooldowns) {
            if (cooldown.getPlayer().equals(p.getUniqueId()))
                toRemove.add(cooldown);
        }
        toRemove.forEach(cooldowns::remove);
    }

    @Override
    public double getRemainingCooldown(Player p, Cooldown cooldown) {
        PlayerCooldown cd = getPlayerCooldown(p, cooldown);
        if (cd == null) return 0;
        long end = cd.getEnd();
        long rem = end - System.currentTimeMillis();
        return Math.max(rem / 1000, 0);
    }

    @Override
    public boolean playerHasCooldown(Player p, Cooldown cooldown) {
        PlayerCooldown cd = getPlayerCooldown(p, cooldown);
        if (cd == null) return false;
        boolean expired = cd.expired();
        // Cooldown is expired, remove from list
        if (expired)
            cooldowns.remove(cd);
        return !expired;
    }

    @Override
    public Cooldown permissionCooldownChange(Player p, Cooldown cooldown) {
        // Clone to avoid changing the original cooldown
        Cooldown clone = cooldown.clone();
        // Build permission
        // commandcooldown.commandPerm.duration
        String label = cooldown.getCommand().getLabel();
        // If it's a base cooldown, command perm is just the label.
        String commandPerm = cooldown.isBaseCooldown() ? label : label + "_" + String.join("_", cooldown.getArgs());
        double lowestDuration = -1;
        for (PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
            String permission = pai.getPermission();
            if (!permission.contains("commandcooldown." + commandPerm)) continue;
            // Only the duration
            String timeString = permission.replace("commandcooldown." + commandPerm + ".", "");
            try {
                double duration = Double.parseDouble(timeString);
                if (lowestDuration == -1)
                    lowestDuration = duration;
                else if (duration < lowestDuration)
                    lowestDuration = duration;
            } catch (NumberFormatException ignored) {
            }
        }
        // No new cooldown
        if (lowestDuration == -1)
            return cooldown;

        clone.setDuration(lowestDuration);
        return clone;
    }

    @Override
    public void loadAllCooldowns() {
        Collection<PlayerCooldown> loadedCooldowns = savedCooldownsRepo.loadAllCooldowns();
        for (PlayerCooldown cd : loadedCooldowns) {
            if (!cd.expired())
                cooldowns.add(cd);
        }
    }

    @Override
    public void saveAllCooldowns() {
        List<PlayerCooldown> save = new ArrayList<>();
        for (PlayerCooldown cd : cooldowns)
            if (!cd.expired())
                save.add(cd);
        savedCooldownsRepo.savePlayerCooldowns(save);
    }

    private PlayerCooldown getPlayerCooldown(Player p, Cooldown cooldown) {
        UUID uuid = p.getUniqueId();
        for (PlayerCooldown playerCooldown : cooldowns) {
            Cooldown cd = playerCooldown.getCooldown();
            if (playerCooldown.getPlayer().equals(uuid) &&
                    cd.equals(cooldown))
                return playerCooldown;
        }
        return null;
    }
}