package com.algorithmia.data;

import com.algorithmia.APIException;

import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.util.Iterator;
import java.util.List;


public abstract class AbstractDataIterator<T> implements Iterator<T> {
    protected DataDirectory dir;
    protected String marker;
    protected int offset = 0;
    protected List<String> children;

    protected AbstractDataIterator(DataDirectory dir) throws APIException {
        this.dir = dir;
        loadNextPage();
    }

    public boolean hasNext() {
        return (marker != null && children != null && offset >= children.size());
    }

    public T next() throws NoSuchElementException {
        if(children == null) {
            throw new NoSuchElementException();
        }

        if(marker != null && offset >= children.size()) {
            try {
                loadNextPage();
            } catch(APIException ex) {
                throw new NoSuchElementException(ex.getMessage());
            }
        }

        if(offset < children.size()) {
            offset++;
            return newDataObjectInstance(dir.path + "/" + children.get(offset-1));

        } else {
            throw new NoSuchElementException();
        }
    }


    /*
     * @throws UnsupportedOperationException as remove is not supported for DataFileIterators
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    abstract protected void loadNextPage() throws APIException;

    // Because this can be statically checked, as opposed to doing something like:
    //   storedClass.getDeclaredConstructor(String.class).newInstance(path)
    abstract protected T newDataObjectInstance(String dataUri);
}