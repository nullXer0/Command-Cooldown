package me.darrionat.commandcooldown.commands;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.commands.subcommands.EditorCommand;
import me.darrionat.commandcooldown.commands.subcommands.HelpCommand;
import me.darrionat.commandcooldown.commands.subcommands.ReloadCommand;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import me.darrionat.pluginlib.commands.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandCooldownCommand extends BaseCommand {
    private final IMessageService messageService;

    public CommandCooldownCommand(CommandCooldownPlugin plugin, IMessageService messageService) {
        super(plugin);
        this.messageService = messageService;
        addSubCommand(new EditorCommand(this, plugin));
        addSubCommand(new ReloadCommand(this, plugin, messageService));
        addSubCommand(new HelpCommand(this, plugin, messageService));
    }

    @Override
    public String getCommandLabel() {
        return "commandcooldown";
    }

    @Override
    protected void runNoArgs(CommandSender sender, Command command, String label, String[] args) {
        messageService.sendBaseMessage(sender);
    }
}