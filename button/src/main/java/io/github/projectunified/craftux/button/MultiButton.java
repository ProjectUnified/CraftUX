package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A base class for buttons that manage multiple child buttons.
 * Provides functionality to add, retrieve, and manage lifecycle of child buttons.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * MultiButton multiButton = new MyMultiButton();
 * multiButton.addButton(
 *     new SimpleButton(new ItemStack(Material.RED_WOOL)),
 *     new SimpleButton(new ItemStack(Material.BLUE_WOOL))
 * );
 * List<Button> buttons = multiButton.getButtons();
 * }</pre>
 */
public abstract class MultiButton implements Element, Button {
    protected final List<Button> buttons = new ArrayList<>();

    /**
     * Add child buttons
     *
     * @param buttons the child buttons
     * @param <T>     the type of the button
     */
    public final <T extends Button> void addButton(@NotNull Collection<@NotNull T> buttons) {
        this.buttons.addAll(buttons);
    }

    /**
     * Add child buttons
     *
     * @param button the button
     */
    public final void addButton(@NotNull Button... button) {
        addButton(Arrays.asList(button));
    }

    /**
     * Get the list of child buttons
     *
     * @return the list of child buttons
     */
    public final List<Button> getButtons() {
        return Collections.unmodifiableList(this.buttons);
    }

    @Override
    public void init() {
        Element.handleIfElement(this.buttons, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(this.buttons, Element::stop);
    }
}
