package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A mask that places a single button at a specified position.
 * Useful for creating simple GUI elements with one interactive item.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SingleMask mask = new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.ANVIL)));
 * Map<Position, Consumer<ActionItem>> actions = mask.apply(playerUUID);
 * }</pre>
 */
public class SingleMask implements Element, Mask {
    protected final Position position;
    protected final Button button;

    /**
     * Create a new mask
     *
     * @param position the position
     * @param button   the button
     */
    public SingleMask(Position position, Button button) {
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
        return Collections.singletonMap(position, button.apply(uuid));
    }
}
