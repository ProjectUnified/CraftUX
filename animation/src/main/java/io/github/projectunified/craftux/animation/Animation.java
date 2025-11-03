package io.github.projectunified.craftux.animation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages a sequence of frames that cycle over time with a specified period.
 * Provides methods to get the current frame based on elapsed time and reset the animation.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * List<String> frames = Arrays.asList("Frame1", "Frame2", "Frame3");
 * Animation<String> animation = new Animation<>(frames, 1000); // 1 second per frame
 * String current = animation.getCurrentFrame(); // Gets current frame based on time
 * }</pre>
 *
 * @param <T> the frame type
 */
public class Animation<T> {
    private final List<T> frames;
    private final long periodMillis;
    private final AnimationMode mode;
    private final AtomicLong startMillis = new AtomicLong(-1);

    /**
     * Creates a new Animation with the specified frames and period.
     *
     * @param frames       the list of frames to cycle through
     * @param periodMillis the period in milliseconds between frame changes
     * @param mode         the mode of the animation
     * @throws IllegalArgumentException if frames is empty or periodMillis is not positive
     */
    public Animation(List<T> frames, long periodMillis, AnimationMode mode) {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("Frames cannot be empty");
        }
        if (periodMillis <= 0) {
            throw new IllegalArgumentException("Period must be positive");
        }

        this.frames = frames;
        this.periodMillis = periodMillis;
        this.mode = mode;
    }

    /**
     * Creates a new Animation with the specified frames and period.
     *
     * @param frames       the list of frames to cycle through
     * @param periodMillis the period in milliseconds between frame changes
     * @throws IllegalArgumentException if frames is empty or periodMillis is not positive
     */
    public Animation(List<T> frames, long periodMillis) {
        this(frames, periodMillis, AnimationMode.REPEAT);
    }

    /**
     * Get the frames
     *
     * @return the frames
     */
    public List<T> getFrames() {
        return Collections.unmodifiableList(frames);
    }

    /**
     * Get the frame based on the current time
     *
     * @param currentMillis the current time in milliseconds
     * @return the frame
     */
    public T getCurrentFrame(long currentMillis) {
        long startMillis = this.startMillis.get();
        if (startMillis < 0) {
            startMillis = currentMillis;
            this.startMillis.set(startMillis);
        }

        if (!isFirstRun(currentMillis)) {
            if (mode == AnimationMode.ONE_TIME) {
                return null;
            } else if (mode == AnimationMode.ONE_TIME_KEEP_LAST) {
                return frames.getLast();
            }
        }

        long diff = currentMillis - startMillis;
        int index = (int) (diff / periodMillis) % frames.size();
        return frames.get(index);
    }

    /**
     * Get the frame based on the current time
     *
     * @return the frame
     */
    public T getCurrentFrame() {
        return getCurrentFrame(System.currentTimeMillis());
    }

    /**
     * Reset the animation
     */
    public void reset() {
        this.startMillis.set(-1);
    }

    /**
     * Check if it's the first run. It will return true if the animation is running for the first time.
     *
     * @param currentMillis the current time in milliseconds
     * @return true if it's the first run
     */
    public boolean isFirstRun(long currentMillis) {
        long startMillis = this.startMillis.get();
        return startMillis < 0 || currentMillis - startMillis < periodMillis * frames.size();
    }

    /**
     * Check if it's the first run. It will return true if the animation is running for the first time.
     *
     * @return true if it's the first run
     */
    public boolean isFirstRun() {
        return isFirstRun(System.currentTimeMillis());
    }
}
