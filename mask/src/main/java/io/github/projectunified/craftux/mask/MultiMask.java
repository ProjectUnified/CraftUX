package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * A base mask that handles multiple child elements
 *
 * @param <T> the type of the return value of the child element
 */
public abstract class MultiMask<T> implements Element, Function<@NotNull UUID, @Nullable Map<Position, ActionItem>> {
    protected final List<Function<@NotNull UUID, @Nullable T>> elements = new ArrayList<>();

    /**
     * Whether this mask requires child elements
     *
     * @return true if it requires child elements
     */
    protected boolean requireChildElements() {
        return false;
    }

    /**
     * Add child elements
     *
     * @param elements the child elements
     * @param <R>      the type of the child elements
     */
    public <R extends Function<@NotNull UUID, @Nullable T>> void add(Collection<R> elements) {
        this.elements.addAll(elements);
    }

    /**
     * Add child elements
     *
     * @param elements the child elements
     */
    @SafeVarargs
    public final void add(Function<@NotNull UUID, @Nullable T>... elements) {
        add(Arrays.asList(elements));
    }

    /**
     * Get the child elements
     *
     * @return the child elements
     */
    public final List<Function<@NotNull UUID, @Nullable T>> getElements() {
        return Collections.unmodifiableList(this.elements);
    }

    @Override
    public void init() {
        if (requireChildElements() && this.elements.isEmpty()) {
            throw new IllegalArgumentException("There is no child element for this mask");
        }
        Element.handleIfElement(this.elements, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(this.elements, Element::stop);
    }
}
