package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The utility class for masks
 */
public class MaskUtils {

    private MaskUtils() {
        // EMPTY
    }

    /**
     * Generate the stream of positions in the area between two positions
     *
     * @param position1 the first position
     * @param position2 the second position
     * @return the stream of positions
     */
    public static @NotNull List<Position> generateAreaPositions(@NotNull Position position1, @NotNull Position position2) {
        Position max = Position.maxPosition(position1, position2);
        Position min = Position.minPosition(position1, position2);
        return IntStream.rangeClosed(min.getY(), max.getY())
                .mapToObj(y -> IntStream.rangeClosed(min.getX(), max.getX()).mapToObj(x -> Position.of(x, y)))
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    /**
     * Get the stream of positions drawing the outline of the area between 2 positions
     *
     * @param position1 the first position
     * @param position2 the second position
     * @return the stream of positions
     */
    public static @NotNull List<Position> generateOutlinePositions(@NotNull Position position1, @NotNull Position position2) {
        Position max = Position.maxPosition(position1, position2);
        Position min = Position.minPosition(position1, position2);
        Stream<Position> top = IntStream.rangeClosed(min.getX(), max.getX()).mapToObj(x -> Position.of(x, min.getY()));
        Stream<Position> right = IntStream.rangeClosed(min.getY(), max.getY()).mapToObj(y -> Position.of(max.getX(), y));
        Stream<Position> bottom = IntStream.rangeClosed(min.getX(), max.getX()).mapToObj(x -> Position.of(x, max.getY()));
        Stream<Position> left = IntStream.rangeClosed(min.getY(), max.getY()).mapToObj(y -> Position.of(min.getX(), y));
        return Stream.concat(Stream.concat(top, right), Stream.concat(bottom, left)).distinct().collect(Collectors.toList());
    }
}
