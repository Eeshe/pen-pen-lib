package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.util.StringUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PenMessage extends Configurable {

    private String path;
    private String defaultValue;

    public PenMessage(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;

        getMessages().add(this);
    }

    /**
     * Used solely with the purpose of calling the register() function.
     */
    public PenMessage() {

    }

    /**
     * Registers the PenMessage module.
     */
    public void register() {
        writeDefaults();
    }

    public void writeDefaults() {
        FileConfiguration config = getConfig();
        for (PenMessage penMessage : getMessages()) {
            String path = penMessage.getPath();
            if (config.contains(path)) continue;

            config.addDefault(penMessage.getPath(), penMessage.getDefaultValue());
        }
        config.options().copyDefaults(true);

        ConfigWrapper configWrapper = getConfigWrapper();
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
    }

    /**
     * Sends the message to the passed CommandSender and plays the success sound.
     *
     * @param sender CommandSender the message will be sent to.
     */
    public void sendSuccess(CommandSender sender) {
        send(sender, CommonSound.SUCCESS);
    }

    /**
     * Sends the message to the passed CommandSender replacing the passed placeholders and plays the success sound.
     *
     * @param sender       CommandSender the message will be sent to.
     * @param placeholders Placeholders that will be replaced in the message.
     */
    public void sendSuccess(CommandSender sender, Map<String, String> placeholders) {
        send(sender, CommonSound.SUCCESS, placeholders);
    }

    /**
     * Sends the message to the passed CommandSender and plays the error sound.
     *
     * @param sender CommandSender the message will be sent to.
     */
    public void sendError(CommandSender sender) {
        send(sender, CommonSound.ERROR);
    }

    /**
     * Sends the message to the passed CommandSender replacing the passed placeholders and plays the error sound.
     *
     * @param sender       CommandSender the message will be sent to.
     * @param placeholders Placeholders that will be replaced in the message.
     */
    public void sendError(CommandSender sender, Map<String, String> placeholders) {
        send(sender, CommonSound.ERROR, placeholders);
    }

    /**
     * Sends the message to the passed CommandSender and plays the passed Sound.
     *
     * @param sender   CommandSender the message will be sent to.
     * @param libSound Sound that will be played.
     */
    public void send(CommandSender sender, PenSound libSound) {
        send(sender, false, libSound, new HashMap<>());
    }

    /**
     * Sends the message to the passed CommandSender replacing the passed placeholders.
     *
     * @param sender       CommandSender the message will be sent to.
     * @param placeholders Placeholders that will be replaced in the message.
     */
    public void send(CommandSender sender, Map<String, String> placeholders) {
        send(sender, false, null, placeholders);
    }

    /**
     * Sends the message to the passed CommandSender and plays the passed Sound replacing the passed placeholders.
     *
     * @param sender       CommandSender the message will be sent to.
     * @param libSound     Sound that will be played.
     * @param placeholders Placeholders that will be replaced in the message.
     */
    public void send(CommandSender sender, PenSound libSound, Map<String, String> placeholders) {
        send(sender, false, libSound, placeholders);
    }

    /**
     * Sends the message to the passed CommandSender replacing the passed placeholders and plays the passed Sound.
     *
     * @param sender       CommandSender the message will be sent to.
     * @param actionBar    Whether the message will be sent in the action bar.
     * @param libSound     Sound that will be sent.
     * @param placeholders Placeholders that will be replaced.
     */
    public void send(CommandSender sender, boolean actionBar, PenSound libSound, Map<String, String> placeholders) {
        if (sender == null) return;
        String message = StringUtil.formatColor(getFormattedValue(placeholders));
        if (!actionBar) {
            sender.sendMessage(message);
        } else if (sender instanceof Player player) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
        }
        if (libSound == null) return;

        libSound.play(sender);
    }

    /**
     * Returns the value formatted with the passed placeholders.
     *
     * @param placeholders Placeholders that will be formatted.
     * @return Value formatted with the passed placeholders.
     */
    public String getFormattedValue(Map<String, String> placeholders) {
        String message = getValue();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    public String getValue() {
        String message = getConfig().getString(path, defaultValue);
        if (getPlaceholders().containsValue(this)) return message;

        for (Map.Entry<String, PenMessage> entry : getPlaceholders().entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue().getValue());
        }
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public abstract List<PenMessage> getMessages();

    public abstract Map<String, PenMessage> getPlaceholders();
}
