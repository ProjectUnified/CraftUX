package io.github.projectunified.craftux.minestom;

import io.github.projectunified.craftux.common.Position;
import net.minestom.server.inventory.InventoryType;

/**
 * Utility class for Minestom inventory operations
 */
public interface MinestomInventoryUtil {
    /**
     * Get the number of slots per row for the given inventory type
     *
     * @param inventoryType the inventory type
     * @return the number of slots per row, or 0 if not applicable (e.g., non-grid inventories)
     */
    static int slotPerRow(InventoryType inventoryType) {
        return switch (inventoryType) {
            case CHEST_1_ROW, CHEST_2_ROW, CHEST_3_ROW, CHEST_4_ROW, CHEST_5_ROW, CHEST_6_ROW, SHULKER_BOX -> 9;
            case WINDOW_3X3, CRAFTER_3X3 -> 3;
            default -> 0;
        };
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
