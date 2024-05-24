package me.eeshe.penpenlib.commands;

import me.eeshe.penpenlib.PenPenPlugin;
import me.eeshe.penpenlib.models.config.CommonMessage;
import me.eeshe.penpenlib.models.config.PenMessage;
import me.eeshe.penpenlib.util.CommandUtil;
import me.eeshe.penpenlib.util.LibMessager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiFunction;

public class PenCommand {
    private final Map<String, PenCommand> subcommands;
    private final Map<Integer, BiFunction<CommandSender, String[], List<String>>> completions;
    private final PenPenPlugin plugin;
    private final PenCommand parentCommand;

    private String name;
    private String permission;
    private PenMessage infoMessage;
    private PenMessage usageMessage;
    private int argumentAmount;
    private boolean isConsoleCommand;
    private boolean isPlayerCommand;
    private boolean isUniversalCommand;

    public PenCommand(PenPenPlugin plugin) {
        this.plugin = plugin;
        this.parentCommand = null;
        this.subcommands = new LinkedHashMap<>();
        this.completions = new HashMap<>();
    }

    public PenCommand(PenPenPlugin plugin, PenCommand parentCommand) {
        this.plugin = plugin;
        this.parentCommand = parentCommand;
        this.subcommands = new LinkedHashMap<>();
        this.completions = new HashMap<>();
    }

    public PenCommand(PenPenPlugin plugin, PenCommand parentCommand, String name, String permission,
                      PenMessage infoMessage, PenMessage usageMessage, int argumentAmount, boolean isConsoleCommand,
                      boolean isPlayerCommand, boolean isUniversalCommand, Map<String, PenCommand> subcommands,
                      Map<Integer, BiFunction<CommandSender, String[], List<String>>> completions) {
        this.plugin = plugin;
        this.parentCommand = parentCommand;
        this.name = name;
        this.permission = permission;
        this.infoMessage = infoMessage;
        this.usageMessage = usageMessage;
        this.argumentAmount = argumentAmount;
        this.isConsoleCommand = isConsoleCommand;
        this.isPlayerCommand = isPlayerCommand;
        this.isUniversalCommand = isUniversalCommand;
        this.subcommands = subcommands;
        this.completions = completions;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            LibMessager.sendHelpMessage(sender, subcommands.values());
            return;
        }
        // Compute parent commands to then find the subcommand name
        PenCommand parentCommand = getParentCommand();
        int parentCommandAmount = 0;
        while (parentCommand != null) {
            parentCommandAmount += 1;
            parentCommand = parentCommand.getParentCommand();
        }
        String subcommandName = args[Math.min(args.length - 1, parentCommandAmount)].toLowerCase();
        PenCommand subcommand = subcommands.get(subcommandName);
        if (subcommand == null) {
            LibMessager.sendHelpMessage(sender, subcommands.values());
            return;
        }
        if (isPlayerCommand && !(sender instanceof Player)) {
            CommonMessage.PLAYER_COMMAND.sendError(sender);
            return;
        }
        if (isConsoleCommand && sender instanceof Player) {
            CommonMessage.CONSOLE_COMMAND.sendError(sender);
            return;
        }
        if (args.length - 1 < subcommand.getArgumentAmount()) {
            CommonMessage.USAGE_TEXT.sendError(sender, Map.of("%usage%", subcommand.getUsageMessage().getValue()));
            return;
        }
        if (!subcommand.checkPermission(sender, true)) return;

        subcommand.execute(sender, Arrays.copyOfRange(args, parentCommandAmount + 1, args.length));
    }

    public List<String> getCommandCompletions(CommandSender sender, String[] args) {
        if (!checkPermission(sender)) return new ArrayList<>();
        if (completions.isEmpty()) return new ArrayList<>(getSubcommands().keySet());

        return completions.getOrDefault(args.length, (sender1, strings) -> new ArrayList<>()).apply(sender, args);
    }

    public boolean checkPermission(CommandSender sender) {
        return CommandUtil.hasPermission(sender, permission);
    }

    public boolean checkPermission(CommandSender sender, boolean sendNotification) {
        return CommandUtil.hasPermission(sender, permission, sendNotification);
    }

    public PenPenPlugin getPlugin() {
        return plugin;
    }

    public PenCommand getParentCommand() {
        return parentCommand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public PenMessage getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(PenMessage infoMessage) {
        this.infoMessage = infoMessage;
    }

    public PenMessage getUsageMessage() {
        return usageMessage;
    }

    public void setUsageMessage(PenMessage usageMessage) {
        this.usageMessage = usageMessage;
    }

    public int getArgumentAmount() {
        return argumentAmount;
    }

    public void setArgumentAmount(int argumentAmount) {
        this.argumentAmount = argumentAmount;
    }

    public boolean isConsoleCommand() {
        return isConsoleCommand;
    }

    public void setConsoleCommand(boolean consoleCommand) {
        isConsoleCommand = consoleCommand;
    }

    public boolean isPlayerCommand() {
        return isPlayerCommand;
    }

    public void setPlayerCommand(boolean playerCommand) {
        isPlayerCommand = playerCommand;
    }

    public boolean isUniversalCommand() {
        return isUniversalCommand;
    }

    public void setUniversalCommand(boolean universalCommand) {
        isUniversalCommand = universalCommand;
    }

    public Map<String, PenCommand> getSubcommands() {
        return subcommands;
    }

    public void setSubcommands(List<PenCommand> penCommands) {
        for (PenCommand penCommand : penCommands) {
            subcommands.put(penCommand.getName(), penCommand);
        }
    }

    public PenCommand getSubcommand(String subcommandName) {
        return subcommands.get(subcommandName);
    }

    public void setCompletions(Map<Integer, BiFunction<CommandSender, String[], List<String>>> completions) {
        this.completions.clear();
        this.completions.putAll(completions);
    }
}
