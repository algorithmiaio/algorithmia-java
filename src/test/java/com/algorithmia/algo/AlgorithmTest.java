package com.algorithmia.algo;

import com.algorithmia.client.HttpClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

public class AlgorithmTest {

    private String defaultKey;
    private String adminKey;
    private String testAddress;
    private String testDefaultKey;
    @Mock
    private HttpClient httpClient;

    @Before
    public void setup() {
        defaultKey = System.getenv("ALGORITHMIA_DEFAULT_API_KEY");
        adminKey =  System.getenv("ALGORITHMIA_ADMIN_API_KEY");
        testAddress = System.getenv("ALGORITHMIA_TEST_ADDRESS");
        testDefaultKey = System.getenv("ALGORITHMIA_TEST_DEFAULT_KEY");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void algorithmPipeJson() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey).algo("docs/JavaAddOne").pipe(41);
        Assert.assertEquals("42", res.as(new TypeToken<JsonElement>(){}).toString());
        int result = res.as(new TypeToken<Integer>(){});
        Assert.assertEquals(42, result);
        Assert.assertEquals(ContentType.Json, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmCAPipeText() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey,null,1,"C:\\Users\\john.bragg\\algorithmia-java\\testCert.pem").algo("demo/Hello").pipe("foo");
        Assert.assertEquals("\"Hello foo\"", res.as(new TypeToken<JsonElement>(){}).toString());
        Assert.assertEquals("\"Hello foo\"", res.asJsonString());
        Assert.assertEquals("Hello foo", res.as(new TypeToken<String>(){}));
        Assert.assertEquals("Hello foo", res.asString());
        Assert.assertEquals(ContentType.Text, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmPipeText() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey).algo("demo/Hello").pipe("foo");
        Assert.assertEquals("\"Hello foo\"", res.as(new TypeToken<JsonElement>(){}).toString());
        Assert.assertEquals("\"Hello foo\"", res.asJsonString());
        Assert.assertEquals("Hello foo", res.as(new TypeToken<String>(){}));
        Assert.assertEquals("Hello foo", res.asString());
        Assert.assertEquals(ContentType.Text, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmPipeBinary() throws Exception {
        byte[] input = new byte[10];
        AlgoResponse res = Algorithmia.client(defaultKey).algo("docs/JavaBinaryInAndOut").pipe(input);
        byte[] output = res.as(new TypeToken<byte[]>(){});
        Assert.assertEquals(Base64.encodeBase64String(input),Base64.encodeBase64String(output));
        Assert.assertEquals(ContentType.Binary, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmRawOutput() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey).algo("demo/Hello")
                .setOutputType(AlgorithmExecutable.AlgorithmOutputType.RAW).pipe("foo");
        Assert.assertEquals("Hello foo", res.getRawOutput());
        Assert.assertEquals(null, res.getMetadata());
    }

    @Test
    public void algorithmVoidOutput() throws Exception {
        AlgoAsyncResponse res = Algorithmia.client(defaultKey).algo("demo/Hello")
                .setOutputType(AlgorithmExecutable.AlgorithmOutputType.VOID).pipe("foo")
                .getAsyncResponse();
        Assert.assertEquals("void", res.getAsyncProtocol());
        Assert.assertTrue(res.getRequestId() != null);  // request is unpredictable, but should be *something*
    }

    @Test
    public void algorithmSetOption() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey).algo("demo/Hello")
                .setOption("output", "raw").pipe("foo");

