package com.algorithmia.data;

import com.algorithmia.APIException;
import java.util.ArrayList;
import java.util.List;

public class DataDirectoryIterator extends AbstractDataIterator<DataDirectory> {
    public DataDirectoryIterator(DataDirectory dir) throws APIException {
        super(dir);
    }

    protected void loadNextPage() throws APIException {
        List<String> dirnames = new ArrayList<String>();
        for(DataDirectory.DirectoryMetadata meta : dir.getPage(marker).folders) {
            dirnames.add(meta.name);
        }

        // Update iterator state
        children = dirnames;
        this.offset = 0;
    }

    protected DataDirectory newDataObjectInstance(String dataUri) {
        return new DataDirectory(dir.client, dataUri);
    }
}