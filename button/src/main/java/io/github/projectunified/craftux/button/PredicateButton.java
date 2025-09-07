package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * The button with predicates
 */
public class PredicateButton implements Element, BiPredicate<@NotNull UUID, @Nullable ActionItem> {
    private @Nullable BiPredicate<@NotNull UUID, @NotNull ActionItem> button = null;
    private @Nullable BiPredicate<@NotNull UUID, @NotNull ActionItem> fallbackButton = null;
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
     * Expand the action predicate
     *
     * @param actionPredicate the action predicate
     */
    public void expandActionPredicate(@NotNull BiPredicate<@NotNull Object, @Nullable Predicate<Object>> actionPredicate) {
        Predicate<Object> oldActionPredicate = this.actionPredicate;
        this.actionPredicate = event -> actionPredicate.test(event, oldActionPredicate);
    }

    /**
     * Expand the action predicate for a specific event class
     *
     * @param eventClass      the event class
     * @param actionPredicate the action predicate
     * @param <E>             the event type
     */
    public <E> void expandActionPredicate(@NotNull Class<E> eventClass, @NotNull BiPredicate<@NotNull E, @Nullable Predicate<Object>> actionPredicate) {
        Predicate<Object> oldActionPredicate = this.actionPredicate;
        this.actionPredicate = event -> {
            if (eventClass.isInstance(event)) {
                if (!actionPredicate.test(eventClass.cast(event), oldActionPredicate)) {
                    return false;
                }
            }
            return oldActionPredicate == null || oldActionPredicate.test(event);
        };
    }


    /**
     * Get the button
     *
     * @return the button
     */
    public @Nullable BiPredicate<@NotNull UUID, @NotNull ActionItem> getButton() {
        return button;
    }

    /**
     * Set the button
     *
     * @param button the button
     */
    public void setButton(@Nullable BiPredicate<@NotNull UUID, @NotNull ActionItem> button) {
        this.button = button;
    }

    /**
     * Get the fallback button
     *
     * @return the fallback button
     */
    public @Nullable BiPredicate<@NotNull UUID, @NotNull ActionItem> getFallbackButton() {
        return fallbackButton;
    }

    /**
     * Set the fallback button
     *
     * @param fallbackButton the fallback button
     */
    public void setFallbackButton(@Nullable BiPredicate<@NotNull UUID, @NotNull ActionItem> fallbackButton) {
        this.fallbackButton = fallbackButton;
    }

    @Override
    public boolean test(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        BiPredicate<@NotNull UUID, @NotNull ActionItem> buttonToUse = viewPredicate == null || viewPredicate.test(uuid) ? button : fallbackButton;
        if (buttonToUse == null) {
            return false;
        }

        boolean result = buttonToUse.test(uuid, actionItem);
        if (actionPredicate != null) {
            actionItem.extendAction((event, action) -> {
                if (actionPredicate.test(event)) {
                    action.accept(event);
                }
            });
            return true;
        }
        return result;
    }

    @Override
    public void init() {
        Element.handleIfElement(button, Element::init);
        Element.handleIfElement(fallbackButton, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(button, Element::stop);
        Element.handleIfElement(fallbackButton, Element::stop);
    }
}
