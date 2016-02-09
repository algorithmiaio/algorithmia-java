package com.algorithmia.data;

import com.algorithmia.APIException;
import java.util.ArrayList;
import java.util.List;

public class DataDirectoryIterator extends AbstractDataIterator<DataDirectory> {
    public DataDirectoryIterator(DataDirectory dir) {
        super(dir);
    }

    protected void loadNextPage() throws APIException {
        List<String> dirnames = new ArrayList<String>();
        DataDirectory.DirectoryListResponse response = dir.getPage(marker);
        for(DataDirectory.DirectoryMetadata meta : response.folders) {
            dirnames.add(meta.name);
        }

        // Update iterator state
        setChildrenAndMarker(dirnames, response.marker);
    }

    protected DataDirectory newDataObjectInstance(String dataUri) {
        return new DataDirectory(dir.client, dataUri);
    }
}