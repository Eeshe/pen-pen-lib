package me.eeshe.penpenlib.commands;

import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.models.config.CommonMessage;
import me.eeshe.penpenlib.util.LibMessager;
import org.bukkit.command.CommandSender;

/**
 * / help command. Displays the help text for the plugin.
 */
public class PenCommandHelp extends PenCommand {

    public PenCommandHelp(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setName("help");
        setPermission("penpenlib.help");
        setInfoMessage(CommonMessage.HELP_COMMAND_INFO);
        setUsageMessage(CommonMessage.HELP_COMMAND_USAGE);
        setArgumentAmount(0);
        setUniversalCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        LibMessager.sendHelpMessage(sender, getParentCommand().getSubcommands().values());
    }
}
