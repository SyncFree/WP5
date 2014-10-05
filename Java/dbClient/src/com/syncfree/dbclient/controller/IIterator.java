package com.syncfree.dbclient.controller;

import java.util.Iterator;

/**
 * Wrapper for the iterator of a DB class.
 * <p>
 * It does not allow to remove any entry.
 * 
 * @author aas
 * @version 0.0
 * @param <E>
 *            the base data type.
 * @param <T>
 *            a class that extends E.
 */
public class IIterator<T extends E, E> implements Iterator<E> {
    private final Iterator<T> mIter;

    public IIterator(final Iterator<T> iter) {
        this.mIter = iter;
    } // Constructor ()

    @Override
    public boolean hasNext() {
        return this.mIter.hasNext();
    } // hasNext()

    @Override
    public E next() {
        return this.mIter.next();
    } // next()

    @Override
    public void remove() {
        // Does nothing
    } // remove()
} // end class IIterator
