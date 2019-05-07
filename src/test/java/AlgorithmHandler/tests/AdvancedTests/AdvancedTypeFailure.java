package AlgorithmHandler.tests.AdvancedTests;

import AlgorithmHandler.algorithms.LoadingAlgorithm;
import AlgorithmHandler.algorithms.MatrixAlgorithm;
import AlgorithmHandler.tests.AlgorithmHandlerTestBase;
import com.algorithmia.algorithmHandler.AlgorithmHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AdvancedTypeFailure extends AlgorithmHandlerTestBase {

    private LoadingAlgorithm algo = new LoadingAlgorithm();
    private Gson gson = new Gson();
    private JsonObject request = GenerateInput();
    private JsonObject expectedResponse = GenerateOutput();

    public JsonObject GenerateInput() {
        MatrixAlgorithm tmp = new MatrixAlgorithm();
        MatrixAlgorithm.AlgoInput inputObj = tmp.new AlgoInput(new Float[]{0.25f, 0.15f}, new Float[]{0.12f, -0.15f});
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "json");
        object.add("data", gson.toJsonTree(inputObj));
        return object;
    }

    public JsonObject GenerateOutput() {
        String outputObj = "Hello james you are 25 years old, and your model file is downloaded here /tmp/somefile";
        JsonObject expectedResponse = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("content_type", "text");
        expectedResponse.add("metadata", metadata);
        expectedResponse.addProperty("result", outputObj);
        return expectedResponse;
    }


    @Test
    public void RunAlgorithm() throws Exception {

        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Apply, algo::DownloadModel, LoadingAlgorithm.AlgoInput.class);
        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());

        System.setIn(fakeIn);
        handler.run();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }

}
