package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * A button that conditionally applies actions from one of two buttons based on a predicate
 * evaluated against the player's UUID. Uses a fallback button if the predicate fails.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * PredicateButton predicateButton = new PredicateButton();
 * predicateButton.setButton(new SimpleButton(new ItemStack(Material.COMMAND_BLOCK)));
 * predicateButton.setFallbackButton(new SimpleButton(new ItemStack(Material.CHEST)));
 * predicateButton.setViewPredicate(uuid -> isAdmin(uuid)); // Check if player is admin
 * }</pre>
 */
public class PredicateButton implements Element, Button {
    private @Nullable Button button = null;
    private @Nullable Button fallbackButton = null;
    private @Nullable Predicate<UUID> viewPredicate = null;

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
     * Get the button
     *
     * @return the button
     */
    public @Nullable Button getButton() {
        return button;
    }

    /**
     * Set the button
     *
     * @param button the button
     */
    public void setButton(@Nullable Button button) {
        this.button = button;
    }

    /**
     * Get the fallback button
     *
     * @return the fallback button
     */
    public @Nullable Button getFallbackButton() {
        return fallbackButton;
    }

    /**
     * Set the fallback button
     *
     * @param fallbackButton the fallback button
     */
    public void setFallbackButton(@Nullable Button fallbackButton) {
        this.fallbackButton = fallbackButton;
    }

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        Button buttonToUse = viewPredicate == null || viewPredicate.test(uuid) ? button : fallbackButton;
        return buttonToUse != null && buttonToUse.apply(uuid, actionItem);
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
