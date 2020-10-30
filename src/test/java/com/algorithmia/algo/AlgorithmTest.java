package com.algorithmia.algo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class AlgorithmTest {

    private String defaultKey;

    @Before
    public void setup() {
        defaultKey = System.getenv("ALGORITHMIA_DEFAULT_API_KEY");
        Assume.assumeNotNull(defaultKey);
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

    /* This will be uncommented during DEV-80
    @Test
    public void algoQuerySCMStatus() throws Exception {
        AlgorithmSCMAuthorizationStatus algorithmSCMAuthorizationStatus = Algorithmia.client(defaultKey).querySCMStatus("github");
        Assert.assertEquals("authorized", algorithmSCMAuthorizationStatus.getAuthorizationStatus());
    }

    @Test
    public void algoRevokeSCMStatus() throws Exception {
        AlgorithmSCMAuthorizationStatus algorithmSCMAuthorizationStatus = Algorithmia.client(defaultKey).revokeSCMStatus("?");
        Assert.assertEquals("?", algorithmSCMAuthorizationStatus.getAuthorizationStatus());
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

    private Algorithm createTestAlgo() {
        String name = "CreateAlgoTest" + System.currentTimeMillis();
        Algorithm.Details details = new Algorithm.Details();
        details.setLabel("CreateAlgoTest");
        Algorithm.Settings settings = new Algorithm.Settings();
        settings.setEnvironment("cpu");
        settings.setLanguage("java");
        settings.setLicense("ap1");
        settings.setNetworkAccess("full");
        settings.setPipelineEnabled(true);
        settings.setSourceVisibility("open");
        return Algorithm.builder().name(name).details(details).settings(settings).buildDTO();
    }
}
