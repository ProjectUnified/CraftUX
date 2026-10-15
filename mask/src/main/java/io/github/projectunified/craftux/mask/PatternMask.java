package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A mask that places a button for each character of a pattern, one row per line.
 * Characters without a registered button are skipped.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * PatternMask mask = new PatternMask(Arrays.asList(
 *     "ABC",
 *     "D#E"
 * ));
 * mask.addButton('A', new SimpleButton(new ItemStack(Material.IRON_SWORD)));
 * mask.addButton('#', new SimpleButton(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
 * Map<Position, Consumer<ActionItem>> actions = mask.apply(playerUUID);
 * }</pre>
 */
public class PatternMask implements Element, Mask {
    private final char[][] pattern;
    private final Map<Character, Button> buttons = new HashMap<>();

    /**
     * Create a new mask
     *
     * @param pattern the pattern, one row per array
     */
    public PatternMask(char[][] pattern) {
        this.pattern = pattern;
    }

    /**
     * Create a new mask
     *
     * @param pattern the pattern, one string per row
     */
    public PatternMask(List<String> pattern) {
        this.pattern = new char[pattern.size()][];
        for (int i = 0; i < pattern.size(); i++) {
            this.pattern[i] = pattern.get(i).toCharArray();
        }
    }

    /**
     * Add a button for a character
     *
     * @param character the character
     * @param button    the button
     */
    public void addButton(char character, Button button) {
        buttons.put(character, button);
    }

    /**
     * Get the buttons by character
     *
     * @return the buttons by character
     */
    public Map<Character, Button> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    /**
     * Get a copy of the pattern
     *
     * @return the pattern
     */
    public char[][] getPattern() {
        char[][] clone = new char[pattern.length][];
        for (int i = 0; i < pattern.length; i++) {
            clone[i] = pattern[i].clone();
        }
        return clone;
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Map<Position, Consumer<ActionItem>> map = new HashMap<>();
        Map<Button, Consumer<ActionItem>> buttonMap = new HashMap<>();
        for (int y = 0; y < pattern.length; y++) {
            char[] line = pattern[y];
            for (int x = 0; x < line.length; x++) {
                char c = line[x];
                Button button = buttons.get(c);
                if (button != null) {
                    Consumer<ActionItem> consumer = buttonMap.computeIfAbsent(button, b -> b.apply(uuid));
                    map.put(Position.of(x, y), consumer);
                }
            }
        }
        return map;
    }

    @Override
    public void init() {
        Element.handleIfElement(buttons.values(), Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(buttons.values(), Element::stop);
    }
}
