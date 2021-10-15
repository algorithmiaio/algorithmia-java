package com.algorithmia.algo;

import com.algorithmia.client.HttpClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.File;
import java.io.IOException;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

public class AlgorithmTest {

    private String user;
    private String env_id = "d8f3110a-ad46-4008-a099-a33824522d09";
    private Algorithm testAlgo;
    private Organization testOrg;
    private String testAlgoBuildId;


    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    private File testfile;

    private String defaultKey;
    private String adminKey;
    private String testAddress;
    private String testDefaultKey;
    @Mock
    private HttpClient httpClient;

    @Before
    public void setup() throws IOException {
        defaultKey = System.getenv("ALGORITHMIA_DEFAULT_API_KEY");
        adminKey = System.getenv("ALGORITHMIA_ADMIN_API_KEY");
        testAddress = System.getenv("ALGORITHMIA_TEST_ADDRESS");
        testDefaultKey = System.getenv("ALGORITHMIA_TEST_DEFAULT_KEY");
        user = System.getenv("ALGORITHMIA_USER_NAME");

        testOrg = createTestOrganization();
        testAlgo = createAlgo(user,testDefaultKey,testAddress);
        publishTestAlgo(testAlgo.getName(),user);
        AlgorithmBuildsList algoList = Algorithmia.client(testDefaultKey,testAddress).listAlgoBuilds(
                user,
                testAlgo.getName(),
                null,
                null);
        List<Algorithm.Build> result = algoList.getResults();
        testAlgoBuildId = result.get(0).getBuildId();


        testfile = tmpFolder.newFile("cert.pem");

        MockitoAnnotations.openMocks(this);
    }


    @After
    public void cleanup() throws IOException {
        Algorithmia.client(testDefaultKey,testAddress).deleteAlgo(user, testAlgo.getName());
    }

