package me.eeshe.penpenlib.commands;

import me.eeshe.penpenlib.PenPenLib;
import me.eeshe.penpenlib.models.config.CommonMessage;
import me.eeshe.penpenlib.util.CommandUtil;
import me.eeshe.penpenlib.util.LibMessager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * This class controls everything regarding command execution in the plugin. It checks the command and arguments
 * every time the player runs a command registered by the plugin.
 */
public class CommandRunner implements CommandExecutor {
    private final PenPenLib plugin;

    public CommandRunner(PenPenLib plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String commandName = cmd.getName();
        PenCommand penCommand = plugin.getLibCommands().get(commandName);
        if (penCommand == null) return false;
        if (args.length < penCommand.getArgumentAmount()) {
            if (penCommand.getUsageMessage() == null) {
                LibMessager.sendHelpMessage(sender, penCommand.getSubcommands().values());
                return true;
            }
            CommonMessage.USAGE_TEXT.sendError(sender, Map.of("%usage%", penCommand.getUsageMessage().getValue()));
            return true;
        }
        if (!CommandUtil.hasPermission(sender, penCommand.getPermission(), true)) return true;

        penCommand.execute(sender, args);
        return true;
    }
}
