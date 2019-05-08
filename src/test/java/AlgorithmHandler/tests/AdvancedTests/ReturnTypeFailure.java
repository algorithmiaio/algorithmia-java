package AlgorithmHandler.tests.AdvancedTests;

import AlgorithmHandler.algorithms.FileHandleAlgorithm;
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

public class ReturnTypeFailure extends AlgorithmHandlerTestBase {


    private FileHandleAlgorithm algo = new FileHandleAlgorithm();
    private JsonObject request = GenerateInput();
    private JsonObject expectedResponse = GenerateOutput();


    public JsonObject GenerateInput(){
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "text");
        object.addProperty("data", "/tmp/somefile.txt");
        return object;
    }

    public JsonObject GenerateOutput(){
        JsonObject expectedResponse = new JsonObject();
        expectedResponse.addProperty("message", "your output type was not successfully serializable.");
        return expectedResponse;
    }

    @Test
    public void runAlgorithm() throws Exception{

        AlgorithmHandler handler = new AlgorithmHandler<>(algo.getClass(), algo::foo);
        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());

        System.setIn(fakeIn);
        handler.run();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse.get("message"), actualResponse.get("message"));

    }


}