    @Test
    public void algorithmPipeJson() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey).algo("docs/JavaAddOne").pipe(41);
        Assert.assertEquals("42", res.as(new TypeToken<JsonElement>() {
        }).toString());
        int result = res.as(new TypeToken<Integer>() {
        });
        Assert.assertEquals(42, result);
        Assert.assertEquals(ContentType.Json, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmCAPipeText() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey, null, 1, testfile.getPath()).algo("demo/Hello").pipe("foo");
        Assert.assertEquals("\"Hello foo\"", res.as(new TypeToken<JsonElement>() {
        }).toString());
        Assert.assertEquals("\"Hello foo\"", res.asJsonString());
        Assert.assertEquals("Hello foo", res.as(new TypeToken<String>() {
        }));
        Assert.assertEquals("Hello foo", res.asString());
        Assert.assertEquals(ContentType.Text, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmPipeText() throws Exception {
        AlgoResponse res = Algorithmia.client(defaultKey).algo("demo/Hello").pipe("foo");
        Assert.assertEquals("\"Hello foo\"", res.as(new TypeToken<JsonElement>() {
        }).toString());
        Assert.assertEquals("\"Hello foo\"", res.asJsonString());
        Assert.assertEquals("Hello foo", res.as(new TypeToken<String>() {
        }));
        Assert.assertEquals("Hello foo", res.asString());
        Assert.assertEquals(ContentType.Text, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmPipeBinary() throws Exception {
        byte[] input = new byte[10];
        AlgoResponse res = Algorithmia.client(defaultKey).algo("docs/JavaBinaryInAndOut").pipe(input);
        byte[] output = res.as(new TypeToken<byte[]>() {
        });
        Assert.assertEquals(Base64.encodeBase64String(input), Base64.encodeBase64String(output));
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
        Assert.assertEquals((long) 300, (long) algo.getTimeout());

        // Check Minute conversion
        algo = algo.setTimeout(20L, TimeUnit.MINUTES);
        Assert.assertEquals((long) 20 * 60, (long) algo.getTimeout());

        // And seconds just in case
        algo = algo.setTimeout(30L, TimeUnit.SECONDS);
        Assert.assertEquals((long) 30, (long) algo.getTimeout());

        // And milliseconds
        algo = algo.setTimeout(5000L, TimeUnit.MILLISECONDS);
        Assert.assertEquals((long) 5, (long) algo.getTimeout());
    }

    @Test
    public void algoGetAlgo() throws Exception {
        Algorithm algorithm = Algorithmia.client(testDefaultKey,testAddress).getAlgo(user, testAlgo.getName());
        Assert.assertEquals(algorithm.getName(), testAlgo.getName());
    }

    @Test
    public void algoCreateAlgo() throws Exception {
        Algorithm algo = createTestAlgo();
        Gson gson = new Gson();
        String json = gson.toJson(algo);
        Algorithm newAlgorithm = Algorithmia.client(testDefaultKey,testAddress).createAlgo(user, json);
        Assert.assertEquals(algo.getName(), newAlgorithm.getName());
    }

    @Test
    public void algoCompileAlgo() throws Exception {
        Algorithm algorithm = Algorithmia.client(testDefaultKey, testAddress).compileAlgo(user, testAlgo.getName());
        Assert.assertEquals(algorithm.getName(), testAlgo.getName());
    }

    @Test
    public void algoPublishAlgo() throws Exception {
        Algorithm newAlgorithm = publishTestAlgo(testAlgo.getName(),user);
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
        AlgorithmSCMStatus scmStatus = Algorithmia.client(testDefaultKey,testAddress).getAlgoSCMStatus(user, testAlgo.getName());
        Assert.assertEquals("active", scmStatus.getScmConnectionStatus());
    }

    @Test
    public void algoListAlgoVersions() throws Exception {
        AlgorithmVersionsList algoList = Algorithmia.client(testDefaultKey, testAddress).listAlgoVersions(
                user,
                testAlgo.getName(),
                null,
                null,
                null,
                null);
        Assert.assertTrue(algoList.getResults().size() > 0);
    }

    @Test
    public void algoGetEnvironments() throws Exception {
        Environment[] environments = Algorithmia.client(defaultKey,testAddress).getEnvironment("python3");
        Assert.assertTrue(environments.length > 0);
    }

    @Test
    public void algoGetAlgoBuild() throws Exception {
        Algorithm.Build expectedBuild = new Algorithm.Build();
        expectedBuild.setBuildId(testAlgoBuildId);
        Algorithm.Build returnedBuild = Algorithmia.client(testDefaultKey,testAddress).getAlgoBuild(
                user,
                testAlgo.getName(),
                testAlgoBuildId);
        Assert.assertEquals(expectedBuild.getBuildId(), returnedBuild.getBuildId());
    }

    @Test
    public void algoListAlgoBuilds() throws Exception {
        AlgorithmBuildsList algoList = Algorithmia.client(testDefaultKey,testAddress).listAlgoBuilds(
                user,
                testAlgo.getName(),
                null,
                null);
        Assert.assertTrue( algoList.getResults().size() > 0);
    }

    @Test
    public void algoUpdateAlgo() throws Exception {

        Algorithm.Details details = new Algorithm.Details();
        details.setLabel("Enough");
        Algorithm.Settings settings = new Algorithm.Settings();
        settings.setAlgorithmEnvironment(env_id);
        settings.setLicense("apl");
        settings.setNetworkAccess("full");
        settings.setPipelineEnabled(true);
        settings.setSourceVisibility("open");

        Algorithm algorithm = Algorithm.builder().details(details).settings(settings).buildDTO();

        Gson gson = new Gson();
        String json = gson.toJson(algorithm);
        Algorithm newAlgorithm = Algorithmia.client(testDefaultKey,testAddress).updateAlgo(user, testAlgo.getName(), json);
         Assert.assertEquals(algorithm.getDetails().getLabel(), newAlgorithm.getDetails().getLabel());
    }

    @Test
    public void algoGetAlgoBuildLogs() throws Exception {
        BuildLogs buildLogs = Algorithmia.client(testDefaultKey,testAddress).getAlgoBuildLogs(
                user,
                testAlgo.getName(),
                testAlgoBuildId);
        Assert.assertNotNull(buildLogs.getLogs());
    }

    @Test
    public void algoGetUserErrorLogs() throws Exception {
        Algorithm brokenAlgo = createAlgo(user,testDefaultKey,testAddress);
        causeAlgorithmError(brokenAlgo.getName(),user,adminKey);
        ErrorLogs errorlogs[] = Algorithmia.client(testDefaultKey,testAddress).getUserErrors(user);
        Assert.assertNotNull(errorlogs[0].getCreatedAt());
        Algorithmia.client(testDefaultKey,testAddress).deleteAlgo(user, brokenAlgo.getName());

    }

    @Test
    public void algoGetAlgorithmErrorLogs() throws Exception {
        Algorithm brokenAlgo = createAlgo(user,testDefaultKey,testAddress);
        causeAlgorithmError(brokenAlgo.getName(),user,adminKey);
        AlgoResponse res = Algorithmia.client(testDefaultKey,testAddress).algo(user+"/"+brokenAlgo.getName()+"/1.0.0").pipe("name");
        ErrorLogs errorlogs[] = Algorithmia.client(testDefaultKey,testAddress).getAlgorithmErrors(user+"/"+brokenAlgo.getName());
        Assert.assertNotNull(errorlogs[0].getCreatedAt());
        Algorithmia.client(testDefaultKey,testAddress).deleteAlgo(user, brokenAlgo.getName());
    }

    @Test
    public void algoGetOrgErrorLogs() throws Exception {
        Algorithm brokenAlgo = createAlgo(testOrg.getOrgName(),testDefaultKey,testAddress);
        causeAlgorithmError(brokenAlgo.getName(),testOrg.getOrgName(),adminKey);
        ErrorLogs errorlogs[] = Algorithmia.client(adminKey,testAddress).getOrganizationErrors(testOrg.getOrgName());
        Assert.assertNotNull(errorlogs[0].getCreatedAt());
        Algorithmia.client(testDefaultKey,testAddress).deleteAlgo(testOrg.getOrgName(), brokenAlgo.getName());
    }

    @Test
    public void algoDeleteAlgo() throws Exception {
        Algorithm testAlgo = createTestAlgo();
        Gson gson = new Gson();
        String json = gson.toJson(testAlgo);
        Algorithm newAlgorithm = Algorithmia.client(testDefaultKey,testAddress).createAlgo(user, json);
        HttpResponse response = Algorithmia.client(testDefaultKey,testAddress).deleteAlgo(user, newAlgorithm.getName());
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
        Organization organization = Algorithmia.client(adminKey, testAddress).getOrganization(testOrg.getOrgName());
        Assert.assertEquals(organization.getOrgEmail(), "test@testAlgo.com");
    }

    @Test
    public void algoEditOrganization() throws Exception {
        JsonObject editOrganizationPayload = editTestOrganizationPayload();
        Gson gson = new Gson();
        String json = gson.toJson(editOrganizationPayload);
        HttpResponse response = Algorithmia.client(adminKey, testAddress).editOrganization(testOrg.getOrgName(), json);
        Assert.assertEquals(204, response.getStatusLine().getStatusCode());
    }

    @Test
    public void algoAddOrganizationMember() throws Exception {
        JsonObject testUserPayload = createTestUserPayload();
        Gson gson = new Gson();
        String json = gson.toJson(testUserPayload);
        User newUser = Algorithmia.client(adminKey, testAddress).createUser(json);
        HttpResponse response = Algorithmia.client(adminKey, testAddress).addOrganizationMember(testOrg.getOrgName(), newUser.getUserName());
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

    private void causeAlgorithmError(String algoTarget,String userTarget,String key){

        CloseableHttpClient client = null;

        HttpClientBuilder builder = HttpClientBuilder.create();
        Collection<Header> headers = Arrays.asList(new BasicHeader(HttpHeaders.AUTHORIZATION,key));
        builder.setDefaultHeaders(headers);
        client = builder.build();

        HttpResponse response;
        HttpPost request;

        String brokenString = "import Algorithmia\n"
                +"\n"
                +"def apply(input):\n"
                +"    return \"Hello hello, {}\".forma(input)";

        try {
            request = new HttpPost(testAddress+"/source/"+userTarget+"/"+algoTarget+"/src/"+algoTarget+".py");
            request.addHeader(HttpHeaders.CONTENT_TYPE,"text/plain");
            request.setEntity(new StringEntity(brokenString));

            response = client.execute(request);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        publishTestAlgo(algoTarget,userTarget);
    }

    private void causeAlgorithmErrorXSRF(String algoTarget,String userTarget,String key){

        CloseableHttpClient signInClient = null;
        CloseableHttpClient client = null;
        BasicCookieStore cookieStore = new BasicCookieStore();

        HttpClientBuilder signInBuilder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore);
        Collection<Header> signInHeaders = Arrays.asList(new BasicHeader(HttpHeaders.AUTHORIZATION,key));
        signInBuilder.setDefaultHeaders(signInHeaders);
        signInClient = signInBuilder.build();

        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore);
        Collection<Header> headers = Arrays.asList(new BasicHeader(HttpHeaders.AUTHORIZATION,key));
        builder.setDefaultHeaders(headers);
        client = builder.build();

        HttpResponse response;
        HttpPost request;


        String brokenString = "import Algorithmia\n"
                +"\n"
                +"def apply(input):\n"
                +"    return \"Hello hello, {}\".forma(input)";

        try {


            HttpGet signInRequest = new HttpGet("https://api.test.algorithmia.com/v1/algorithm-environments/edge/environments/current");
            HttpResponse r = signInClient.execute(signInRequest);
            String val = r.getHeaders("set-cookie")[0].getValue();

            request = new HttpPost(testAddress+"/source/"+userTarget+"/"+algoTarget+"/src/"+algoTarget+".py");
            String[] a = val.split("=");
            String[] value = a[1].split(";");
            //cookieStore.addCookie(new BasicClientCookie(a[0],a[1]));
            request.addHeader("X-XSRF-TOKEN",value[0]);
            request.addHeader(HttpHeaders.CONTENT_TYPE,"text/plain");
            request.setEntity(new StringEntity(brokenString));

            response = client.execute(request);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        publishTestAlgo(algoTarget,userTarget);
    }

    private Algorithm publishTestAlgo(String algoTarget,String userTarget){
        Algorithm.VersionInfo versionInfo = new Algorithm.VersionInfo();
        versionInfo.setVersionType("major");
        Algorithm algorithm = Algorithm.builder().versionInfo(versionInfo).buildDTO();
        Gson gson = new Gson();
        String json = gson.toJson(algorithm);
        //Must call compile in order to increase version of already published algorithm
        try {
            Algorithmia.client(testDefaultKey, testAddress).compileAlgo(userTarget, algoTarget);
            return Algorithmia.client(testDefaultKey, testAddress).publishAlgo(userTarget, algoTarget, json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return algorithm;
    }

    private Algorithm createAlgo(String targetUser, String apiKey, String address) {
        Algorithm testAlgo = createTestAlgo();
        Gson gson = new Gson();
        String json = gson.toJson(testAlgo);
        Algorithm newAlgo = null;
        try {
            newAlgo = Algorithmia.client(apiKey, address).createAlgo(targetUser, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newAlgo;
    }

    private Organization createTestOrganization(){
        JsonObject testOrganizationPayload = createTestOrganizationPayload();
        Gson gson = new Gson();
        String json = gson.toJson(testOrganizationPayload);
        Organization newOrganization = null;
        try {
            newOrganization = Algorithmia.client(adminKey, testAddress).createOrganization(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newOrganization;
    }

    private Algorithm createTestAlgo() {
        String name = "CreateAlgoTest" + System.currentTimeMillis();
        Algorithm.Details details = new Algorithm.Details();
        details.setLabel(name);
        Algorithm.Settings settings = new Algorithm.Settings();
        settings.setAlgorithmEnvironment(env_id);
        settings.setLicense("apl");
        settings.setNetworkAccess("full");
        settings.setPipelineEnabled(true);
        settings.setSourceVisibility("open");

        return Algorithm.builder().details(details).name(name).settings(settings).buildDTO();
    }

    private JsonObject createTestUserPayload() {
        JsonObject testUserPayload = new JsonObject();
        testUserPayload.addProperty("username", "sherring" + System.currentTimeMillis());
        testUserPayload.addProperty("email", System.currentTimeMillis() + "@testAlgo.com");
        testUserPayload.addProperty("passwordHash", "");
        testUserPayload.addProperty("shouldCreateHello", false);
        return testUserPayload;
    }

    private JsonObject createTestOrganizationPayload() {
        JsonObject testOrganizationPayload = new JsonObject();
        testOrganizationPayload.addProperty("org_name", "MyOrg" + System.currentTimeMillis());
        testOrganizationPayload.addProperty("org_label", "myLabel");
        testOrganizationPayload.addProperty("org_contact_name", "some owner");
        testOrganizationPayload.addProperty("org_email", "test@testAlgo.com");
        testOrganizationPayload.addProperty("type_id", "basic");
        testOrganizationPayload.addProperty("org_url", "https://algorithmia.com");
        return testOrganizationPayload;
    }

    private JsonObject editTestOrganizationPayload() {
        JsonObject testOrganizationPayload = new JsonObject();
        testOrganizationPayload.addProperty("org_label", "myLabel");
        testOrganizationPayload.addProperty("org_contact_name", "some owner");
        testOrganizationPayload.addProperty("org_email", System.currentTimeMillis() + "@testAlgo.com");
        testOrganizationPayload.addProperty("type_id", "legacy");
        testOrganizationPayload.addProperty("resource_type", "organization");
        testOrganizationPayload.addProperty("id", "3d9a9f41-d82a-11ea-9a3c-0ee5e2d35097");
        testOrganizationPayload.addProperty("org_url", "https://algorithmia.com");
        return testOrganizationPayload;
    }
}
