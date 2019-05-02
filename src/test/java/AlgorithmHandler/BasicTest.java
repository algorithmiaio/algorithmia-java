package AlgorithmHandler;

import com.algorithmia.algorithmHandler.*;
import com.google.gson.JsonObject;
import AlgorithmHandler.algorithms.BasicAlgorithm;
import org.junit.Test;
import org.junit.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BasicTest extends AlgorithmHandlerTestBase {

    /// TEXT hello world
    @Test
    public void RunAlgorithm()throws Exception{

        JsonObject expectedResponse = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("content_type", "text");
        expectedResponse.add("metadata", metadata);
        expectedResponse.addProperty("result", "Hello james");

        BasicAlgorithm algo = new BasicAlgorithm();
        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Foo);
        InputStream fakeIn = new ByteArrayInputStream("{\"content_type\":\"text\", \"data\":\"james\"}".getBytes());

        System.setIn(fakeIn);
        handler.run();


        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse  = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }
}