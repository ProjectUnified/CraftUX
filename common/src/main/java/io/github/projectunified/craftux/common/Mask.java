package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a mask that maps positions to action item consumers for a given player UUID.
 * Masks define the layout and behavior of GUI elements in a grid-based interface.
 *
 * <p>Example implementation:</p>
 * <pre>{@code
 * public class MyMask implements Mask {
 *     @Override
 *     public Map<Position, Consumer<ActionItem>> apply(UUID uuid) {
 *         Map<Position, Consumer<ActionItem>> map = new HashMap<>();
 *         map.put(Position.of(0, 0), actionItem -> {
 *             actionItem.setItem("Button at 0,0");
 *             actionItem.setAction(event -> System.out.println("Clicked at 0,0"));
 *         });
 *         return map;
 *     }
 * }
 * }</pre>
 */
public interface Mask {
    /**
     * Get a map of positions to action item consumers
     *
     * @param uuid the uuid of the player
     * @return the map, or null if no items should be displayed. Can return null in a conditional case (e.g. predicate mask)
     */
    @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid);

    /**
     * Get a map of positions to action items
     *
     * @param uuid the uuid of the player
     * @return the map, or null if no items should be displayed. Can return null in a conditional case (e.g. predicate mask)
     */
    default @Nullable Map<Position, ActionItem> getActionMap(UUID uuid) {
        Map<Position, Consumer<ActionItem>> map = apply(uuid);
        if (map == null) return null;
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    ActionItem actionItem = new ActionItem();
                    entry.getValue().accept(actionItem);
                    return actionItem;
                }));
    }
}
