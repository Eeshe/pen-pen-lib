package me.eeshe.penpenlib.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class StorageDataFile {
    private File file;
    private FileConfiguration dataYml;

    public StorageDataFile(String path) {
        createDataFile(path);
    }

    public StorageDataFile(File file) {
        this.file = file;
        this.dataYml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Creates the data file.
     *
     * @param path Path where the plugin will create the file in.
     */
    private void createDataFile(String path) {
        this.file = new File(path);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.dataYml = YamlConfiguration.loadConfiguration(file);
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getData() {
        return dataYml;
    }

    public void save() {
        try {
            dataYml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        dataYml.setDefaults(YamlConfiguration.loadConfiguration(file));
    }

    public void delete() {
        file.delete();
    }
}
