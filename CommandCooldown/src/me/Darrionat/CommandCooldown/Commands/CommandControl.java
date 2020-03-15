package me.Darrionat.CommandCooldown.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.Main;
import me.Darrionat.CommandCooldown.Utils.Utils;

public class CommandControl implements CommandExecutor {

    private Main plugin;

    public CommandControl(Main plugin) {
        this.plugin = plugin;

        plugin.getCommand("commandcooldown").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("commandcooldown.admin")) {
                p.sendMessage(Utils
                        .chat(config.getString("Messages.NoPermission").replace("%perm%", "commandcooldown.admin")));
                return true;
            }
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.chat("&a&lCOMMAND COOLDOWN v" + plugin.getDescription().getVersion()));
            sender.sendMessage(Utils.chat("  &7Author: Darrionat"));
            sender.sendMessage(Utils.chat("  &7Support: https://discord.gg/xNKrH5Z"));
            sender.sendMessage(Utils.chat("  &7/" + label + " help - &oFor additional information"));
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(Utils.chat("&a&lCOMMAND COOLDOWN - USAGE"));
            sender.sendMessage(Utils.chat("  &7/" + label + " list"));
            sender.sendMessage(Utils.chat("  &7/" + label + " bypass"));
            sender.sendMessage(Utils.chat("  &7/" + label + " reload"));
            sender.sendMessage(Utils.chat("  &7/" + label + " add command cooldown alias1 alias2 alias3..."));
            sender.sendMessage(Utils.chat("  &7/" + label + " remove command"));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            List<String> list = new ArrayList<String>();
            for (String key : config.getKeys(false)) {

                if (key.equalsIgnoreCase("Messages") || key.equalsIgnoreCase("checkUpdates")
                        || key.equalsIgnoreCase("SendBypassMessage")) {
                    continue;
                }

                list.add("&a&l[" + key + "]");

                // Cooldown
                int cooldown = config.getInt(key + ".cooldown");
                long hours = cooldown / 3600;
                long minutes = (cooldown % 3600) / 60;
                long seconds = cooldown % 60;
                String timeString = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
                list.add("  &7Cooldown: " + String.valueOf(timeString));

                // Aliases
                String aliases = "  &7Aliases: ";
                for (String s : config.getConfigurationSection(key).getStringList("aliases")) {
                    aliases = aliases + s + ", ";
                }
                list.add(aliases);
            }
            for (String s : list) {
                sender.sendMessage(Utils.chat(s));
            }
            return true;
        }

        // /cc remove command
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                sender.sendMessage(Utils.chat("  &7/" + label + " remove command"));
                return true;
            }
            if (config.getConfigurationSection(args[1]) == null) {
                sender.sendMessage(Utils.chat(config.getString("Messages.DoesNotExist")));
                return true;
            }
            config.set(args[1], null);
            plugin.saveConfig();
            sender.sendMessage(Utils.chat(config.getString("Messages.DeleteSuccessful")));
            return true;
        }

        // /cc add command cooldown aliases...
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(Utils.chat("  &7/" + label + " add command cooldown alias1 alias2 alias3..."));
                return true;
            }
            if (args[1].contains("/")) {
                args[1].replace("/", "");
            }
            try {
                @SuppressWarnings("unused")
                int cooldown = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Utils.chat(config.getString("Messages.NotNumber")));
                return true;
            }
            List<String> aliases = new ArrayList<String>();

            // Maxed out at 100. Probability of someone going over 100 aliases is more slime
            // than winning the lottery.
            for (int i = 3; i <= 100; i++) {
                try {
                    aliases.add(args[i]);
                } catch (ArrayIndexOutOfBoundsException exe) {
                    break;
                }
            }
            String command = args[1];
            int cooldown = Integer.parseInt(args[2]);
            config.createSection(command);
            ConfigurationSection section = config.getConfigurationSection(command);
            section.set("aliases", aliases);
            section.set("cooldown", cooldown);

            plugin.saveConfig();
            sender.sendMessage(Utils.chat(config.getString("Messages.CreationSuccessful")));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(Utils.chat(config.getString("Messages.ReloadSuccessful")));
            return true;
        }

        if (args[0].equalsIgnoreCase("bypass")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.chat(config.getString("Messages.NotAPlayer")));
                return true;
            }
            Player p = (Player) sender;
            String name = p.getName();
            ArrayList<String> bypassList = Utils.getBypassList();

            if (bypassList.contains(name)) {
                p.sendMessage(Utils.chat(config.getString("Messages.BypassOff")));
                bypassList.remove(name);
                return true;
            }
            p.sendMessage(Utils.chat(config.getString("Messages.BypassOn")));
            bypassList.add(name);
            return true;

        }

        // At the end, so if none of the conditions are met, it will send this message.
        sender.sendMessage(Utils.chat("&a&lCOMMAND COOLDOWN v" + plugin.getDescription().getVersion()));
        sender.sendMessage(Utils.chat("  &7Author: Darrionat"));
        sender.sendMessage(Utils.chat("  &7Support: https://discord.gg/xNKrH5Z"));
        sender.sendMessage(Utils.chat("  &7/" + label + " help - &oFor additional information"));
        return true;
    }

}