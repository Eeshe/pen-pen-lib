package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.PenPenLib;
import me.eeshe.penpenlib.files.config.ConfigWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonMessage extends PenMessage {
    private static final List<PenMessage> MESSAGES = new ArrayList<>();
    private static final ConfigWrapper CONFIG_WRAPPER = new ConfigWrapper(PenPenLib.getInstance(), null, "messages.yml");

    public static final CommonMessage NEUTRAL_COLOR = new CommonMessage("neutral-color", "&e");
    public static final CommonMessage SUCCESS_COLOR = new CommonMessage("success-color", "&a");
    public static final CommonMessage ERROR_COLOR = new CommonMessage("error-color", "&c");
    public static final CommonMessage ACCENT_COLOR = new CommonMessage("accent-color", "&l");
    public static final CommonMessage UNKNOWN_COMMAND = new CommonMessage("unknown-command", "Unknown command. Run {ACCENT_COLOR}/penpenlib {ERROR_COLOR}help to see the full list of commands.");
    public static final CommonMessage PLAYER_COMMAND = new CommonMessage("player-command", "{ERROR_COLOR}Not available for consoles.");
    public static final CommonMessage CONSOLE_COMMAND = new CommonMessage("console-command", "{ERROR_COLOR}Not available for players.");
    public static final CommonMessage USAGE_TEXT = new CommonMessage("usage-text", "{ERROR_COLOR}Usage: %usage%.");
    public static final CommonMessage NO_PERMISSION = new CommonMessage("no-permission", "{ERROR_COLOR}You don't have permissions to run this command.");
    public static final CommonMessage PLAYER_NOT_FOUND = new CommonMessage("player-not-found", "{ERROR_COLOR}Unknown player {ACCENT_COLOR}%player%{ERROR_COLOR}.");
    public static final CommonMessage NON_NUMERIC_INPUT = new CommonMessage("non-numeric-input", "{ERROR_COLOR}You must enter a numeric value.");
    public static final CommonMessage NON_INTEGER_NUMERIC_INPUT = new CommonMessage("non-integer-numeric-input", "{ERROR_COLOR}You must enter a numeric value with no decimals.");
    public static final CommonMessage INVALID_NUMERIC_INPUT_ZERO = new CommonMessage("invalid-numeric-input-zero", "{ERROR_COLOR}Value can't be 0.");
    public static final CommonMessage INVALID_NUMERIC_INPUT_MUST_BE_HIGHER_THAN_ZERO = new CommonMessage("invalid-numeric-input-must-be-higher-than-zero", "{ERROR_COLOR}Value must be higher than 0.");
    public static final CommonMessage INVALID_TIME_FORMAT = new CommonMessage("invalid-time-format", "{ERROR_COLOR}Invalid time format. Use {ACCENT_COLOR}AdBhCmDs.");
    public static final CommonMessage ON_COOLDOWN = new CommonMessage("on-cooldown", "{ERROR_COLOR}Cooldown: {ACCENT_COLOR}%cooldown%");
    public static final CommonMessage ITEM_NOT_FOUND = new CommonMessage("item-not-found", "{ERROR_COLOR}Item {ACCENT_COLOR}%item%{ERROR_COLOR} not found.");
    public static final CommonMessage HELP_COMMAND_INFO = new CommonMessage("help-command-info", "Displays this list.");
    public static final CommonMessage HELP_COMMAND_USAGE = new CommonMessage("help-command-usage", "/penpenlib help");
    public static final CommonMessage HELP_TEXT_HEADER = new CommonMessage("help-text-header", "&e{ACCENT_COLOR}Commands");
    public static final CommonMessage HELP_TEXT_COMMAND_FORMAT = new CommonMessage("help-text-command-format", "&b%usage% &e- &9%info%");
    public static final CommonMessage NO_AVAILABLE_COMMANDS = new CommonMessage("no-available-commands", "{ERROR_COLOR}You don't have access to any commands.");
    public static final CommonMessage RELOAD_COMMAND_INFO = new CommonMessage("reload-command-info", "Reloads the plugin's configuration file.");
    public static final CommonMessage RELOAD_COMMAND_USAGE = new CommonMessage("reload-command-usage", "/penpenlib reload");
    public static final CommonMessage RELOAD_COMMAND_SUCCESS = new CommonMessage("reload-command-success", "{SUCCESS_COLOR}Configuration successfully reloaded.");
    private static final Map<String, PenMessage> PLACEHOLDERS = Map.ofEntries(
            Map.entry("{NEUTRAL_COLOR}", NEUTRAL_COLOR),
            Map.entry("{SUCCESS_COLOR}", SUCCESS_COLOR),
            Map.entry("{ERROR_COLOR}", ERROR_COLOR),
            Map.entry("{ACCENT_COLOR}", ACCENT_COLOR)
    );

    public CommonMessage(String path, String defaultValue) {
        super(path, defaultValue);
    }

    public CommonMessage() {
    }

    @Override
    public ConfigWrapper getConfigWrapper() {
        return CONFIG_WRAPPER;
    }

    @Override
    public List<PenMessage> getMessages() {
        return MESSAGES;
    }

    @Override
    public Map<String, PenMessage> getPlaceholders() {
        return PLACEHOLDERS;
    }
}
