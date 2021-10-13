package com.algorithmia.algo;

import com.algorithmia.client.Auth;
import com.algorithmia.client.HttpClient;
import com.algorithmia.data.DataDirectory;
import com.algorithmia.data.DataFile;
import com.google.gson.Gson;
import org.apache.commons.io.IOExceptionList;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instantiate Algorithmia clients for calling algorithms and accessing data
 */
public final class AlgorithmiaClient {
    private HttpClient client;

    /**
     * Instantiate Algorithmia client with the given auth and max number of connections
     *
     * @param auth           Algorithmia Auth object, a null auth object is valid, though only
     *                       correct for within the Algorithmia platform
     * @param maxConnections max number of concurrent connections to hold open to Algorithmia
     */
    protected AlgorithmiaClient(Auth auth, String apiAddress, int maxConnections) {
        this.client = new HttpClient(auth, apiAddress, maxConnections, null);
    }

    protected AlgorithmiaClient(Auth auth, String apiAddress, int maxConnections, String pemPath) {
        this.client = new HttpClient(auth, apiAddress, maxConnections, pemPath);
    }

    //For testing
    public AlgorithmiaClient(HttpClient client) {
        this.client = client;
    }

    /**
     * Initialize an Algorithm object from this client
     *
     * @param algoUri the algorithm's URI, e.g., algo://user/algoname
     * @return an Algorithm client for the specified algorithm
     */
    public AlgorithmExecutable algo(String algoUri) {
        return new AlgorithmExecutable(client, new AlgorithmRef(algoUri));
    }

