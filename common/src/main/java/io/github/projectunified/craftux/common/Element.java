package io.github.projectunified.craftux.common;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents an element in the GUI that can be initialized and stopped.
 * Elements are components that make up the user interface and may require lifecycle management.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class MyElement implements Element {
 *     @Override
 *     public void init() {
 *         // Initialize resources
 *     }
 *
 *     @Override
 *     public void stop() {
 *         // Clean up resources
 *     }
 * }
 * }</pre>
 */
public interface Element {
    /**
     * Handle the object if it is an instance of {@link Element}
     *
     * @param o               the object
     * @param elementConsumer the consumer
     */
    static void handleIfElement(Object o, Consumer<Element> elementConsumer) {
        if (o instanceof Element) {
            elementConsumer.accept((Element) o);
        }
    }

    /**
     * Loop through the collection and handle the element if it is an instance of {@link Element}
     *
     * @param collection      the collection
     * @param elementConsumer the consumer
     */
    static <T> void handleIfElement(Collection<T> collection, Consumer<Element> elementConsumer) {
        collection.forEach(o -> handleIfElement(o, elementConsumer));
    }

    /**
     * Initialize the element. Should be called before adding to the GUI.
     */
    default void init() {
    }

    /**
     * Stop the element. Should be called after removing from the GUI.
     */
    default void stop() {
    }
}
