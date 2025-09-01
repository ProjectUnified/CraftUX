package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.event.ClickEvent;
import io.github.projectunified.craftux.common.item.ActionItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A simple button
 */
public class SimpleButton implements Function<@NotNull UUID, @NotNull ActionItem> {
    private final Function<UUID, Object> itemFunction;
    private final Consumer<ClickEvent> consumer;

    /**
     * Create a new simple button
     *
     * @param itemFunction the item function
     * @param consumer     the consumer
     */
    public SimpleButton(@NotNull Function<@NotNull UUID, @Nullable Object> itemFunction, @NotNull Consumer<@NotNull ClickEvent> consumer) {
        this.itemFunction = itemFunction;
        this.consumer = consumer;
    }

    /**
     * Create a new button
     *
     * @param item     the item
     * @param consumer the consumer
     */
    public SimpleButton(@Nullable Object item, @NotNull Consumer<@NotNull ClickEvent> consumer) {
        this(uuid -> item, consumer);
    }

    /**
     * Create a new button with a null item
     *
     * @param consumer the consumer
     */
    public SimpleButton(@NotNull Consumer<@NotNull ClickEvent> consumer) {
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
    public @NotNull ActionItem apply(@NotNull UUID uuid) {
        return new ActionItem()
                .setItem(itemFunction.apply(uuid))
                .setAction(event -> {
                    if (event instanceof ClickEvent) {
                        consumer.accept((ClickEvent) event);
                    }
                });
    }
}