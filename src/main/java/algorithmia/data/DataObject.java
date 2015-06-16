package algorithmia.data;

import algorithmia.AlgorithmiaConf;
import algorithmia.APIException;
import algorithmia.client.*;



/**
 * An abstract object for common functionality of DataFile and DataDirectory
 */
abstract public class DataObject {

    public final String path;
    protected final HttpClient client;

    public DataObject(HttpClient client, String dataUrl) {
        this.client = client;
        this.path = dataUrl.replaceAll("^data://|^/", "");
    }

    public DataDirectory getParent() {
        return new DataDirectory(client, this.path.replaceFirst("/[^/]+$", ""));
    }

    public String getName() {
        return this.path.substring(this.path.lastIndexOf("/") + 1);
    }

    abstract public boolean exists() throws APIException;

    /**
     * Resolves this collection into an HTTP url
     * @return the HTTP url for this collection
     */
    public String url() {
        return AlgorithmiaConf.apiAddress() + "/v1/data/" + path;
    }

    @Override
    public String toString() {
        return "data://" + path;
    }
}
