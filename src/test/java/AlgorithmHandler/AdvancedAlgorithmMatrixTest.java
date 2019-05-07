package AlgorithmHandler;

import AlgorithmHandler.algorithms.AdvancedAlgorithmTwo;
import com.algorithmia.algorithmHandler.AlgorithmHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class AdvancedAlgorithmMatrixTest extends AlgorithmHandlerTestBase {

    private AdvancedAlgorithmTwo algo = new AdvancedAlgorithmTwo();
    private Gson gson = new Gson();
    private JsonObject request = PrepareInput();
    private JsonObject expectedResponse = PrepareOutput();


    JsonObject PrepareInput() {
        AdvancedAlgorithmTwo.AlgoInput inputObj = algo.new AlgoInput(new Float[]{0.25f, 0.25f, 0.25f}, new Float[]{0.25f, 0.25f, 0.25f});
        gson.toJsonTree(inputObj);
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "json");
        object.add("data", gson.toJsonTree(inputObj));
        return object;
    }

    JsonObject PrepareOutput() {
        AdvancedAlgorithmTwo.AlgoOutput outputObj = algo.new AlgoOutput(new Float[]{0.5f, 0.5f, 0.5f});
        JsonObject expectedResponse = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("content_type", "json");
        expectedResponse.add("metadata", metadata);
        expectedResponse.add("result", gson.toJsonTree(outputObj));
        return expectedResponse;
    }

    @Test
    public void RunAlgorithm() throws Exception {
        AlgorithmHandler handler = new AlgorithmHandler<>(algo::matrixElmWiseAddition, AdvancedAlgorithmTwo.AlgoInput.class);

        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());

        System.setIn(fakeIn);
        handler.run();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }
}
