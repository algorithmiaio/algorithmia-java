package com.algorithmia.data;

import com.algorithmia.APIException;
import com.algorithmia.client.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;

/**
 * A data stream
 */
public class DataDirectory extends DataObject {

    public DataDirectory(HttpClient client, String dataUrl) {
        super(client, dataUrl);
    }

    /**
     * Determine if this Algorithmia data directory exists
     * @return true iff the directory exists
     * @throws APIException if there were any problems communicating with the Algorithmia API
     */
    public boolean exists() throws APIException {
        HttpResponse response = this.client.get(getUrl());
        int status = response.getStatusLine().getStatusCode();
        if(status != 200 && status != 404) {
            throw APIException.fromHttpResponse(response);
        }
        return (200 == status);
    }

    /**
     * Get an iterator for retrieving files in this DataDirectory
     * The file list retrieval is done in batches, generally ~1000 files at a time
     * @return the list of files
     * @throws APIException if there were any problems communicating with the Algorithmia API
     */
    public DataFileIterator getFileIter() throws APIException {
        return new DataFileIterator(this);
    }

    public DataDirectoryIterator getDirIter() throws APIException{
        return new DataDirectoryIterator(this);
    }

    /**
     * Queries the DataAPI for a particular data file
     * @param filename the name of the file within this collection to get a reference for
     * @return a handle to the requested file
     */
    public DataFile file(String filename) {
        return new DataFile(client, path + "/" + filename);
    }

    /**
     * Convenience wrapper for putting a File
     * @param file a file to put into this data directory
     * @return a handle to the requested file
     * @throws APIException if there were any problems communicating with the Algorithmia API
     * @throws FileNotFoundException if the specified file does not exist
     */
    public DataFile putFile(File file) throws APIException, FileNotFoundException {
        DataFile dataFile = new DataFile(client, path + "/" + file.getName());
        dataFile.put(file);
        return dataFile;
    }

    private class CreateDirectoryRequest {
        @SuppressWarnings("unused")//Used indirectly by GSON
        private String name;
        CreateDirectoryRequest(String name) {
            this.name = name;
        }
    }

    /**
     * Creates this directory via the Algorithmia Data API
     * @throws APIException if there were any problems communicating with the Algorithmia API
     */
    public void create() throws APIException {
        CreateDirectoryRequest reqObj = new CreateDirectoryRequest(this.getName());
        Gson gson = new Gson();
        JsonElement inputJson = gson.toJsonTree(reqObj);

        String url = this.getParent().getUrl();
        HttpResponse response = this.client.post(url, new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON));
        HttpClientHelpers.throwIfNotOk(response);
    }

    /**
     * Creates this directory vi the Algorithmia Data API
     * @param forceDelete forces deletion of the directory even if it still has other files in it
     * @throws APIException if there were any problems communicating with the Algorithmia API
     */
    public void delete(boolean forceDelete) throws APIException {
        HttpResponse response = client.delete(getUrl() + "?force=" + forceDelete);
        HttpClientHelpers.throwIfNotOk(response);
    }

    protected class FileMetadata {
        protected String filename;

        protected FileMetadata(String filename) {
            this.filename = filename;
        }
    }

    protected class DirectoryMetadata {
        protected String name;
        protected DirectoryMetadata(String name) {
            this.name = name;
        }
    }

    protected class DirectoryListResponse {
        protected List<FileMetadata> files;
        protected List<DirectoryMetadata> folders;
        protected DirectoryListResponse(List<FileMetadata> files, List<DirectoryMetadata> folders) {
            this.files = files;
            this.folders = folders;
        }
    }

    /**
     * Gets a single page of the directory listing. Subsquent pages are fetched with the returned marker value.
     * @param marker indicates the specific page to fetch; first page is fetched if null
     * @return a page of files and directories that exist within this directory
     * @throws APIException if there were any problems communicating with the Algorithmia API
     */
    protected DirectoryListResponse getPage(String marker) throws APIException {
        String url = (marker == null) ? getUrl() : getUrl() + "?marker=" + marker;
        return client.get(url, new TypeToken<DirectoryListResponse>(){});
    }
}
