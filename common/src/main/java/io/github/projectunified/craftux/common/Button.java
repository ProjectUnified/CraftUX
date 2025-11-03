package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a button that can apply actions to an ActionItem based on a player's UUID.
 * Buttons are used in GUI systems to define interactive elements that respond to player actions.
 *
 * <p>Example implementation:</p>
 * <pre>{@code
 * public class MyButton implements Button {
 *     @Override
 *     public boolean apply(UUID uuid, ActionItem actionItem) {
 *         actionItem.setItem("My Button");
 *         actionItem.setAction(event -> System.out.println("Button clicked by " + uuid));
 *         return true;
 *     }
 * }
 * }</pre>
 */
public interface Button {
    /**
     * Apply actions to the action item
     *
     * @param uuid       the uuid of the player
     * @param actionItem the action item
     * @return true if any action was applied. Can return false in a conditional case (e.g. predicate button)
     */
    boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem);

    /**
     * Get a consumer that applies actions to the action item
     *
     * @param uuid the uuid of the player
     * @return the consumer
     */
    default Consumer<ActionItem> apply(@NotNull UUID uuid) {
        return actionItem -> apply(uuid, actionItem);
    }
}
