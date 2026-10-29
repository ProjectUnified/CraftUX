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
    private final Map<Character, List<Position>> positions = new HashMap<>();
    private final Map<Character, Button> buttons = new HashMap<>();

    /**
     * Create a new mask
     *
     * @param pattern the pattern, one row per array
     */
    public PatternMask(char[][] pattern) {
        for (int y = 0; y < pattern.length; y++) {
            char[] line = pattern[y];
            for (int x = 0; x < line.length; x++) {
                char c = line[x];
                positions.computeIfAbsent(c, _char -> new ArrayList<>()).add(Position.of(x, y));
            }
        }
    }

    /**
     * Create a new mask
     *
     * @param pattern the pattern, one string per row
     */
    public PatternMask(List<String> pattern) {
        for (int y = 0; y < pattern.size(); y++) {
            String line = pattern.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                positions.computeIfAbsent(c, _char -> new ArrayList<>()).add(Position.of(x, y));
            }
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
     * Get the parsed position-by-character map
     *
     * @return the position-by-charactor map
     */
    public Map<Character, List<Position>> getPositions() {
        return Collections.unmodifiableMap(positions);
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Map<Position, Consumer<ActionItem>> buttonMap = new HashMap<>();
        for (Map.Entry<Character, Button> entry : buttons.entrySet()) {
            Button button = entry.getValue();
            List<Position> positionList = positions.get(entry.getKey());
            if (positionList != null) {
                Consumer<ActionItem> actionItemConsumer = button.apply(uuid);
                positionList.forEach(position -> buttonMap.put(position, actionItemConsumer));
            }
        }
        return buttonMap;
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
