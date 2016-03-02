package com.algorithmia.data;

import com.algorithmia.APIException;
import com.algorithmia.client.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;

/**
 * A data stream
 */
public class DataDirectory extends DataObject {

    public DataDirectory(HttpClient client, String dataUrl) {
        super(client, dataUrl, DataObjectType.DIRECTORY);
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
    @Deprecated
    public DataFileIterator getFileIter() throws APIException {
        return new DataFileIterator(this);
    }
    public Iterable<DataFile> files() throws APIException {
        return new Iterable<DataFile>() {
            public DataFileIterator iterator() {
                return new DataFileIterator(DataDirectory.this);
            }
        };
    }

    @Deprecated
    public DataDirectoryIterator getDirIter() throws APIException {
        return new DataDirectoryIterator(this);
    }
    public Iterable<DataDirectory> dirs() throws APIException {
        return new Iterable<DataDirectory>() {
            public DataDirectoryIterator iterator() {
                return new DataDirectoryIterator(DataDirectory.this);
            }
        };
    }

    /**
     * Queries the DataAPI for a particular data file
     * @param filename the name of the file within this collection to get a reference for
     * @return a handle to the requested file
     */
    public DataFile file(String filename) {
        return new DataFile(client, trimmedPath + "/" + filename);
    }

    /**
     * Convenience wrapper for putting a File
     * @param file a file to put into this data directory
     * @return a handle to the requested file
     * @throws APIException if there were any problems communicating with the Algorithmia API
     * @throws FileNotFoundException if the specified file does not exist
     */
    public DataFile putFile(File file) throws APIException, FileNotFoundException {
        DataFile dataFile = new DataFile(client, trimmedPath + "/" + file.getName());
        dataFile.put(file);
        return dataFile;
    }

    private class CreateDirectoryRequest {
        @SuppressWarnings("unused")//Used indirectly by GSON
        private String name;
        private DataAcl acl;
        CreateDirectoryRequest(String name, DataAcl acl) {
            this.name = name;
            this.acl = acl;
        }
    }

    private class UpdateDirectoryRequest {
        @SuppressWarnings("unused")//Used indirectly by GSON
        private DataAcl acl;
        UpdateDirectoryRequest(DataAcl acl) {
            this.acl = acl;
        }
    }

    /**
     * Creates this directory via the Algorithmia Data API
     * @throws APIException if there were any problems communicating with the Algorithmia API
     */
    public void create() throws APIException {
        CreateDirectoryRequest reqObj = new CreateDirectoryRequest(this.getName(), null);
        Gson gson = new Gson();
        JsonElement inputJson = gson.toJsonTree(reqObj);

        String url = this.getParent().getUrl();
        HttpResponse response = this.client.post(url, new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON));
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void create(DataAcl dataAcl) throws APIException {
        CreateDirectoryRequest reqObj = new CreateDirectoryRequest(this.getName(), dataAcl);
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

    public DataAcl getPermissions() throws APIException {
        DirectoryListResponse response = getPage(null, true);
        return DataAcl.fromAclResponse(response.acl);
    }

    public boolean updatePermissions(DataAcl dataAcl) throws APIException {
        UpdateDirectoryRequest request = new UpdateDirectoryRequest(dataAcl);
        Gson gson = new Gson();
        JsonElement inputJson = gson.toJsonTree(request);

        StringEntity entity = new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON);
        HttpResponse response = client.patch(getUrl(), entity);

        HttpClientHelpers.throwIfNotOk(response);
        return true;
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
        protected String marker;
        protected Map<String, List<String>> acl;
        protected DirectoryListResponse(List<FileMetadata> files,
                                        List<DirectoryMetadata> folders,
                                        String marker,
                                        Map<String, List<String>> acl) {
            this.files = files;
            this.folders = folders;
            this.marker = marker;
            this.acl = acl;
        }
    }

    /**
     * Gets a single page of the directory listing. Subsquent pages are fetched with the returned marker value.
     * @param marker indicates the specific page to fetch; first page is fetched if null
     * @return a page of files and directories that exist within this directory
     * @throws APIException if there were any problems communicating with the Algorithmia API
     */
    protected DirectoryListResponse getPage(String marker, Boolean getAcl) throws APIException {
        String url = getUrl();

        Map<String, String> params = new HashMap<String, String>();
        if (marker != null) {
            params.put("marker", marker);
        }
        if (getAcl) {
            params.put("acl", getAcl.toString());
        }

        return client.get(url, new TypeToken<DirectoryListResponse>(){}, params);
    }
}
