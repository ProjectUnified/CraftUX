package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.GUIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The button with predicates
 */
public class PredicateButton implements GUIElement, Function<@NotNull UUID, @Nullable ActionItem> {
    private Function<@NotNull UUID, @Nullable ActionItem> button = context -> null;
    private Function<@NotNull UUID, @Nullable ActionItem> fallbackButton = context -> null;
    private @Nullable Predicate<UUID> viewPredicate = null;
    private @Nullable Predicate<Object> actionPredicate = null;

    /**
     * Get the view predicate
     *
     * @return the view predicate
     */
    public @Nullable Predicate<UUID> getViewPredicate() {
        return viewPredicate;
    }

    /**
     * Set the view predicate
     *
     * @param viewPredicate the view predicate
     */
    public void setViewPredicate(@NotNull Predicate<@NotNull UUID> viewPredicate) {
        this.viewPredicate = viewPredicate;
    }

    /**
     * Get the action predicate
     *
     * @return the action predicate
     */
    public @Nullable Predicate<@NotNull Object> getActionPredicate() {
        return actionPredicate;
    }

    /**
     * Set the action predicate
     *
     * @param actionPredicate the action predicate
     */
    public void setActionPredicate(@Nullable Predicate<@NotNull Object> actionPredicate) {
        this.actionPredicate = actionPredicate;
    }

    /**
     * Set the action predicate for a specific event class
     *
     * @param eventClass      the event class
     * @param actionPredicate the action predicate
     * @param <E>             the event type
     */
    public <E> void setActionPredicate(@NotNull Class<E> eventClass, @NotNull Predicate<@NotNull E> actionPredicate) {
        Predicate<Object> oldActionPredicate = this.actionPredicate;
        this.actionPredicate = event -> {
            if (eventClass.isInstance(event) && !actionPredicate.test(eventClass.cast(event))) {
                return false;
            }
            return oldActionPredicate == null || oldActionPredicate.test(event);
        };
    }


    /**
     * Get the button
     *
     * @return the button
     */
    public Function<@NotNull UUID, @Nullable ActionItem> getButton() {
        return button;
    }

    /**
     * Set the button
     *
     * @param button the button
     */
    public void setButton(@NotNull Function<@NotNull UUID, @Nullable ActionItem> button) {
        this.button = button;
    }

    /**
     * Get the fallback button
     *
     * @return the fallback button
     */
    public Function<@NotNull UUID, @Nullable ActionItem> getFallbackButton() {
        return fallbackButton;
    }

    /**
     * Set the fallback button
     *
     * @param fallbackButton the fallback button
     */
    public void setFallbackButton(@NotNull Function<@NotNull UUID, @Nullable ActionItem> fallbackButton) {
        this.fallbackButton = fallbackButton;
    }

    @Override
    public @Nullable ActionItem apply(@NotNull UUID uuid) {
        ActionItem actionItem;
        if (viewPredicate == null || viewPredicate.test(uuid)) {
            actionItem = button.apply(uuid);
        } else {
            actionItem = fallbackButton.apply(uuid);
        }

        if (actionItem == null) {
            return null;
        }

        ActionItem copy = new ActionItem(actionItem);
        if (actionPredicate == null) {
            return copy;
        }

        return new ActionItem(actionItem).extendAction((event, action) -> {
            if (actionPredicate.test(event)) {
                action.accept(event);
            }
        });
    }

    @Override
    public void init() {
        GUIElement.handleIfElement(button, GUIElement::init);
        GUIElement.handleIfElement(fallbackButton, GUIElement::init);
    }

    @Override
    public void stop() {
        GUIElement.handleIfElement(button, GUIElement::stop);
        GUIElement.handleIfElement(fallbackButton, GUIElement::stop);
    }
}
