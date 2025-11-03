package io.github.projectunified.craftux.simple;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A simple implementation of Button that sets an item and action for an ActionItem.
 * The item can be a static value or computed per player, and the action handles click events.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Static item and action
 * SimpleButton button = new SimpleButton(new ItemStack(Material.DIAMOND), event -> System.out.println("Clicked"));
 *
 * // Dynamic item based on player
 * SimpleButton dynamicButton = new SimpleButton(
 *     uuid -> new ItemStack(Material.EMERALD, getPlayerLevel(uuid)),
 *     event -> System.out.println("Dynamic button clicked")
 * );
 * }</pre>
 */
public class SimpleButton implements Button {
    private final Function<UUID, Object> itemFunction;
    private final Consumer<Object> consumer;

    /**
     * Create a new simple button
     *
     * @param itemFunction the item function
     * @param consumer     the consumer
     */
    public SimpleButton(@NotNull Function<@NotNull UUID, @Nullable Object> itemFunction, @NotNull Consumer<@NotNull Object> consumer) {
        this.itemFunction = itemFunction;
        this.consumer = consumer;
    }

    /**
     * Create a new button
     *
     * @param item     the item
     * @param consumer the consumer
     */
    public SimpleButton(@Nullable Object item, @NotNull Consumer<@NotNull Object> consumer) {
        this(uuid -> item, consumer);
    }

    /**
     * Create a new button with a null item
     *
     * @param consumer the consumer
     */
    public SimpleButton(@NotNull Consumer<@NotNull Object> consumer) {
        this((Object) null, consumer);
    }

    /**
     * Create a new button
     *
     * @param itemFunction the item function
     */
    public SimpleButton(@NotNull Function<@NotNull UUID, @Nullable Object> itemFunction) {
        this(itemFunction, event -> {
        });
    }

    /**
     * Create a new button
     *
     * @param item the item
     */
    public SimpleButton(@Nullable Object item) {
        this(uuid -> item);
    }

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        actionItem.setItem(itemFunction.apply(uuid));
        actionItem.setAction(consumer);
        return true;
    }
}