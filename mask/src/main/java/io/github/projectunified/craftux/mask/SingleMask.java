package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The simple mask with a single button
 */
public class SingleMask implements Element, Function<@NotNull UUID, @Nullable Map<Position, Consumer<ActionItem>>> {
    protected final Position position;
    protected final BiPredicate<@NotNull UUID, @NotNull ActionItem> button;

    /**
     * Create a new mask
     *
     * @param position the position
     * @param button   the button
     */
    public SingleMask(Position position, @NotNull BiPredicate<@NotNull UUID, @NotNull ActionItem> button) {
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
    public @NotNull Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        return Collections.singletonMap(position, actionItem -> button.test(uuid, actionItem));
    }
}
