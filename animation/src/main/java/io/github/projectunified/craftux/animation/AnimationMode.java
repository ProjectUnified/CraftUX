package io.github.projectunified.craftux.animation;

/**
 * The mode of {@link Animation}
 */
public enum AnimationMode {
    /**
     * The frame will be repeated
     */
    REPEAT,
    /**
     * The {@link Animation#getCurrentFrame(long)}} will return {@link null} if the animation is completed ({@link Animation#isFirstRun(long)} returns {@link false})
     */
    ONE_TIME,
    /**
     * The {@link Animation#getCurrentFrame(long)}} will return the last element of the {@link Animation#getFrames()} if the animation is completed ({@link Animation#isFirstRun(long)} returns {@link false})
     */
    ONE_TIME_KEEP_LAST
}
