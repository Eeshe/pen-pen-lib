package me.eeshe.penpenlib.commands;

import me.eeshe.penpenlib.PenPenPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandPenPenPen extends PenCommand {

    public CommandPenPenPen(PenPenPlugin plugin) {
        super(plugin);

        setName("penpenlib");
        setArgumentAmount(1);
        setSubcommands(List.of(
                new PenCommandReload(plugin, this),
                new PenCommandHelp(plugin, this)
        ));
        setCompletions(Map.of(0, (sender, strings) -> new ArrayList<>(getSubcommands().keySet())));
    }
}
