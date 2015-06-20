package com.algorithmia.data;

import com.algorithmia.APIException;
import java.util.ArrayList;
import java.util.List;

public class DataFileIterator extends AbstractDataIterator<DataFile> {
    public DataFileIterator(DataDirectory dir) throws APIException {
        super(dir);
    }

    protected void loadNextPage() throws APIException {
        List<String> filenames = new ArrayList<String>();
        for(DataDirectory.FileMetadata meta : dir.getPage(marker).files) {
            filenames.add(meta.filename);
        }

        // Update iterator state
        children = filenames;
        this.offset = 0;
    }

    protected DataFile newDataObjectInstance(String dataUri) {
        return new DataFile(dir.client, dataUri);
    }
}
