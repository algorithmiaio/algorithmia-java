package AlgorithmHandler;

import AlgorithmHandler.algorithms.AdvancedAlgorithmOne;
import com.algorithmia.algorithmHandler.AlgorithmHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class AdvancedAlgorithmTest extends AlgorithmHandlerTestBase {

    private AdvancedAlgorithmOne algo = new AdvancedAlgorithmOne();
    private Gson gson = new Gson();
    private JsonObject request = PrepareInput();
    private JsonObject expectedResponse = PrepareOutput();

    JsonObject PrepareInput() {
        AdvancedAlgorithmOne.AlgoInput inputObj = algo.new AlgoInput("james", 25);
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "json");
        object.add("data", gson.toJsonTree(inputObj));
        return object;
    }

    JsonObject PrepareOutput() {
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

        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Apply, algo::DownloadModel, AdvancedAlgorithmOne.AlgoInput.class);
        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());

        System.setIn(fakeIn);
        handler.run();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }
}
