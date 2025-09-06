package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * The simple mask with a single button
 */
public class SingleMask implements Element, Function<@NotNull UUID, @Nullable Map<Position, ActionItem>> {
    protected final Position position;
    protected final Function<@NotNull UUID, @Nullable ActionItem> button;

    /**
     * Create a new mask
     *
     * @param position the position
     * @param button   the button
     */
    public SingleMask(Position position, @NotNull Function<@NotNull UUID, @Nullable ActionItem> button) {
        this.position = position;
        this.button = button;
    }

    @Override
    public void init() {
        Element.handleIfElement(this.button, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(this.button, Element::stop);
    }

    @Override
    public @Nullable Map<Position, ActionItem> apply(@NotNull UUID uuid) {
        ActionItem actionItem = this.button.apply(uuid);
        return actionItem == null ? null : Collections.singletonMap(this.position, actionItem);
    }
}
