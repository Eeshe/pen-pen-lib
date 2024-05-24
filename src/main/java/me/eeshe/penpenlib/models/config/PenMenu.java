package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.util.ConfigUtil;
import me.eeshe.penpenlib.util.ParseUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class PenMenu extends Configurable {
    private String path;
    private int defaultSize;
    private String defaultTitle;
    private Material defaultFrame;
    private List<Integer> defaultFrameSlots;
    private Material defaultFiller;
    private List<Integer> defaultFillerSlots;
    private MenuItem defaultPreviousPageItem;
    private MenuItem defaultNextPageItem;
    private MenuItem defaultBackItem;
    private List<MenuItem> defaultMenuItems;
    private Map<String, Object> additionalConfigs;

    public PenMenu(String path, int defaultSize, String defaultTitle, Material defaultFrame,
                   List<Integer> defaultFrameSlots, Material defaultFiller, List<Integer> defaultFillerSlots,
                   MenuItem defaultPreviousPageItem, MenuItem defaultNextPageItem, MenuItem defaultBackItem,
                   List<MenuItem> defaultMenuItems, Map<String, Object> additionalConfigs) {
        this.path = path;
        this.defaultSize = defaultSize;
        this.defaultTitle = defaultTitle;
        this.defaultFrame = defaultFrame;
        this.defaultFrameSlots = defaultFrameSlots;
        this.defaultFiller = defaultFiller;
        this.defaultFillerSlots = defaultFillerSlots;
        this.defaultPreviousPageItem = defaultPreviousPageItem;
        this.defaultNextPageItem = defaultNextPageItem;
        this.defaultBackItem = defaultBackItem;
        this.defaultMenuItems = defaultMenuItems;
        this.additionalConfigs = additionalConfigs;

        getMenus().add(this);
    }

    /**
     * Used solely with the purpose of calling the register() function.
     */
    public PenMenu() {

    }

    /**
     * Registers the PenMenu module.
     */
    public void register() {
        writeDefaults();
    }

    public void writeDefaults() {
        FileConfiguration config = getConfig();

        config.options().setHeader(List.of(
                "Use size 0 on a menu size to make it an adaptive menu. Using only the necessary slots to display its contents.",
                "Using -1 on the frame slots setting will automatically place frame items in the outer border of the menu.",
                "Using -1 on the filler slots setting will automatically fill all empty slots of the menu."
        ));
        for (PenMenu penMenu : getMenus()) {
            String path = penMenu.getPath();
            if (config.contains(path)) continue;

            config.addDefault(path + ".size", penMenu.getDefaultSize());
            config.addDefault(path + ".title", penMenu.getDefaultTitle());
            config.addDefault(path + ".frame", penMenu.getDefaultFrame().name());
            config.addDefault(path + ".frame-slots", penMenu.getDefaultFrameSlots());
            config.addDefault(path + ".filler", penMenu.getDefaultFiller().name());
            config.addDefault(path + ".filler-slots", penMenu.getDefaultFillerSlots());
            writeMenuItem(path + ".previous-page-item", penMenu.getDefaultPreviousPageItem());
            writeMenuItem(path + ".next-page-item", penMenu.getDefaultNextPageItem());
            writeMenuItem(path + ".back-item", penMenu.getDefaultBackItem());
            writeMenuItems(path + ".menu-items", penMenu.getDefaultMenuItems());
            ConfigUtil.writeAdditionalConfigs(getConfig(), path, penMenu.getAdditionalConfigs());
        }
        config.options().copyDefaults(true);

        ConfigWrapper configWrapper = getConfigWrapper();
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
    }

    /**
     * Writes the passed list of menu items to the passed config path.
     *
     * @param path      Path the items will be written to.
     * @param menuItems Menu items that will be written.
     */
    private void writeMenuItems(String path, List<MenuItem> menuItems) {
        for (MenuItem menuItem : menuItems) {
            writeMenuItem(path + "." + menuItem.getId(), menuItem);
        }
    }

    /**
     * Writes the passed menu item to the passed config path.
     *
     * @param path     Path the item will be written to.
     * @param menuItem Menu item that will be written.
     */
    private void writeMenuItem(String path, MenuItem menuItem) {
        if (menuItem == null) return;

        FileConfiguration config = getConfig();
        ConfigUtil.writeConfigItemStack(config, path + ".item", menuItem.getItem());
        config.addDefault(path + ".slot", menuItem.getSlot());
    }

    public ConfigMenu fetch() {
        return fetch(null);
    }

    public ConfigMenu fetch(ItemStack placeholderItem) {
        ConfigurationSection menuSection = getConfig().getConfigurationSection(path);

        int size = menuSection.getInt("size", defaultSize);
        String title = menuSection.getString("title", defaultTitle);
        Material frameMaterial = ParseUtil.parseMaterial(menuSection.getString(".frame"), defaultFrame);
        List<Integer> frameSlots = menuSection.getIntegerList("frame-slots");
        Material fillerMaterial = ParseUtil.parseMaterial(menuSection.getString(".filler"), defaultFiller);
        List<Integer> fillerSlots = menuSection.getIntegerList("filler-slots");
        MenuItem previousPageItem = fetchMenuItem(getConfig(), path + ".previous-page-item");
        MenuItem nextPageItem = fetchMenuItem(getConfig(), path + ".next-page-item");
        MenuItem backItem = fetchMenuItem(getConfig(), path + ".back-item");
        List<MenuItem> menuItems = fetchMenuItems(this, placeholderItem);

        // Calibrate slots from frame and filler configs
        frameSlots = calibrateConfiguredSlots(frameSlots);
        fillerSlots = calibrateConfiguredSlots(fillerSlots);
        return new ConfigMenu(size, title, frameMaterial, frameSlots, fillerMaterial, fillerSlots, previousPageItem,
                nextPageItem, backItem, menuItems);
    }

    /**
     * Fetches the configured menu items for the passed menu.
     *
     * @param penMenu         Menu whose items will be returned.
     * @param placeholderItem Item used to replace STRUCTURE_VOID menu items.
     * @return Configured menu items for the passed menu.
     */
    public List<MenuItem> fetchMenuItems(PenMenu penMenu, ItemStack placeholderItem) {
        List<MenuItem> menuItems = new ArrayList<>();
        FileConfiguration config = penMenu.getConfig();
        ConfigurationSection itemSection = config.getConfigurationSection(penMenu.getPath() + ".menu-items");
        if (itemSection == null) return menuItems;

        for (String menuItemId : itemSection.getKeys(false)) {
            MenuItem menuItem = fetchMenuItem(config, itemSection.getCurrentPath() + "." + menuItemId, placeholderItem);
            if (menuItemId == null) continue;

            menuItems.add(menuItem);
        }
        return menuItems;
    }

    /**
     * Fetches the menu item configured in the passed path.
     *
     * @param path Path the menu item will be fetched from.
     * @return Menu item configured for the passed menu.
     */
    public MenuItem fetchMenuItem(FileConfiguration config, String path) {
        return fetchMenuItem(config, path, null);
    }

    /**
     * Fetches the menu item configured in the passed path.
     *
     * @param path            Path the menu item will be fetched from.
     * @param placeholderItem Item to replace STRUCTURE_VOID menu items.
     * @return Menu item configured for the passed menu.
     */
    public MenuItem fetchMenuItem(FileConfiguration config, String path, ItemStack placeholderItem) {
        ConfigurationSection itemSection = config.getConfigurationSection(path);
        if (itemSection == null) return null;

        ItemStack item = ConfigUtil.fetchConfigItemStack(config, itemSection.getCurrentPath() + ".item");
        if (item != null && placeholderItem != null && item.getType() == Material.STRUCTURE_VOID) {
            // Add the configured item's lore to the placeholder item
            item = item.clone();
            ItemMeta itemMeta = item.getItemMeta();
            List<String> itemLore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();

            ItemMeta placeholderMeta = placeholderItem.clone().getItemMeta();
            List<String> placeholderLore = placeholderMeta.getLore() != null ? placeholderMeta.getLore() : new ArrayList<>();
            placeholderLore.addAll(itemLore);
            placeholderMeta.setLore(placeholderLore);

            placeholderItem = placeholderItem.clone();
            placeholderItem.setItemMeta(placeholderMeta);

            item = placeholderItem;
        }
        int slot = itemSection.getInt("slot") - 1;

        String[] pathSplit = path.split("\\.");
        if (pathSplit.length == 0) return null;

        return new MenuItem(pathSplit[pathSplit.length - 1], item, slot);
    }

    /**
     * Calibrates the passed collection of slots by subtract -1 to them as long as the int value isn't -1.
     *
     * @param collection Collection to calibrate.
     * @return List of calibrated slots.
     */
    private List<Integer> calibrateConfiguredSlots(Collection<Integer> collection) {
        return collection.stream().map(slot -> {
            if (slot == -1) return slot;

            return slot - 1;
        }).toList();
    }

    public String getPath() {
        return path;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public Material getDefaultFrame() {
        return defaultFrame;
    }

    public List<Integer> getDefaultFrameSlots() {
        return defaultFrameSlots;
    }

    public Material getDefaultFiller() {
        return defaultFiller;
    }

    public List<Integer> getDefaultFillerSlots() {
        return defaultFillerSlots;
    }

    public MenuItem getDefaultPreviousPageItem() {
        return defaultPreviousPageItem;
    }

    public MenuItem getDefaultNextPageItem() {
        return defaultNextPageItem;
    }

    public MenuItem getDefaultBackItem() {
        return defaultBackItem;
    }

    public List<MenuItem> getDefaultMenuItems() {
        return defaultMenuItems;
    }

    public Map<String, Object> getAdditionalConfigs() {
        return additionalConfigs;
    }

    public String getAdditionalConfigString(String additionalPath) {
        return getConfig().getString(path + "." + additionalPath);
    }

    public Material getAdditionalConfigMaterial(String additionalPath) {
        return ConfigUtil.fetchMaterial(getConfig(), path + "." + additionalPath);
    }

    public List<String> getAdditionalConfigStringList(String additionalPath) {
        return getConfig().getStringList(path + "." + additionalPath);
    }

    public int getAdditionalConfigInt(String additionalPath) {
        return getConfig().getInt(path + "." + additionalPath);
    }

    public abstract List<PenMenu> getMenus();
}