        Assert.assertEquals("Hello foo", res.getRawOutput());
    }

    @Test
    public void algorithmSetOptions() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("output", "raw");

        AlgoResponse res = Algorithmia.client(defaultKey).algo("demo/Hello")
                .setOptions(options).pipe("foo");

        Assert.assertEquals("Hello foo", res.getRawOutput());
    }

    @Test
    public void algorithmCheckTimeout() throws Exception {
        AlgorithmExecutable algo = Algorithmia.client(defaultKey).algo("docs/JavaAddOne");

        // Check default timeout - just for fun. This doesn't have to be specified at all time
        // but I wanted to make sure this method never throws an exception when the key in the options
        // is null.
        Assert.assertEquals((long)300, (long)algo.getTimeout());

        // Check Minute conversion
        algo = algo.setTimeout(20L, TimeUnit.MINUTES);
        Assert.assertEquals((long)20 * 60, (long)algo.getTimeout());

        // And seconds just in case
        algo = algo.setTimeout(30L, TimeUnit.SECONDS);
        Assert.assertEquals((long)30, (long)algo.getTimeout());

        // And milliseconds
        algo = algo.setTimeout(5000L, TimeUnit.MILLISECONDS);
        Assert.assertEquals((long)5, (long)algo.getTimeout());
    }

    @Test
    public void algoGetAlgo() throws Exception {
        Algorithm algorithm = Algorithmia.client(defaultKey).getAlgo("dherring", "ResultFile");
        Assert.assertEquals(algorithm.getName(), "ResultFile");
    }

    @Test
    public void algoCreateAlgo() throws Exception {
        Algorithm testAlgo = createTestAlgo();
        Gson gson = new Gson();
        String json = gson.toJson(testAlgo);
        Algorithm newAlgorithm = Algorithmia.client(defaultKey).createAlgo("dherring", json);
        Assert.assertEquals(testAlgo.getName(), newAlgorithm.getName());
    }

    @Test
    public void algoCompileAlgo() throws Exception {
        Algorithm algorithm = Algorithmia.client(defaultKey).compileAlgo("dherring", "ResultFile");
        Assert.assertEquals(algorithm.getName(), "ResultFile");
    }

    @Test
    public void algoPublishAlgo() throws Exception {
        Algorithm.VersionInfo versionInfo = new Algorithm.VersionInfo(
                "git_hash",
                "release_notes",
                "sample_input",
                "sample_output",
                "patch");
        Algorithm algorithm = Algorithm.builder().versionInfo(versionInfo).buildDTO();
        Gson gson = new Gson();
        String json = gson.toJson(algorithm);
        //Must call compile in order to increase version of already published algorithm
        Algorithmia.client(defaultKey).compileAlgo("dherring", "ResultFile");
        Algorithm newAlgorithm = Algorithmia.client(defaultKey).publishAlgo("dherring", "ResultFile", json);
        Assert.assertNotNull(newAlgorithm.getVersionInfo().getSemanticVersion());
    }

    @Test
    public void algoGetAlgoSCM() throws Exception {
        Algorithm.SCM scm = Algorithmia.client(defaultKey).getSCM("internal");
        Assert.assertEquals(scm.getEnabled(), true);
    }

    @Test
    public void algoListAlgoSCMs() throws Exception {
        AlgorithmSCMsList algorithmSCMsList = Algorithmia.client(defaultKey).listSCMs();
        Assert.assertFalse(algorithmSCMsList.getResults().isEmpty());
    }

    @Test
    public void algoQuerySCMStatus() throws Exception {
        AlgorithmSCMAuthorizationStatus algorithmSCMAuthorizationStatus = Algorithmia.client(defaultKey).querySCMStatus("github");
        Assert.assertEquals("authorized", algorithmSCMAuthorizationStatus.getAuthorizationStatus());
    }

    /*@Test
    public void algoRevokeSCMStatus() throws Exception {
        HttpResponse response = Algorithmia.client(testDefaultKey, testAddress).revokeSCMStatus("fa359f8a-5a37-4726-9174-1475b41939ef");
        Assert.assertEquals(204, response.getStatusLine().getStatusCode());
    }*/

    @Test
    public void algoGetAlgoSCMStatus() throws Exception {
        AlgorithmSCMStatus scmStatus = Algorithmia.client(defaultKey).getAlgoSCMStatus("dherring", "ResultFile");
        Assert.assertEquals("active", scmStatus.getScmConnectionStatus());
    }

    @Test
    public void algoListAlgoVersions() throws Exception {
        AlgorithmVersionsList algoList = Algorithmia.client(defaultKey).listAlgoVersions(
                "dherring",
                "ResultFile",
                null,
                null,
                null,
                null);
        Assert.assertEquals(10, algoList.getResults().size());
    }

    @Test
    public void algoGetAlgoBuild() throws Exception {
        Algorithm.Build expectedBuild = new Algorithm.Build();
        expectedBuild.setBuildId("579ff0a8-6b1f-4cf4-83a5-c7cb6999ae24");
        Algorithm.Build returnedBuild = Algorithmia.client(defaultKey).getAlgoBuild(
                "dherring",
                "ResultFile",
                "579ff0a8-6b1f-4cf4-83a5-c7cb6999ae24");
        Assert.assertEquals(expectedBuild.getBuildId(), returnedBuild.getBuildId());
    }

    @Test
    public void algoListAlgoBuilds() throws Exception {
        AlgorithmBuildsList algoList = Algorithmia.client(defaultKey).listAlgoBuilds(
                "dherring",
                "ResultFile",
                null,
                null);
        Assert.assertEquals(10, algoList.getResults().size());
    }

    @Test
    public void algoUpdateAlgo() throws Exception {
        Algorithm algorithm = Algorithmia.client(defaultKey).getAlgo("dherring", "ResultFile");
        algorithm.getDetails().setLabel("Enough");
        Gson gson = new Gson();
        String json = gson.toJson(algorithm);
        Algorithm newAlgorithm = Algorithmia.client(defaultKey).updateAlgo("dherring", "ResultFile", json);
        Assert.assertEquals(algorithm.getDetails().getLabel(), newAlgorithm.getDetails().getLabel());
    }

    @Test
    public void algoGetAlgoBuildLogs() throws Exception {
        BuildLogs buildLogs = Algorithmia.client(defaultKey).getAlgoBuildLogs(
                "dherring",
                "ResultFile",
                "579ff0a8-6b1f-4cf4-83a5-c7cb6999ae24");
        Assert.assertNotNull(buildLogs.getLogs());
    }

    @Test
    public void algoDeleteAlgo() throws Exception {
        Algorithm testAlgo = createTestAlgo();
        Gson gson = new Gson();
        String json = gson.toJson(testAlgo);
        Algorithm newAlgorithm = Algorithmia.client(defaultKey).createAlgo("dherring", json);
        HttpResponse response = Algorithmia.client(defaultKey).deleteAlgo("dherring", newAlgorithm.getName());
        Assert.assertEquals(204, response.getStatusLine().getStatusCode());
    }

    @Test
    public void algoCreateUser() throws Exception {
        JsonObject testUserPayload = createTestUserPayload();
        Gson gson = new Gson();
        String json = gson.toJson(testUserPayload);
        User newUser = Algorithmia.client(adminKey, testAddress).createUser(json);
        Assert.assertEquals(testUserPayload.get("username").getAsString(), newUser.getUserName());
    }

    @Test
    public void algoCreateOrganization() throws Exception {
        JsonObject testOrganizationPayload = createTestOrganizationPayload();
        Gson gson = new Gson();
        String json = gson.toJson(testOrganizationPayload);
        Organization newOrganization = Algorithmia.client(adminKey, testAddress).createOrganization(json);
        Assert.assertEquals(testOrganizationPayload.get("org_email").getAsString(), newOrganization.getOrgEmail());
    }

    @Test
    public void algoGetOrganization() throws Exception {
        Organization organization = Algorithmia.client(adminKey, testAddress).getOrganization("a_myOrg15");
        Assert.assertEquals(organization.getOrgEmail(), "a_myOrg15@algo.com");
    }

    @Test
    public void algoEditOrganization() throws Exception {
        JsonObject editOrganizationPayload = editTestOrganizationPayload();
        Gson gson = new Gson();
        String json = gson.toJson(editOrganizationPayload);
        HttpResponse response = Algorithmia.client(adminKey, testAddress).editOrganization("MyOrg1606332498213", json);
        Assert.assertEquals(204, response.getStatusLine().getStatusCode());
    }

    @Test
    public void algoAddOrganizationMember() throws Exception {
        JsonObject testUserPayload = createTestUserPayload();
        Gson gson = new Gson();
        String json = gson.toJson(testUserPayload);
        User newUser = Algorithmia.client(adminKey, testAddress).createUser(json);
        HttpResponse response = Algorithmia.client(adminKey, testAddress).addOrganizationMember("MyOrg1606329175792", newUser.getUserName());
        Assert.assertEquals(201, response.getStatusLine().getStatusCode());
    }

    @Test
    public void algoReportInsights() throws Exception {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 5, 5), 200, ""));
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream("{\"response\": \"hello\"}".getBytes()));
        response.setEntity(httpEntity);

        Mockito.when(httpClient.post(anyString(), any())).thenReturn(response);

        AlgorithmiaClient algorithmiaClient = new AlgorithmiaClient(httpClient);

        AlgorithmiaInsights algorithmiaInsights = algorithmiaClient.reportInsights("{\"cats_in_image\": \"4\", \"dogs_in_image\": \"7\"}");

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor.forClass(StringEntity.class);

        Mockito.verify(httpClient).post(eq("/v1/insights"), captor.capture());

        StringEntity capturedValue = captor.getValue();

        String expectedString = "[{\"insight_key\": \"cats_in_image\", \"insight_value\": \"4\"}, {\"insight_key\": \"dogs_in_image\", \"insight_value\": \"7\"}]";
        Assert.assertEquals(expectedString, new String(capturedValue.getContent().readAllBytes()));
        Assert.assertEquals("hello", algorithmiaInsights.getResponse());
    }

    private Algorithm createTestAlgo() {
        String name = "CreateAlgoTest" + System.currentTimeMillis();
        Algorithm.Details details = new Algorithm.Details();
        details.setLabel("CreateAlgoTest" + System.currentTimeMillis());
        Algorithm.Settings settings = new Algorithm.Settings();
        settings.setEnvironment("cpu");
        settings.setLanguage("java");
        settings.setLicense("ap1");
        settings.setNetworkAccess("full");
        settings.setPipelineEnabled(true);
        settings.setSourceVisibility("open");
        return Algorithm.builder().name(name).details(details).settings(settings).buildDTO();
    }

    private JsonObject createTestUserPayload() {
        JsonObject testUserPayload = new JsonObject();
        testUserPayload.addProperty("username", "sherring" + System.currentTimeMillis());
        testUserPayload.addProperty("email", System.currentTimeMillis() + "@algo.com");
        testUserPayload.addProperty("passwordHash", "");
        testUserPayload.addProperty("shouldCreateHello", false);
        return testUserPayload;
    }

    private JsonObject createTestOrganizationPayload() {
        JsonObject testOrganizationPayload = new JsonObject();
        testOrganizationPayload.addProperty("org_name", "MyOrg" + System.currentTimeMillis());
        testOrganizationPayload.addProperty("org_label", "myLabel");
        testOrganizationPayload.addProperty("org_contact_name", "some owner");
        testOrganizationPayload.addProperty("org_email", System.currentTimeMillis() + "@algo.com");
        testOrganizationPayload.addProperty("type_id", "basic");
        return testOrganizationPayload;
    }

    private JsonObject editTestOrganizationPayload() {
        JsonObject testOrganizationPayload = new JsonObject();
        testOrganizationPayload.addProperty("org_label", "myLabel");
        testOrganizationPayload.addProperty("org_contact_name", "some owner");
        testOrganizationPayload.addProperty("org_email", System.currentTimeMillis() + "@algo.com");
        testOrganizationPayload.addProperty("type_id", "legacy");
        testOrganizationPayload.addProperty("resource_type", "organization");
        testOrganizationPayload.addProperty("id", "3d9a9f41-d82a-11ea-9a3c-0ee5e2d35097");
        return testOrganizationPayload;
    }
}
