package io.github.projectunified.craftux.spigot.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * A button that stores an output item per player and allows taking it to the cursor when clicked with an empty cursor.
 * Useful for creating output slots where players can retrieve items.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * OutputButton outputButton = new OutputButton();
 * outputButton.setOutputItem(playerUUID, new ItemStack(Material.DIAMOND));
 * outputButton.setDisplayItemFunction((uuid, item) -> {
 *     if (item == null) return new ItemStack(Material.AIR);
 *     return item;
 * });
 * }</pre>
 */
public class OutputButton implements Element, Button {
    private final Map<UUID, ItemStack> map = new IdentityHashMap<>();
    private BiFunction<@NotNull UUID, @Nullable ItemStack, @Nullable ItemStack> displayItemFunction = (uuid, item) -> item;

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        actionItem.setItem(displayItemFunction.apply(uuid, getOutputItem(uuid)));
        actionItem.setAction(InventoryClickEvent.class, event -> {
            ItemStack item = event.getCursor();
            if (item != null && item.getType() != Material.AIR) {
                return;
            }
            ItemStack storeItem = getOutputItem(uuid);
            event.getWhoClicked().setItemOnCursor(storeItem);
            setOutputItem(uuid, null);
        });
        return true;
    }

    @Override
    public void stop() {
        map.clear();
    }

    /**
     * Set the output item for the unique id
     *
     * @param uuid      the unique id
     * @param itemStack the item, or null to remove the output button
     */
    public void setOutputItem(@NotNull UUID uuid, @Nullable ItemStack itemStack) {
        map.compute(uuid, (uuid1, item) -> itemStack);
    }

    /**
     * Get the output item for the unique id
     *
     * @param uuid the unique id
     * @return the item
     */
    @Nullable
    public ItemStack getOutputItem(@NotNull UUID uuid) {
        return map.get(uuid);
    }

    /**
     * Set the function to display the item on the GUI
     *
     * @param displayItemFunction the function
     * @return this instance
     */
    @Contract("_ -> this")
    public OutputButton setDisplayItemFunction(@NotNull BiFunction<UUID, ItemStack, ItemStack> displayItemFunction) {
        this.displayItemFunction = displayItemFunction;
        return this;
    }
}
