package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The action item
 */
public final class ActionItem {
    private @Nullable Object item;
    private @Nullable Consumer<Object> action;

    /**
     * Create an empty {@link ActionItem}
     */
    public ActionItem() {
        // EMPTY
    }

    /**
     * Create a copy of {@link ActionItem}
     *
     * @param actionItem       the action item to copy
     * @param applyOnlyNonnull if true, only non-null fields will be copied
     */
    public ActionItem(@NotNull ActionItem actionItem, boolean applyOnlyNonnull) {
        if (applyOnlyNonnull) {
            if (actionItem.item != null) {
                this.item = actionItem.item;
            }
            if (actionItem.action != null) {
                this.action = actionItem.action;
            }
        } else {
            this.item = actionItem.item;
            this.action = actionItem.action;
        }
    }

    /**
     * Create a copy of {@link ActionItem}
     *
     * @param actionItem the action item to copy
     */
    public ActionItem(@NotNull ActionItem actionItem) {
        this(actionItem, false);
    }

    /**
     * Get the item
     *
     * @return the item
     */
    public @Nullable Object getItem() {
        return item;
    }

    /**
     * Set the item
     *
     * @param item the item
     * @return this object
     */
    public ActionItem setItem(@Nullable Object item) {
        this.item = item;
        return this;
    }

    /**
     * Get the item unchecked
     *
     * @param <T> the item type
     * @return the item
     */
    public <T> @Nullable T getItemUnchecked() {
        //noinspection unchecked
        return (T) item;
    }

    /**
     * Get the action
     *
     * @return the action
     */
    public @Nullable Consumer<Object> getAction() {
        return action;
    }

    /**
     * Set the action
     *
     * @param action the action
     * @return this object
     */
    public ActionItem setAction(@Nullable Consumer<Object> action) {
        this.action = action;
        return this;
    }

    /**
     * Extend the action
     *
     * @param operator the operator with the event and the old action
     * @return this object
     */
    public ActionItem extendAction(BiConsumer<Object, Consumer<Object>> operator) {
        Consumer<Object> oldAction = this.action != null ? this.action : event -> {
        };
        this.action = event -> operator.accept(event, oldAction);
        return this;
    }

    /**
     * Set the action
     *
     * @param eventClass the event class
     * @param action     the action
     * @param <E>        the event type
     * @return this object
     */
    public <E> ActionItem setAction(Class<E> eventClass, Consumer<E> action) {
        return extendAction((event, oldAction) -> {
            if (eventClass.isInstance(event)) {
                action.accept(eventClass.cast(event));
            } else {
                oldAction.accept(event);
            }
        });
    }

    /**
     * Extend the action
     *
     * @param eventClass the event class
     * @param operator   the operator with the event and the old action
     * @param <E>        the event type
     * @return this object
     */
    public <E> ActionItem extendAction(Class<E> eventClass, BiConsumer<E, Consumer<Object>> operator) {
        return extendAction((event, oldAction) -> {
            if (eventClass.isInstance(event)) {
                operator.accept(eventClass.cast(event), oldAction);
            } else {
                oldAction.accept(event);
            }
        });
    }

    /**
     * Call the action
     *
     * @param event the event
     */
    public void callAction(Object event) {
        if (action != null) {
            action.accept(event);
        }
    }
}
