package ru.ifmo.app.lib.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Peekable<T> implements Iterator<T> {
    private Iterator<T> iterator;
    private Optional<T> nextElement;

    public Peekable(Iterator<T> iterator) {
        this.iterator = iterator;
        this.nextElement = this.iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

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
    
    public T peek() throws NoSuchElementException {
        return nextElement.orElseGet(iterator::next);
    }
}
