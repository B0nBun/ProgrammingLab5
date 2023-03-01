package ru.ifmo.app.lib.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Class used to construct an iterator, from which a {@link Iterator#next next} value can be picked.
 * <i>
 * Peekable stores the next value in the private {@code nextElement} field, so if your iterator has
 * any side effects, you should be aware of the fact that the {@link Iterator#next} method is invoked
 * firstly  in the constructor and then for each subsequent {@link Peekable#next} call, the value from
 * the private field is returned and replaced with the one returned from the new {@link Iterator#next} call.
 * </i>
 */
public class Peekable<T> implements Iterator<T> {
    /**
     * An iterator values of which are peeked and returned in the {@link Peekable#next}
     */
    private Iterator<T> iterator;

    /**
     * A field that stores the value, which will be returned from this iterator next.
     */
    private Optional<T> nextElement;

    /**
     * Given the iterator, construct a peekable iterator from it.
     * 
     * @param iterator
     */
    public Peekable(Iterator<T> iterator) {
        this.iterator = iterator;
        this.nextElement = this.iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    /**
     * Converts iterable to iterator with {@link Iterable#iterator} and
     * uses the {@link Peekable(Iterator)} constructor to create a peekable.
     * 
     * @param iterable Iterable from which used iterator is created.
     */
    public Peekable(Iterable<T> iterable) {
        this(iterable.iterator());
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    public T next() throws NoSuchElementException {
        var result = this.nextElement.orElseGet(this.iterator::next);
        this.nextElement = iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
        return result;
    }
    
    /**
     * "Peek" a value, which will be returned next from this iterator.
     * 
     * @return A value, which will be returned from the next {@link Peekable#next} call.
     * @throws NoSuchElementException Thrown if the iterator ended and there is not values to "peek"
     */
    public T peek() throws NoSuchElementException {
        return nextElement.orElseGet(iterator::next);
    }
}
