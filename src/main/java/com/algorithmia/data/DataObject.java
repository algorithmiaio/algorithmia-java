package com.algorithmia.data;

import com.algorithmia.AlgorithmiaConf;
import com.algorithmia.APIException;
import com.algorithmia.client.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * An abstract object for common functionality of DataFile and DataDirectory
 */
abstract public class DataObject {

    public enum DataObjectType {
        FILE,
        DIRECTORY
    }

    public final String path;
    final String trimmedPath;
    protected final HttpClient client;
    protected final DataObjectType dataType;

    public DataObject(HttpClient client, String dataUrl, DataObjectType dataObjectType) {
        this.client = client;
        this.path = dataUrl.replaceAll("^data://|^/", "");
        this.trimmedPath = getTrimmedPath(this.path);
        this.dataType = dataObjectType;
    }

    public DataDirectory getParent() {
        return new DataDirectory(client, trimmedPath.replaceFirst("/[^/]+$", ""));
    }

    public String getName() {
        return trimmedPath.substring(trimmedPath.lastIndexOf("/") + 1);
    }

    public DataObjectType getType() {
        return this.dataType;
    }

    public boolean isFile() {
        return this.dataType == DataObjectType.FILE;
    }

    public boolean isDirectory() {
        return this.dataType == DataObjectType.DIRECTORY;
    }

    abstract public boolean exists() throws APIException;

    /**
     * Resolves this collection into an HTTP url
     * @return the HTTP url for this collection
     */
    public String getUrl() {
        try {
            return AlgorithmiaConf.apiAddress() + "/v1/data/" + URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "data://" + path;
    }

    // This is needed for directory support. We want to support directories that end with
    // a slash and those that don't.
    private static String getTrimmedPath(String path) {
        String result = path;
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
