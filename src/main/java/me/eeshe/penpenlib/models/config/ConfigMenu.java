package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.util.MenuUtil;
import me.eeshe.penpenlib.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

public class ConfigMenu {
    private final int size;
    private final String title;
    private final Material frame;
    private final List<Integer> frameSlots;
    private final Material filler;
    private final List<Integer> fillerSlots;
    private final MenuItem previousPageItem;
    private final MenuItem nextPageItem;
    private final MenuItem backItem;
    private final List<MenuItem> menuItems;

    public ConfigMenu(int size, String title, Material frame, List<Integer> frameSlots, Material filler,
                      List<Integer> fillerSlots, MenuItem previousPageItem, MenuItem nextPageItem, MenuItem backItem,
                      List<MenuItem> menuItems) {
        this.size = size;
        this.title = title;
        this.frame = frame;
        this.frameSlots = frameSlots;
        this.filler = filler;
        this.fillerSlots = fillerSlots;
        this.previousPageItem = previousPageItem;
        this.nextPageItem = nextPageItem;
        this.backItem = backItem;
        this.menuItems = menuItems;
    }

    /**
     * Creates an inventory containing the ConfigMenu.
     *
     * @param inventoryHolder INventoryHolder the inventory will use.
     * @return Inventory containing the ConfigMenu.
     */
    public Inventory createInventory(InventoryHolder inventoryHolder) {
        return createInventory(inventoryHolder, false, false, false, false, null);
    }

    /**
     * Creates an inventory containing the ConfigMenu.
     *
     * @param inventoryHolder InventoryHolder the inventory will use.
     * @param placeFrame      Whether an inventory frame should be placed.
     * @param placeFiller     Whether inventory filler should be placed.
     * @param placeBackButton Whether a back button should be placed.
     * @param placeMenuItems  Whether menu items should be placed.
     * @param placeholders    Placeholders used during the inventory creation process.
     * @return Inventory containing the ConfigMenu.
     */
    public Inventory createInventory(InventoryHolder inventoryHolder, boolean placeFrame, boolean placeFiller,
                                     boolean placeBackButton, boolean placeMenuItems, Map<String, String> placeholders) {
        return createInventory(inventoryHolder, 0, 1, placeFrame, placeFiller, placeBackButton,
                placeMenuItems, placeholders);
    }

