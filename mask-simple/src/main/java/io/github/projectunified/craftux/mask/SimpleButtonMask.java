package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A simple button map
 */
public class SimpleButtonMask implements Element, Function<@NotNull UUID, @NotNull Map<Position, Consumer<ActionItem>>> {
    private final Map<BiPredicate<@NotNull UUID, @NotNull ActionItem>, Collection<Position>> buttonSlotMap = new LinkedHashMap<>();

    /**
     * Set the button
     *
     * @param position the position
     * @param button   the button
     */
    public void setButton(Position position, @NotNull BiPredicate<@NotNull UUID, @NotNull ActionItem> button) {
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
                (button, positions) ->
                        positions.forEach(position -> {
                            Consumer<ActionItem> consumer = actionItem -> button.test(uuid, actionItem);
                            map.merge(
                                    position,
                                    consumer,
                                    Consumer::andThen
                            );
                        })
        );

        return map;
    }
}
