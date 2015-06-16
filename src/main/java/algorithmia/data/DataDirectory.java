package algorithmia.data;

import algorithmia.AlgorithmiaConf;
import algorithmia.APIException;
import algorithmia.client.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.JsonElement;

import algorithmia.client.HttpClientHelpers.JsonAsyncCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;

/**
 * A data stream
 */
public class DataDirectory extends DataObject {

    public DataDirectory(HttpClient client, String dataUrl) {
        super(client, dataUrl);
    }

    public boolean exists() throws APIException {
        HttpResponse response = this.client.get(url());
        int status = response.getStatusLine().getStatusCode();
        if(status != 200 && status != 404) {
            throw APIException.fromHttpResponse(response, null);
        }
        return (200 == status);
    }

    /**
     * Get an iterator for retrieving files in this DataDirectory
     * The file list retrieval is done in batches, generally ~1000 files at a time
     * @return the list of files
     * @throws APIException if there were any problems communicating with the DataAPI
     */
    public DataFileIterator getFileIter() throws APIException {
        return new DataFileIterator(this);
    }

    /**
     * Queries the DataAPI for a particular data file
     * @param filename the name of the file within this collection to get a reference for
     * @return a handle to the requested file
     */
    public DataFile file(String filename) throws APIException {
        return new DataFile(client, path + "/" + filename);
    }

    /**
     * Convenience wrapper for putting a File using the file's filename
     * @param filename the name of the file within this collection to get a reference for
     * @return a handle to the requested file
     */
    public DataFile putFile(File file) throws APIException, FileNotFoundException {
        DataFile dataFile = new DataFile(client, path + "/" + file.getName());
        dataFile.put(file);
        return dataFile;
    }

    private class CreateDirectoryRequest {
        private String name;
        CreateDirectoryRequest(String name) {
            this.name = name;
        }
    }

    public void create() throws APIException {
        CreateDirectoryRequest reqObj = new CreateDirectoryRequest(this.getName());
        Gson gson = new Gson();
        JsonElement inputJson = gson.toJsonTree(reqObj);

        String url = this.getParent().url();
        HttpResponse response = this.client.post(url, new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON));
        HttpClientHelpers.assertStatusSuccess(response);
    }

    public void delete(boolean forceDelete) throws APIException {
        HttpResponse response = this.client.delete(this.url() + "?force=" + forceDelete);
        HttpClientHelpers.assertStatusSuccess(response);
    }

    public class FileMetadata {
        public String filename;

        FileMetadata(String filename) {
            this.filename = filename;
        }
    }

    public class DirectoryListResponse {
        public List<FileMetadata> files;

        DirectoryListResponse(List<FileMetadata> files) { this.files = files; }
    }

    protected DirectoryListResponse getPage(String marker) throws APIException {
        String url = (marker == null) ? url() : url() + "?marker=" + marker;
        HttpResponse response = client.get(url);
        JsonElement json = HttpClientHelpers.parseResponseJson(response);

        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<DirectoryListResponse>(){}.getType());
    }
}
