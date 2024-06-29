package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.PenPenLib;
import me.eeshe.penpenlib.util.PlaceholderUtil;
import me.eeshe.penpenlib.util.StringUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuItem {
    private static final NamespacedKey MENU_ITEM_KEY = new NamespacedKey(PenPenLib.getInstance(), "menu_item");

    private final String id;
    private final ItemStack item;
    private final int slot;

    public MenuItem(String id, ItemStack item, int slot) {
        this.id = id;
        this.item = item;
        this.slot = slot;
    }

    /**
     * Extracts the menu action stored in the passed item's persistent data container.
     *
     * @param item Item whose action will be extracted.
     * @return Menu action stored in the passed item's persistent data container.
     */
    public static String getMenuAction(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return null;

        return item.getItemMeta().getPersistentDataContainer().get(MENU_ITEM_KEY, PersistentDataType.STRING);
    }

    /**
     * Generates the ItemStack representing the MenuItem.
     *
     * @return ItemStack representing the MenuItem.
     */
    public ItemStack generateItemStack(Map<String, String> placeholders) {
        ItemStack item = generateItemStack();
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        String displayName = PlaceholderUtil.formatPlaceholders(meta.getDisplayName() != null ? meta.getDisplayName() : "", placeholders);
        List<String> lore = PlaceholderUtil.formatPlaceholders(meta.getLore() != null ? meta.getLore() : new ArrayList<>(), placeholders);
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Generates the ItemStack representing the MenuItem.
     *
     * @return ItemStack representing the MenuItem.
     */
    public ItemStack generateItemStackWithListPlaceholders(Map<String, List<String>> placeholders) {
        if (item == null) return null;
        ItemStack item = this.item.clone();
        ItemMeta meta = item.getItemMeta();

        String displayName = meta.getDisplayName();
        List<String> lore = meta.getLore();
        for (Map.Entry<String, List<String>> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            List<String> value = new ArrayList<>(entry.getValue());

            displayName = displayName.replace(placeholder, String.join("", value));
            if (lore != null) {
                lore = new ArrayList<>(lore);
                for (String loreLine : new ArrayList<>(lore)) {
                    if (!loreLine.contains(placeholder)) continue;

                    int index = lore.indexOf(loreLine);
                    lore.remove(loreLine);
                    value.replaceAll(StringUtil::formatColor);
                    if (value.size() == 1) {
                        lore.add(index, loreLine.replace(placeholder, value.get(0)));
                    } else {
                        lore.addAll(index, value);
                    }
                }
            }
        }
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(MENU_ITEM_KEY, PersistentDataType.STRING, id);
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Generates the base MenuItem ItemStack without changing its lore or display name with placeholders.
     *
     * @return Base MenuItem ItemStack without changing its lore.
     */
    private ItemStack generateItemStack() {
        if (item == null) return null;
        ItemStack item = this.item.clone();
        ItemMeta meta = item.getItemMeta();

        String displayName = meta.getDisplayName();
        List<String> lore = meta.getLore();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(MENU_ITEM_KEY, PersistentDataType.STRING, id);
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);

        return item;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    public int getAdaptiveSlot(int inventorySize) {
        if (slot < 0) {
            return inventorySize + slot + 1;
        }
        return slot;
    }
}
