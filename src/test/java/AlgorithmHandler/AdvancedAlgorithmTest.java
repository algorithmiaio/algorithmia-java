package AlgorithmHandler;

import AlgorithmHandler.algorithms.AdvancedAlgorithm;
import com.algorithmia.algorithmHandler.AlgorithmHandler;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AdvancedAlgorithmTest extends AlgorithmHandlerTestBase {

    @Test
    public void RunAlgorithm()throws Exception{

        JsonObject expectedResponse = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("content_type", "text");
        expectedResponse.add("metadata", metadata);
        expectedResponse.addProperty("result", "Hello james");

        AdvancedAlgorithm algo = new AdvancedAlgorithm();
        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Apply, algo::DownloadModel);
        InputStream fakeIn = new ByteArrayInputStream("{\"content_type\":\"json\", \"data\":{\"name\":\"james\", \"age\":25}}".getBytes());

        System.setIn(fakeIn);
        handler.run();


        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse  = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }
}
