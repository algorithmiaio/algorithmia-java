package AlgorithmHandler.tests.BasicTests;

import AlgorithmHandler.algorithms.BasicAlgorithm;
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

public class BaseTypeFailure extends AlgorithmHandlerTestBase {

    private BasicAlgorithm algo = new BasicAlgorithm();
    private Gson gson = new Gson();
    private JsonObject request = GenerateInput();
    private JsonObject expectedResponse = GenerateOutput();


    public JsonObject GenerateInput(){
        Float[] inputObj = new Float[]{0.25f, 0.15f};
        JsonObject object = new JsonObject();
        object.addProperty("content_type", "json");
        object.add("data", gson.toJsonTree(inputObj));

        return object;
    }

    public JsonObject GenerateOutput(){
        JsonObject expectedResponse = new JsonObject();
        expectedResponse.addProperty("message", "unable to parse input into type java.lang.String , with input [0.25,0.15]");
        expectedResponse.addProperty("error_type", "class java.lang.RuntimeException");
        return expectedResponse;
    }

    @Test
    public void runAlgorithm() throws Exception {
        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Foo, String.class);
        InputStream fakeIn = new ByteArrayInputStream(request.toString().getBytes());
        System.setIn(fakeIn);
        handler.run();

        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse.get("message"), actualResponse.get("message"));
    }


}
