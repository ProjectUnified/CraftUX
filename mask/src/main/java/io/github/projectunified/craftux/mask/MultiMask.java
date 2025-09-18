package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Mask;

import java.util.*;

/**
 * A base mask that handles multiple child elements
 *
 * @param <T> the type of the child element
 */
public abstract class MultiMask<T> implements Element, Mask {
    protected final List<T> elements = new ArrayList<>();

    /**
     * Add child elements
     *
     * @param elements the child elements
     * @param <R>      the type of the child elements
     */
    public <R extends T> void add(Collection<R> elements) {
        this.elements.addAll(elements);
    }

    /**
     * Add child elements
     *
     * @param elements the child elements
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
