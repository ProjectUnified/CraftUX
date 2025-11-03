package io.github.projectunified.craftux.common;

import java.util.Objects;

/**
 * Represents a 2D position with x and y coordinates in the GUI grid.
 * Positions are used to locate elements in the interface.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Position pos = Position.of(1, 2);
 * int x = pos.getX(); // 1
 * int y = pos.getY(); // 2
 * }</pre>
 */
public class Position {
    private final int x;
    private final int y;

    /**
     * Creates a new position
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new position
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    /**
     * Get the max position
     *
     * @param x1 the x of the first position
     * @param y1 the y of the first position
     * @param x2 the x of the second position
     * @param y2 the y of the second position
     * @return the max position
     */
    public static Position maxPosition(int x1, int y1, int x2, int y2) {
        return Position.of(Math.max(x1, x2), Math.max(y1, y2));
    }

    /**
     * Get the max position
     *
     * @param position1 the first position
     * @param position2 the second position
     * @return the max position
     */
    public static Position maxPosition(Position position1, Position position2) {
        return maxPosition(position1.getX(), position1.getY(), position2.getX(), position2.getY());
    }

    /**
     * Get the min position
     *
     * @param x1 the x of the first position
     * @param y1 the y of the first position
     * @param x2 the x of the second position
     * @param y2 the y of the second position
     * @return the min position
     */
    public static Position minPosition(int x1, int y1, int x2, int y2) {
        return Position.of(Math.min(x1, x2), Math.min(y1, y2));
    }

    /**
     * Get the min position
     *
     * @param position1 the first position
     * @param position2 the second position
     * @return the min position
     */
    public static Position minPosition(Position position1, Position position2) {
        return minPosition(position1.getX(), position1.getY(), position2.getX(), position2.getY());
    }

    /**
     * Get the x coordinate
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position that = (Position) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
