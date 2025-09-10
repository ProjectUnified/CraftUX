package io.github.projectunified.craftux.simple;

import io.github.projectunified.craftux.common.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * A simple button mask
 */
public class SimpleButtonMask implements Element, Mask {
    private final Map<Button, Collection<Position>> buttonSlotMap = new LinkedHashMap<>();

    /**
     * Set the button
     *
     * @param position the position
     * @param button   the button
     */
    public void setButton(Position position, @NotNull Button button) {
        buttonSlotMap.computeIfAbsent(button, b -> new ArrayList<>()).add(position);
    }

    @Override
    public void init() {
        Element.handleIfElement(buttonSlotMap.keySet(), Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(buttonSlotMap.keySet(), Element::stop);
        buttonSlotMap.clear();
    }

    @Override
    public @NotNull Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Map<Position, Consumer<ActionItem>> map = new HashMap<>();

        buttonSlotMap.forEach(
                (button, positions) -> {
                    Consumer<ActionItem> consumer = button.apply(uuid);
                    positions.forEach(position -> map.merge(
                            position,
                            consumer,
                            Consumer::andThen
                    ));
                }
        );

        return map;
    }
}
