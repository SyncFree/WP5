package com.syncfree.dbclient.data;


/**
 * Interface to add elements in a list in another object.
 * 
 * @author aas
 * @param <E> the type of data to be able to add to the list.
 */
public interface IList<E> {
    /**
     * Adds the new data into the list.
     *
     * @param data the data to add.
     * @return true if the operation was successful or false otherwise.
     */
    public boolean add(final E data);
} // end class IList
