package me.eeshe.penpenlib.commands;

import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.models.config.CommonMessage;
import org.bukkit.command.CommandSender;

/**
 * / reload command. Reloads the plugin's configurations.
 */
public class PenCommandReload extends PenCommand {

    public PenCommandReload(PenPenPlugin plugin, PenCommand parentCommand) {
        super(plugin, parentCommand);

        setName("reload");
        setPermission("penpenlib.reload");
        setInfoMessage(CommonMessage.RELOAD_COMMAND_INFO);
        setUsageMessage(CommonMessage.RELOAD_COMMAND_USAGE);
        setArgumentAmount(0);
        setUniversalCommand(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        getPlugin().reload();
        CommonMessage.RELOAD_COMMAND_SUCCESS.sendSuccess(sender);
    }
}