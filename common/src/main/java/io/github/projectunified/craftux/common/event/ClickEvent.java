package io.github.projectunified.craftux.common.event;

import io.github.projectunified.craftux.common.inventory.InventoryPosition;

/**
 * The event when a player clicks on the GUI
 */
public interface ClickEvent extends ViewerEvent, CancellableEvent {
    /**
     * Get the position that was clicked
     *
     * @return the position
     */
    InventoryPosition getPosition();
}
