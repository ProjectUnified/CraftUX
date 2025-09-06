package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.GUIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * A base button that handles multiple child buttons
 */
public abstract class MultiButton implements GUIElement, Function<@NotNull UUID, @Nullable ActionItem> {
    protected final List<Function<@NotNull UUID, @Nullable ActionItem>> buttons = new ArrayList<>();

    /**
     * Whether to require child buttons
     *
     * @return true if child buttons are required
     */
    protected boolean requireChildButtons() {
        return false;
    }

    /**
     * Add child buttons
     *
     * @param buttons the child buttons
     * @param <T>     the type of the button
     */
    public final <T extends Function<@NotNull UUID, @Nullable ActionItem>> void addButton(@NotNull Collection<@NotNull T> buttons) {
        this.buttons.addAll(buttons);
    }

    /**
     * Add child buttons
     *
     * @param button the button
     */
    @SafeVarargs
    public final void addButton(@NotNull Function<@NotNull UUID, @Nullable ActionItem>... button) {
        addButton(Arrays.asList(button));
    }

    /**
     * Get the list of child buttons
     *
     * @return the list of child buttons
     */
    public final List<Function<@NotNull UUID, @Nullable ActionItem>> getButtons() {
        return Collections.unmodifiableList(this.buttons);
    }

    @Override
    public void init() {
        if (requireChildButtons() && this.buttons.isEmpty()) {
            throw new IllegalArgumentException("There is no child button for this button");
        }
        GUIElement.handleIfElement(this.buttons, GUIElement::init);
    }

    @Override
    public void stop() {
        GUIElement.handleIfElement(this.buttons, GUIElement::stop);
    }
}
