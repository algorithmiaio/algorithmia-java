package AlgorithmHandler;

import com.algorithmia.algorithmHandler.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import AlgorithmHandler.algorithms.BasicAlgorithm;
import org.junit.Test;
import org.junit.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BasicTest extends AlgorithmHandlerTestBase {

    private BasicAlgorithm algo = new BasicAlgorithm();
    private Gson gson = new Gson();
    private JsonObject request = PrepareInput();
    private JsonObject expectedResponse = PrepareOutput();

    JsonObject PrepareInput(){
        String inputObj = "james";
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "text");
        object.add("data", gson.toJsonTree(inputObj));

        return object;
    }

    JsonObject PrepareOutput(){
        JsonObject expectedResponse = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("content_type", "text");
        expectedResponse.add("metadata", metadata);
        expectedResponse.addProperty("result", "Hello james");
        return expectedResponse;
    }


    /// TEXT hello world
    @Test
    public void RunAlgorithm()throws Exception{

        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Foo, String.class);
        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());

        System.setIn(fakeIn);
        handler.run();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse  = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }
}