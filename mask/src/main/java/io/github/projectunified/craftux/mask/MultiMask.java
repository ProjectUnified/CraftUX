package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Mask;

import java.util.*;

/**
 * A base class for masks that manage multiple child elements.
 * Provides functionality to add, retrieve, and manage lifecycle of child elements.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * MultiMask<Mask> multiMask = new MyMultiMask();
 * multiMask.add(new SingleMask(Position.of(0, 0), button1), new SingleMask(Position.of(1, 1), button2));
 * List<Mask> elements = multiMask.getElements();
 * }</pre>
 *
 * @param <T> the type of the child element
 */
public abstract class MultiMask<T> implements Element, Mask {
    protected final List<T> elements = new ArrayList<>();

    /**
     * Adds child elements to this mask.
     *
     * @param elements the child elements to add
     * @param <R>      the type of the child elements, must extend T
     */
    public <R extends T> void add(Collection<R> elements) {
        this.elements.addAll(elements);
    }

    /**
     * Adds multiple child elements to this mask.
     *
     * @param elements the child elements to add
     */
    @SafeVarargs
    public final void add(T... elements) {
        add(Arrays.asList(elements));
    }

    /**
     * Get the child elements
     *
     * @return the child elements
     */
    public final List<T> getElements() {
        return Collections.unmodifiableList(this.elements);
    }

    @Override
    public void init() {
        Element.handleIfElement(this.elements, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(this.elements, Element::stop);
    }
}
