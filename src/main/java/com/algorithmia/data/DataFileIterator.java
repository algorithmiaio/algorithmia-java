package com.algorithmia.data;

import com.algorithmia.APIException;
import java.util.ArrayList;
import java.util.List;

public class DataFileIterator extends AbstractDataIterator<DataFile> {
    public DataFileIterator(DataDirectory dir) {
        super(dir);
    }

    protected void loadNextPage() throws APIException {
        List<String> filenames = new ArrayList<String>();
        DataDirectory.DirectoryListResponse response = dir.getPage(marker, false);

        if (response.files != null) {
            for(DataDirectory.FileMetadata meta : response.files) {
                filenames.add(meta.filename);
            }
        }

        // Update iterator state
        setChildrenAndMarker(filenames, response.marker);
    }

    protected DataFile newDataObjectInstance(String dataUri) {
        return new DataFile(dir.client, dataUri);
    }
}