    /**
     * Creates an inventory containing the ConfigMenu.
     *
     * @param inventoryHolder      InventoryHolder the inventory will use.
     * @param additionalItemAmount Additional amount of items that will be added to the menu after creating it.
     * @param placeFrame           Whether an inventory frame should be placed.
     * @param placeFiller          Whether inventory filler should be placed.
     * @param placeBackButton      Whether a back button should be placed.
     * @param placeMenuItems       Whether menu items should be placed.
     * @param placeholders         Placeholders used during the inventory creation process.
     * @return Inventory containing the ConfigMenu.
     */
    public Inventory createInventory(InventoryHolder inventoryHolder, int additionalItemAmount, int page, boolean placeFrame,
                                     boolean placeFiller, boolean placeBackButton, boolean placeMenuItems,
                                     Map<String, String> placeholders) {
        if (placeholders == null) {
            placeholders = new HashMap<>();
        }
        String formattedTitle = title;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            formattedTitle = formattedTitle.replace(entry.getKey(), entry.getValue());
        }
        boolean usesDynamicSize = usesDynamicSize();
        int maximumEmptySlots = calculateEmptySlots();
        if (usesDynamicSize && maximumEmptySlots < additionalItemAmount) {
            additionalItemAmount -= (page - 1) * maximumEmptySlots;
        }
        int inventorySize = usesDynamicSize() ? computeDynamicSize(additionalItemAmount) : getSize();
        Inventory inventory = Bukkit.createInventory(inventoryHolder, inventorySize, StringUtil.formatColor(formattedTitle));
        if (placeFrame) {
            MenuUtil.placeFrameItems(inventory, getFrame(), getFrameSlots());
        }
        MenuItem backItem = getBackItem();
        if (placeBackButton && backItem != null) {
            inventory.setItem(backItem.getAdaptiveSlot(getSize()), backItem.generateItemStack(placeholders));
        }
        if (placeMenuItems) {
            MenuUtil.addMenuItems(inventory, getMenuItems(), placeholders);
        }
        if (placeFiller) {
            MenuUtil.placeFillerItems(inventory, getFiller(), getFillerSlots());
        }
        return inventory;
    }

    /**
     * Creates an inventory containing the ConfigMenu.
     *
     * @param inventoryHolder InventoryHolder the inventory will use.
     * @param placeFrame      Whether an inventory frame should be placed.
     * @param placeFiller     Whether inventory filler should be placed.
     * @param placeBackButton Whether a back button should be placed.
     * @param placeMenuItems  Whether menu items should be placed.
     * @param placeholders    Placeholders used during the inventory creation process.
     * @return Inventory containing the ConfigMenu.
     */
    public Inventory createInventoryWithListPlaceholders(InventoryHolder inventoryHolder, boolean placeFrame,
                                                         boolean placeFiller, boolean placeBackButton,
                                                         boolean placeMenuItems, Map<String, List<String>> placeholders) {
        return createInventoryWithListPlaceholders(inventoryHolder, 0, placeFrame, placeFiller,
                placeBackButton, placeMenuItems, placeholders);
    }

    /**
     * Creates an inventory containing the ConfigMenu.
     *
     * @param inventoryHolder      InventoryHolder the inventory will use.
     * @param additionalItemAmount Additional amount of items that will be added to the menu after creating it.
     * @param placeFrame           Whether an inventory frame should be placed.
     * @param placeFiller          Whether inventory filler should be placed.
     * @param placeBackButton      Whether a back button should be placed.
     * @param placeMenuItems       Whether menu items should be placed.
     * @param placeholders         Placeholders used during the inventory creation process.
     * @return Inventory containing the ConfigMenu.
     */
    public Inventory createInventoryWithListPlaceholders(InventoryHolder inventoryHolder, int additionalItemAmount,
                                                         boolean placeFrame, boolean placeFiller, boolean placeBackButton,
                                                         boolean placeMenuItems, Map<String, List<String>> placeholders) {
        if (placeholders == null) {
            placeholders = new HashMap<>();
        }
        String formattedTitle = title;
        for (Map.Entry<String, List<String>> entry : placeholders.entrySet()) {
            formattedTitle = formattedTitle.replace(entry.getKey(), entry.getValue().get(0));
        }
        int inventorySize = usesDynamicSize() ? computeDynamicSize(additionalItemAmount) : getSize();
        Inventory inventory = Bukkit.createInventory(inventoryHolder, inventorySize, StringUtil.formatColor(formattedTitle));
        if (placeFrame) {
            MenuUtil.placeFrameItems(inventory, getFrame(), getFrameSlots());
        }
        if (placeFiller) {
            MenuUtil.placeFillerItems(inventory, getFiller(), getFillerSlots());
        }
        MenuItem backItem = getBackItem();
        if (placeBackButton && backItem != null) {
            inventory.setItem(backItem.getSlot(), backItem.generateItemStackWithListPlaceholders(placeholders));
        }
        if (placeMenuItems) {
            MenuUtil.addMenuItemsWithListPlaceholders(inventory, getMenuItems(), placeholders);
        }
        return inventory;
    }

    /**
     * Computes a dynamic inventory size for the passed amount of items.
     *
     * @param itemAmount Amount of items that will be put in the inventory.
     * @return Dynamic inventory size for the passed amount of items.
     */
    private int computeDynamicSize(int itemAmount) {
        boolean usesDynamicSlots = usesDynamicFrameSlots();
        int lastUsedSlot = 0;
        int computedItemAmount = 0;
        for (int slot = 0; slot < 54 && computedItemAmount < itemAmount; slot++) {
            if (usesDynamicSlots && MenuUtil.isDynamicFrameSlot(slot)) continue;
            if (getFrameSlots().contains(slot)) continue;

            lastUsedSlot = slot;
            computedItemAmount += 1;
        }
        int inventorySize = MenuUtil.getNearestMenuSize(lastUsedSlot);
        if (inventorySize == 54) return inventorySize;
        if (inventorySize == lastUsedSlot) {
            inventorySize = MenuUtil.getNearestMenuSize(inventorySize + 1);
        }
        return MenuUtil.getNearestMenuSize(inventorySize + 1);
    }

    /**
     * Calculates the amount of empty slots the inventory with the ConfigMenu's parameters can have.
     * <p>
     * Mainly use to determine how many additional items can fit in a page of the inventory.
     *
     * @return Amount of empty slots the inventory with the ConfigMenu's parameters can have.
     */
    public int calculateEmptySlots() {
        return (usesDynamicSize() ? 54 : getSize()) - computeUsedSlots().size();
    }

    public int getSize() {
        return size;
    }

    public boolean usesDynamicSize() {
        return size == 0;
    }

    public String getTitle() {
        return title;
    }

    public Material getFrame() {
        return frame;
    }

    public boolean hasFrame() {
        return frame != null && frame != Material.AIR;
    }

    public List<Integer> getFrameSlots() {
        return frameSlots;
    }

    public boolean usesDynamicFrameSlots() {
        return frameSlots.contains(-1);
    }

    public Material getFiller() {
        return filler;
    }

    public List<Integer> getFillerSlots() {
        return fillerSlots;
    }

    public boolean hasFiller() {
        return filler != null && filler != Material.AIR;
    }

    public MenuItem getPreviousPageItem() {
        return previousPageItem;
    }

    public MenuItem getNextPageItem() {
        return nextPageItem;
    }

    public MenuItem getBackItem() {
        return backItem;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    /**
     * Returns a list with the used slots by frames, fillers, page items, back item and menu items.
     *
     * @return List with all the used slots by frames, fillers, page items, back item and menu items.
     */
    public List<Integer> computeUsedSlots() {
        List<Integer> usedSlots = new ArrayList<>();
        int inventorySize = usesDynamicSize() ? 54 : getSize();
        if (usesDynamicFrameSlots()) {
            usedSlots.addAll(MenuUtil.computeFrameSlots(inventorySize));
        }
        usedSlots.addAll(getFrameSlots());
        if (getBackItem() != null) {
            usedSlots.add(getBackItem().getAdaptiveSlot(inventorySize));
        }
        if (getPreviousPageItem() != null) {
            usedSlots.add(getPreviousPageItem().getAdaptiveSlot(inventorySize));
        }
        if (getNextPageItem() != null) {
            usedSlots.add(getNextPageItem().getAdaptiveSlot(inventorySize));
        }
        usedSlots.addAll(getMenuItems().stream().map(menuItem -> menuItem.getAdaptiveSlot(inventorySize)).toList());

        // Remove duplicates
        usedSlots = new ArrayList<>(new HashSet<>(usedSlots));
        usedSlots.removeIf(integer -> integer < 0);
        if (!usesDynamicSize()) {
            usedSlots.removeIf(integer -> integer > inventorySize);
        }
        return usedSlots;
    }
}
