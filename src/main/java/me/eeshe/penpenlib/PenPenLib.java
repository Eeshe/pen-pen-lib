package me.eeshe.penpenlib;

import me.eeshe.penpenlib.commands.CommandCompleter;
import me.eeshe.penpenlib.commands.CommandPenPenPen;
import me.eeshe.penpenlib.commands.CommandRunner;
import me.eeshe.penpenlib.commands.PenCommand;
import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.files.config.MainConfig;
import me.eeshe.penpenlib.models.config.CommonMessage;
import me.eeshe.penpenlib.models.config.CommonSound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PenPenLib extends JavaPlugin implements PenPenPlugin {
    private final Map<String, PenCommand> libCommands = new LinkedHashMap<>();
    private final List<ConfigWrapper> configFiles = new ArrayList<>();

    private MainConfig mainConfig;

    private CommandExecutor commandExecutor;
    private CommandCompleter commandCompleter;

    /**
     * Creates and returns a static instance of the Plugin's main class.
     *
     * @return Instance of the main class of the plugin.
     */
    public static PenPenLib getInstance() {
        return PenPenLib.getPlugin(PenPenLib.class);
    }

    @Override
    public void onEnable() {
        setupFiles();
        registerCommands();
    }

    /**
     * Creates and configures all the config files of the plugin.
     */
    public void setupFiles() {
        configFiles.clear();

        this.mainConfig = new MainConfig(this);
        CommonMessage commonMessage = new CommonMessage();
        CommonSound commonSound = new CommonSound();
        configFiles.add(mainConfig);
        configFiles.addAll(List.of(
                mainConfig,
                commonMessage.getConfigWrapper(),
                commonSound.getConfigWrapper()
        ));
        for (ConfigWrapper configFile : configFiles) {
            configFile.writeDefaults();
        }
        commonMessage.register();
        commonSound.register();
    }

    /**
     * Registers the EmperixLib's commands, runners and tab completers.
     */
    private void registerCommands() {
        this.commandExecutor = new CommandRunner(this);
        this.commandCompleter = new CommandCompleter(this);
        registerCommands(List.of(
                new CommandPenPenPen(this)
        ));
    }

    /**
     * Registers the passed list of LibCommands.
     *
     * @param penCommands LibCommands to register.
     */
    public void registerCommands(List<PenCommand> penCommands) {
        for (PenCommand penCommand : penCommands) {
            PluginCommand pluginCommand = penCommand.getPlugin().getSpigotPlugin().getServer().getPluginCommand(penCommand.getName());
            if (pluginCommand == null) continue;

            this.libCommands.put(penCommand.getName(), penCommand);
            pluginCommand.setExecutor(commandExecutor);
            pluginCommand.setTabCompleter(commandCompleter);
        }
    }

    @Override
    public void onDisable() {
        libCommands.clear();
    }

    /**
     * Reloads the plugin.
     */
    @Override
    public void reload() {
        for (ConfigWrapper configFile : configFiles) {
            configFile.reloadConfig();
        }
        setupFiles();
    }

    @Override
    public Plugin getSpigotPlugin() {
        return this;
    }

    public Map<String, PenCommand> getLibCommands() {
        return libCommands;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
