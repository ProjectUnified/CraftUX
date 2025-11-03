package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Represents an item with an associated action that can be triggered.
 * The item can be of any type, and the action is a Consumer that accepts an event object.
 * This class provides methods to set, get, and extend both the item and the action.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ActionItem actionItem = new ActionItem();
 * actionItem.setItem("My Item");
 * actionItem.setAction(event -> System.out.println("Clicked: " + event));
 * actionItem.callAction("click");
 * }</pre>
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
            apply(actionItem);
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
     */
    public void setItem(@Nullable Object item) {
        this.item = item;
    }

    /**
     * Extend the item
     *
     * @param operator the operator to extend the item
     */
    public void extendItem(UnaryOperator<Object> operator) {
        this.item = operator.apply(this.item);
    }

    /**
     * Extend the item if it is of the given class
     *
     * @param itemClass the class to check
     * @param operator  the operator to extend the item
     * @param <T>       the item type
     */
    public <T> void extendItem(Class<T> itemClass, UnaryOperator<T> operator) {
        if (itemClass.isInstance(this.item)) {
            this.item = operator.apply(itemClass.cast(this.item));
        }
    }

    /**
     * Get the item as the given class, or null if it is not of that class
     *
     * @param itemClass the class to check
     * @param <T>       the item type
     * @return the item or null
     */
    public <T> @Nullable T getItem(Class<T> itemClass) {
        if (itemClass.isInstance(this.item)) {
            return itemClass.cast(this.item);
        }
        return null;
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
     */
    public void setAction(@Nullable Consumer<Object> action) {
        this.action = action;
    }

    /**
     * Extend the action
     *
     * @param operator the operator with the event and the old action
     */
    public void extendAction(BiConsumer<Object, Consumer<Object>> operator) {
        Consumer<Object> oldAction = this.action != null ? this.action : event -> {
        };
        this.action = event -> operator.accept(event, oldAction);
    }

    /**
     * Set the action
     *
     * @param eventClass the event class
     * @param action     the action
     * @param <E>        the event type
     */
    public <E> void setAction(Class<E> eventClass, Consumer<E> action) {
        extendAction((event, oldAction) -> {
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
     */
    public <E> void extendAction(Class<E> eventClass, BiConsumer<E, Consumer<Object>> operator) {
        extendAction((event, oldAction) -> {
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

    /**
     * Apply non-null fields from another {@link ActionItem}
     *
     * @param actionItem the action item to copy
     */
    public void apply(@NotNull ActionItem actionItem) {
        if (actionItem.item != null) {
            this.item = actionItem.item;
        }
        if (actionItem.action != null) {
            this.action = actionItem.action;
        }
    }
}
