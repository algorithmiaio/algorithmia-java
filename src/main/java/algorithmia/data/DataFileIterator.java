package algorithmia.data;

import algorithmia.APIException;

import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.util.Iterator;
import java.util.List;
/**
 * A
 */
public class DataFileIterator implements Iterator<DataFile> {

    private DataDirectory dir;
    private String marker;
    private int offset = 0;
    private List<DataDirectory.FileMetadata> files;

    public DataFileIterator(DataDirectory dir) throws APIException {
        this.dir = dir;
        loadNextPage();
    }

    public boolean hasNext() {
        return (marker != null || offset < files.size());
    }

    public DataFile next() throws NoSuchElementException {
        if(marker != null && offset >= files.size()) {
            try {
                loadNextPage();
            } catch(APIException ex) {
                throw new NoSuchElementException(ex.getMessage());
            }
        }

        if(offset < files.size()) {
            offset++;
            return new DataFile(dir.client, files.get(offset-1).filename);
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void loadNextPage() throws APIException {
        this.files = dir.getPage(marker).files;
        this.offset = 0;
    }

}