    /**
     * Get an Algorithm object from this client
     *
     * @param userName the users algorithmia user name
     * @param algoName the name of the algorithm
     * @return an Algorithm object for the specified algorithm
     */
    public Algorithm getAlgo(String userName, String algoName) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName;
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Algorithm.class);
    }

    /**
     * Get am Algorithm SCM object from this client
     *
     * @param scmId id of the scm to retrieve
     * @return an Algorithm SCM object
     */
    public Algorithm.SCM getSCM(String scmId) throws IOException {
        String path = "/v1/scms/" + scmId;
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Algorithm.SCM.class);
    }

    /**
     * List Algorithm SCMs from this client
     *
     * @return an Algorithm SCM object
     */
    public AlgorithmSCMsList listSCMs() throws IOException {
        String path = "/v1/scms";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, AlgorithmSCMsList.class);
    }

    /**
     * Query an Algorithm SCM status from this client
     *
     * @param scmId id of the scm to retrieve
     * @return an Algorithm SCM authorization object
     */
    public AlgorithmSCMAuthorizationStatus querySCMStatus(String scmId) throws IOException {
        String path = "/v1/scms/" + scmId + "/oauth/status";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, AlgorithmSCMAuthorizationStatus.class);
    }

    /**
     * Revoke an Algorithm SCM status from this client
     * @param scmId id of the scm to retrieve
     * @return an Algorithm SCM authorization object
     */
    /*public HttpResponse revokeSCMStatus(String scmId) throws IOException {
        String path = "/v1/scms/" + scmId + "/oauth/revoke";
        return this.client.post(path);
    }*/

    /**
     * Get an Algorithm SCM status for from this client
     *
     * @param userName the users Algorithmia user name
     * @param algoName the name of the algorithm
     * @return an Algorithm SCM object
     */
    public AlgorithmSCMStatus getAlgoSCMStatus(String userName, String algoName) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName + "/scm/status";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, AlgorithmSCMStatus.class);
    }

    /**
     * List algorithm versions from this client
     *
     * @param userName  the users Algorithmia user name
     * @param algoName  the name of the algorithm
     * @param callable  whether to return only public or private algorithm versions
     * @param limit     items per page
     * @param published whether to return only versions that have been published
     * @param marker    used for pagination
     * @return an AlgorithmVersionsList object for the specified algorithm
     */
    public AlgorithmVersionsList listAlgoVersions(String userName, String algoName, Boolean callable, Integer limit,
                                                  Boolean published, String marker) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName + "/versions";
        Map<String, String> params = new HashMap<String, String>();
        if (callable != null) {
            params.put("callable", callable.toString());
        }
        if (limit != null) {
            params.put("limit", limit.toString());
        }
        if (published != null) {
            params.put("published", published.toString());
        }
        if (marker != null) {
            params.put("marker", marker);
        }

        return this.client.get(path, new TypeToken<AlgorithmVersionsList>() {
        }, params);
    }

    /**
     * Get an Algorithm Build object from this client
     *
     * @param userName the users Algorithmia user name
     * @param algoName the name of the algorithm
     * @param buildId  id of the build to retrieve
     * @return a Algorithm Build object for the specified algorithm
     */
    public Algorithm.Build getAlgoBuild(String userName, String algoName, String buildId) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName + "/builds/" + buildId;
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Algorithm.Build.class);
    }

    /**
     * List algorithm builds from this client
     *
     * @param userName the users algorithmia user name
     * @param algoName the name of the algorithm
     * @param limit    items per page
     * @param marker   used for pagination
     * @return an AlgorithmBuildsList object for the specified algorithm
     */
    public AlgorithmBuildsList listAlgoBuilds(String userName, String algoName,
                                              Integer limit, String marker) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName + "/builds";
        Map<String, String> params = new HashMap<String, String>();
        if (limit != null) {
            params.put("limit", limit.toString());
        }
        if (marker != null) {
            params.put("marker", marker);
        }

        return this.client.get(path, new TypeToken<AlgorithmBuildsList>() {
        }, params);
    }

    /**
     * Get build logs for an Algorithm object from this client
     *
     * @param userName the users Algorithmia user name
     * @param algoName the name of the algorithm
     * @param buildId  id of the build to retrieve logs
     * @return a BuildLogs object for the specified algorithm
     */
    public BuildLogs getAlgoBuildLogs(String userName, String algoName, String buildId) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName + "/builds/" + buildId + "/logs";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, BuildLogs.class);
    }

    public ErrorLogs[] getUserErrors(String userName) throws IOException {
        String path = "/v1/users/"+ userName +"/errors";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        ErrorLogs[] logs = new ErrorLogs[0];
        try{
            if(response.getStatusLine().getStatusCode() != 200){ throw new AlgorithmException(response.getStatusLine().toString());}
            logs = gson.fromJson(responseString,ErrorLogs[].class);
        }catch (AlgorithmException e){
            System.out.println(e);
        }
        return logs;
    }

    public ErrorLogs[] getAlgorithmErrors(String algoName) throws IOException {
        String path = "/v1/algorithms/"+algoName+"/errors";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        ErrorLogs[] logs = new ErrorLogs[0];
        try{
            if(response.getStatusLine().getStatusCode() != 200){ throw new AlgorithmException(response.getStatusLine().toString());}
            logs = gson.fromJson(responseString,ErrorLogs[].class);
        }catch (AlgorithmException e){
            System.out.println(e);
        }
        return logs;
    }

    public ErrorLogs[] getOrganizationErrors(String orgName) throws IOException {
        String path = "/v1/organizations/"+orgName+"/errors";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        ErrorLogs[] logs = new ErrorLogs[0];
        try{
            if(response.getStatusLine().getStatusCode() != 200){ throw new AlgorithmException(response.getStatusLine().toString());}
            logs = gson.fromJson(responseString,ErrorLogs[].class);
        }catch (AlgorithmException e){
            System.out.println(e);
        }
        return logs;
    }

    /**
     * Create a new Algorithm object from this client
     *
     * @param userName      the users algorithmia user name
     * @param requestString json payload
     * @return an Algorithm object for the specified algorithm
     */
    public Algorithm createAlgo(String userName, String requestString) throws IOException {
        String path = "/v1/algorithms/" + userName;
        HttpResponse response = this.client.post(path, new StringEntity(requestString, ContentType.APPLICATION_JSON));
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Algorithm.class);
    }

    /**
     * Compile an Algorithm from this client
     *
     * @param userName the users algorithmia user name
     * @param algoName the name of the algorithm
     * @return an Algorithm object for the specified algorithm
     */
    public Algorithm compileAlgo(String userName, String algoName) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName + "/compile";
        HttpResponse response = this.client.post(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Algorithm.class);
    }

    /**
     * Update an Algorithm object from this client
     *
     * @param userName      the users algorithmia user name
     * @param algoName      the name of the algorithm
     * @param requestString json payload
     * @return an Algorithm object for the specified algorithm
     */
    public Algorithm updateAlgo(String userName, String algoName, String requestString) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName;
        HttpResponse response = this.client.put(path, new StringEntity(requestString, ContentType.APPLICATION_JSON));
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Algorithm.class);
    }

    /**
     * Publish an Algorithm from this client
     *
     * @param userName      the users Algorithmia user name
     * @param algoName      the name of the algorithm
     * @param requestString json payload
     * @return an Algorithm object for the specified algorithm
     */
    public Algorithm publishAlgo(String userName, String algoName, String requestString) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName + "/versions";
        HttpResponse response = this.client.post(path, new StringEntity(requestString, ContentType.APPLICATION_JSON));
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Algorithm.class);
    }

    /**
     * Delete an Algorithm from this client
     *
     * @param userName the users algorithmia user name
     * @param algoName the name of the algorithm
     * @return an empty response
     */
    public HttpResponse deleteAlgo(String userName, String algoName) throws IOException {
        String path = "/v1/algorithms/" + userName + "/" + algoName;
        return this.client.delete(path);
    }

    /**
     * Create a user from this client
     *
     * @param requestString json payload
     * @return a user object
     */
    public User createUser(String requestString) throws IOException {
        String path = "/v1/users";
        HttpResponse response = this.client.post(path, new StringEntity(requestString, ContentType.APPLICATION_JSON));
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, User.class);
    }

    /**
     * Create an organization from this client
     *
     * @param requestString json payload
     * @return an organization object
     */
    public Organization createOrganization(String requestString) throws IOException {
        String path = "/v1/organizations";
        Gson gson = new Gson();
        HttpResponse response = this.client.post(path, new StringEntity(organizationTypeIdChanger(gson.fromJson
                (requestString, Organization.class)), ContentType.APPLICATION_JSON));
        String responseString = EntityUtils.toString(response.getEntity());
        return gson.fromJson(responseString, Organization.class);
    }

    public Environment[] getEnvironment(String language) throws IOException {
        String path = "/v1/algorithm-environments/edge/languages/"+language+"/environments";
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        JSONObject jsonObject = new JSONObject(responseString);
        JSONArray jsonArray = jsonObject.getJSONArray("environments");
        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(jsonArray), Environment[].class);
    }

    /**
     * Get an organization from this client
     *
     * @param orgName the organization name
     * @return an organization object
     */
    public Organization getOrganization(String orgName) throws IOException {
        String path = "/v1/organizations/" + orgName;
        HttpResponse response = this.client.get(path);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        return gson.fromJson(responseString, Organization.class);
    }

    /**
     * Edit an organization from this client
     *
     * @param orgName       the organization name
     * @param requestString json payload
     * @return an empty response
     */
    public HttpResponse editOrganization(String orgName, String requestString) throws IOException {
        String path = "/v1/organizations/" + orgName;
        Gson gson = new Gson();
        return this.client.put(path, new StringEntity(organizationTypeIdChanger(gson.fromJson
                (requestString, Organization.class)), ContentType.APPLICATION_JSON));
    }

    /**
     * Helper for swapping out the type_id value
     */
    private String organizationTypeIdChanger(Organization editedOrganization) throws IOException {
        Boolean isSet = false;
        Gson gson = new Gson();
        HttpResponse typesResponse = getOrgTypes();
        String typesResponseString = EntityUtils.toString(typesResponse.getEntity());
        List<Map<String, String>> typesMapList = gson.fromJson(typesResponseString, new TypeToken<List<Map<String, String>>>().getType());
        for (Map<String, String> type : typesMapList) {
            if (type.get("name").equals(editedOrganization.getTypeId())) {
                editedOrganization.setTypeId(type.get("id"));
                isSet = true;
                break;
            }
        }
        if (!isSet) {
            throw new IllegalArgumentException("No matching value found");
        }
        return gson.toJson(editedOrganization);
    }

    /**
     * Get types uuid endpoint
     */
    public HttpResponse getOrgTypes() throws IOException {
        String path = "/v1/organization/types";
        return this.client.get(path);
    }

    /**
     * Create a member to an organization
     *
     * @param orgName  the organization name
     * @param userName the users algorithmia user name
     * @return an organization object
     */
    public HttpResponse addOrganizationMember(String orgName, String userName) throws IOException {
        String path = "/v1/organizations/" + orgName + "/members/" + userName;
        return this.client.put(path);
    }

    /**
     * Initialize a DataDirectory object from this client
     *
     * @param path to a data directory, e.g., data://.my/foo
     * @return a DataDirectory client for the specified directory
     */
    public DataDirectory dir(String path) {
        return new DataDirectory(client, path);
    }

    /**
     * Initialize an DataFile object from this client
     *
     * @param path to a data file, e.g., data://.my/foo/bar.txt
     * @return a DataFile client for the specified file
     */
    public DataFile file(String path) {
        return new DataFile(client, path);
    }

    public AlgorithmiaInsights reportInsights(String input) throws IOException {
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(input, new TypeToken<Map<String, String>>().getType());
        List<String> insightPayload = new ArrayList<>();
        map.forEach((key, value) -> {
            String item = String.format("{\"insight_key\": \"%s\", \"insight_value\": \"%s\"}", key, value);
            insightPayload.add(item);
        });
        HttpResponse response = this.client.post("/v1/insights", new StringEntity(insightPayload.toString(), ContentType.APPLICATION_JSON));
        String responseString = EntityUtils.toString(response.getEntity());
        return gson.fromJson(responseString, AlgorithmiaInsights.class);
    }

    public void close() throws IOException {
        this.client.close();
    }
}
