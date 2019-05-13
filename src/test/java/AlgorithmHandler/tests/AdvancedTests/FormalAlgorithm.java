package AlgorithmHandler.tests.AdvancedTests;

import AlgorithmHandler.algorithms.MatrixAlgorithm;
import AlgorithmHandler.tests.HandlerTestBase;
import com.algorithmia.algorithm.Handler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FormalAlgorithm extends HandlerTestBase {

    private MatrixAlgorithm algo = new MatrixAlgorithm();
    private Gson gson = new Gson();
    private JsonObject request = GenerateInput();
    private JsonObject expectedResponse = GenerateOutput();


    public JsonObject GenerateInput() {
        MatrixAlgorithm.AlgoInput inputObj = algo.new AlgoInput(new Float[]{0.25f, 0.25f, 0.25f}, new Float[]{0.25f, 0.25f, 0.25f});
        gson.toJsonTree(inputObj);
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "json");
        object.add("data", gson.toJsonTree(inputObj));
        return object;
    }

    public JsonObject GenerateOutput() {
        MatrixAlgorithm.AlgoOutput outputObj = algo.new AlgoOutput(new Float[]{0.5f, 0.5f, 0.5f});
        JsonObject expectedResponse = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("content_type", "json");
        expectedResponse.add("metadata", metadata);
        expectedResponse.add("result", gson.toJsonTree(outputObj));
        return expectedResponse;
    }

    @Test
    public void RunAlgorithm() throws Exception {
        Handler handler = new Handler<>(algo.getClass(), algo::matrixElmWiseAddition);

        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());

        System.setIn(fakeIn);
        handler.serve();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }
}
