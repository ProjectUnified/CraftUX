package io.github.projectunified.craftux.spigot;

import io.github.projectunified.craftux.common.Position;
import org.bukkit.event.inventory.InventoryType;

/**
 * Utility class for Spigot inventory operations
 */
public interface SpigotInventoryUtil {
    /**
     * Get the number of slots per row for the given inventory type
     *
     * @param inventoryType the inventory type
     * @return the number of slots per row, or 0 if not applicable (e.g., non-grid inventories)
     */
    static int slotPerRow(InventoryType inventoryType) {
        switch (inventoryType) {
            case CHEST:
            case ENDER_CHEST:
            case SHULKER_BOX:
                return 9;
            case DISPENSER:
            case DROPPER:
            case HOPPER:
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Convert a position to a slot index
     *
     * @param position      the position
     * @param inventoryType the inventory type
     * @return the slot index
     */
    static int toSlot(Position position, InventoryType inventoryType) {
        int slotPerRow = slotPerRow(inventoryType);
        return position.getX() + position.getY() * slotPerRow;
    }

    /**
     * Convert a slot index to a position
     *
     * @param slot          the slot index
     * @param inventoryType the inventory type
     * @return the position
     */
    static Position toPosition(int slot, InventoryType inventoryType) {
        int slotPerRow = slotPerRow(inventoryType);
        if (slotPerRow < 1) {
            return Position.of(slot, 0);
        } else {
            return Position.of(slot % slotPerRow, slot / slotPerRow);
        }
    }
}
