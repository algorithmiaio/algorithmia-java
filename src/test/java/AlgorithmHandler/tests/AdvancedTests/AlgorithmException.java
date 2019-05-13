package AlgorithmHandler.tests.AdvancedTests;

import AlgorithmHandler.algorithms.ThrowsExceptionAlgorithm;
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

public class AlgorithmException extends HandlerTestBase {


    private ThrowsExceptionAlgorithm algo = new ThrowsExceptionAlgorithm();
    private Gson gson = new Gson();
    private JsonObject request = GenerateInput();
    private JsonObject expectedResponse = GenerateOutput();


    public JsonObject GenerateInput() {
        String inputObj = "hello world";
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "text");
        object.add("data", gson.toJsonTree(inputObj));
        return object;
    }

    public JsonObject GenerateOutput() {
        JsonObject expectedResponse = new JsonObject();
        expectedResponse.addProperty("message", "This is an exception.");
        return expectedResponse;
    }

    @Test
    public void RunAlgorithm() throws Exception {

        Handler handler = new Handler<>(algo.getClass(), algo::foo);
        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());

        System.setIn(fakeIn);
        handler.serve();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse.get("message"), actualResponse.get("message"));

    }

}
