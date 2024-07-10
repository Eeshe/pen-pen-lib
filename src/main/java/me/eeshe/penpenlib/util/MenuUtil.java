package me.eeshe.penpenlib.util;

import me.eeshe.penpenlib.PenPenLib;
import me.eeshe.penpenlib.models.Scheduler;
import me.eeshe.penpenlib.models.config.CommonSound;
import me.eeshe.penpenlib.models.config.ConfigMenu;
import me.eeshe.penpenlib.models.config.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Utility class that eases the work with framed GUIs.
 */
public class MenuUtil {
    // Does include top and bottoms rows
    private static final List<Integer> DYNAMIC_FRAME_SLOTS = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53);
    // Does not include top or bottom rows
    private static final Set<Integer> SIDE_FRAME_SLOTS = Set.of(0, 8, 9, 17, 18, 26, 27, 35, 36, 44);
    private static final NavigableSet<Integer> MENU_SIZES = new TreeSet<>(Set.of(9, 18, 27, 36, 45, 54));

    /**
     * Places the frame items in the passed inventory using the passed slots. If the slot list contains -1, it will
     * automatically place the frame items in the external borders of the inventory.
     *
     * @param inventory     Inventory to place the frame items in.
     * @param frameMaterial Material of the frame item.
     * @param slots         Slots the items will be placed in.
     */
    public static void placeFrameItems(Inventory inventory, Material frameMaterial, Collection<Integer> slots) {
        if (frameMaterial.isAir()) return;
        // Convert to mutable
        slots = new HashSet<>(slots);
        if (slots.contains(-1)) {
            slots.addAll(computeFrameSlots(inventory.getSize()));
        }
        // Remove duplicates
        slots = new HashSet<>(slots);

        ItemStack frameItem = ItemUtil.generateItemStack(frameMaterial, " ");
        int inventorySize = inventory.getSize();
        for (Integer slot : slots) {
            if (slot >= inventorySize || slot < 0) continue;

            inventory.setItem(slot, frameItem);
        }
    }

    /**
     * Computes the slots where an external inventory frame will be placed in.
     *
     * @param inventorySize Size of the inventory whose frame slots will be computed.
     * @return Slots where an external inventory frame will be placed in.
     */
    public static Set<Integer> computeFrameSlots(int inventorySize) {
        Set<Integer> slots = new HashSet<>();
        // Side columns
        for (Integer sideFrameSlot : SIDE_FRAME_SLOTS) {
            if (sideFrameSlot >= inventorySize) continue;

            slots.add(sideFrameSlot);
        }
        if (inventorySize <= 18) return slots;

        // Top row
        for (int i = 0; i < 9; i++) {
            slots.add(i);
        }
        // Bottom row
        for (int i = inventorySize - 9; i < inventorySize; i++) {
            slots.add(i);
        }
        return slots;
    }

    /**
     * Places the filler items in the passed inventory using the passed slots. If the slot list contains -1, it will
     * automatically place the filler items in the external borders of the inventory.
     *
     * @param configMenu    ConfigMenu to place the filler items in.
     * @param inventory     Inventory to place the filler items in.
     */
    public static void placeFillerItems(ConfigMenu configMenu, Inventory inventory) {
        Material fillerMaterial = configMenu.getFiller();
        if (fillerMaterial.isAir()) return;
        // Convert to mutable
        Set<Integer> slots = new HashSet<>(configMenu.getFillerSlots());
        if (slots.contains(-1)) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                if (slots.contains(slot)) continue;
                ItemStack item = inventory.getItem(slot);
                if (item != null && !item.getType().isAir()) continue;

                slots.add(slot);
            }
        }
        // Remove duplicates
        slots = new HashSet<>(slots);

        ItemStack frameItem = ItemUtil.generateItemStack(fillerMaterial, " ");
        int inventorySize = inventory.getSize();
        for (Integer slot : slots) {
            if (slot >= inventorySize || slot < 0) continue;

            inventory.setItem(slot, frameItem);
        }
    }

    /**
     * Adds the passed menu items to the passed inventory while formatting with the passed placeholders.
     *
     * @param inventory    Inventory to add the items to.
     * @param menuItems    Menu items to add.
     * @param placeholders Placeholders to format.
     */
    public static void addMenuItems(Inventory inventory, List<MenuItem> menuItems, Map<String, String> placeholders) {
        int inventorySize = inventory.getSize();
        for (MenuItem menuItem : menuItems) {
            int slot = menuItem.getAdaptiveSlot(inventorySize);
            if (slot >= inventorySize || slot < 0) {
                LogUtil.sendWarnLog("Slot out of bounds for menu item '" + menuItem.getId() + "'.");
                continue;
            }
            inventory.setItem(slot, menuItem.generateItemStack(placeholders));
        }
    }

    /**
     * Adds the passed menu items to the passed inventory while formatting with the passed list placeholders.
     *
     * @param inventory    Inventory to add the items to.
     * @param menuItems    Menu items to add.
     * @param placeholders Placeholders to format.
     */
    public static void addMenuItemsWithListPlaceholders(Inventory inventory, List<MenuItem> menuItems,
                                                        Map<String, List<String>> placeholders) {
        for (MenuItem menuItem : menuItems) {
            inventory.setItem(menuItem.getAdaptiveSlot(inventory.getSize()), menuItem.generateItemStackWithListPlaceholders(placeholders));
        }
    }

    /**
     * Adds page items depending on the passed arguments.
     * If page > 1 add a previous page item.
     * If hasNextPage == true add a next page item.
     *
     * @param inventory   Inventory where page items will be added to.
     * @param configMenu  ConfigMenu the page items will be added to.
     * @param page        Current page number the inventory is in.
     * @param hasNextPage True if the inventory is supposed to have a next page.
     */
    public static void addPageItems(Inventory inventory, ConfigMenu configMenu, int page, boolean hasNextPage) {
        if (page > 1) {
            MenuItem previousPageItem = configMenu.getPreviousPageItem();
            if (previousPageItem != null) {
                inventory.setItem(previousPageItem.getAdaptiveSlot(inventory.getSize()), previousPageItem.generateItemStack(new HashMap<>()));
            }
        }
        if (hasNextPage) {
            MenuItem nextPageItem = configMenu.getNextPageItem();
            if (nextPageItem != null) {
                inventory.setItem(nextPageItem.getAdaptiveSlot(inventory.getSize()), nextPageItem.generateItemStack(new HashMap<>()));
            }
        }
    }

    /**
     * Calculates the inventory size that is needed for the passed amount of items.
     *
     * @param itemAmount Amount of items that will be in the inventory.
     * @param hasFrame   Whether the menu has a frame.
     * @return Inventory size that is needed for the passed amount of items.
     */
    public static int calculateInventorySize(int itemAmount, boolean hasFrame) {
        int slotsPerRow = hasFrame ? 7 : 9;
        int baseRows = hasFrame ? 2 : 0;
        int rowAmount = baseRows + ((int) Math.ceil((double) itemAmount / slotsPerRow));

        return Math.max(9, Math.min(rowAmount * 9, 54));
    }

    /**
     * Computes the initial index for a list with the passed amount of items opening in the passed page.
     * <p>
     * The computation method consists of getting the amount of empty slots of the passed ConfigMenu, calculate the
     * amount of items per page, and then multiply it by the page number.
     *
     * @param configMenu ConfigMenu being created.
     * @return Initial index for a list with the passed amount of items opening in the passed page.
     */
    public static int computeInitialIndex(ConfigMenu configMenu, int page) {
        if (page == 1) return 0;

        return configMenu.calculateEmptySlots() * (page - 1);
    }

    /**
     * Opens the passed inventory to the passed player synchronously.
     *
     * @param player    Player the inventory will be opened to.
     * @param inventory Inventory to open.
     */
    public static void openSync(Player player, Inventory inventory) {
        Scheduler.run(PenPenLib.getInstance(), player.getLocation(), () -> player.openInventory(inventory));
    }

    /**
     * Handles the base menu actions such as:
     * <p>
     * Back button.
     * Previous page.
     * Next page.
     *
     * @param event                InventoryClickEvent.
     * @param backRunnable         Action to run when the back button is clicked.
     * @param previousPageRunnable Action to run when the previous page button is clicked.
     * @param nextPageRunnable     Action to run when the next page button is clicked.
     * @return Whether any of the base actions was handled.
     */
    public static boolean handleBaseMenuActions(InventoryClickEvent event, Runnable backRunnable,
                                                Runnable previousPageRunnable, Runnable nextPageRunnable) {
        Player player = (Player) event.getWhoClicked();
        String menuAction = MenuItem.getMenuAction(event.getCurrentItem());
        if (menuAction == null) return false;
        switch (menuAction) {
            case "back-item" -> {
                if (backRunnable == null) return false;

                backRunnable.run();
                CommonSound.BACK.play(player);
            }
            case "previous-page-item" -> {
                if (previousPageRunnable == null) return false;

                previousPageRunnable.run();
                CommonSound.PREVIOUS_PAGE.play(player);
            }
            case "next-page-item" -> {
                if (nextPageRunnable == null) return false;

                nextPageRunnable.run();
                CommonSound.NEXT_PAGE.play(player);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the passed slot is one of the slots used when creating a dynamic menu frame.
     *
     * @param slot Slot to check.
     * @return True if the passed slot is one of the slots used when creating a dynamic menu frame.
     */
    public static boolean isDynamicFrameSlot(int slot) {
        return DYNAMIC_FRAME_SLOTS.contains(slot);
    }

    /**
     * Returns the nearest menu size corresponding to the passed slot.
     *
     * @param slot Slot to compute.
     * @return Nearest menu size corresponding to the passed slot.
     */
    public static int getNearestMenuSize(int slot) {
        if (slot > MENU_SIZES.last()) return MENU_SIZES.last();

        return MENU_SIZES.ceiling(slot);
    }
}
