
import com.algorithmia.algorithmHandler.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonParser;

public class AlgorithmHandlerTest {

    private String FIFOPIPE = "/tmp/algoout";
    private JsonParser parser = new JsonParser();

    @Before
    public void IntializePipe() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("touch  "+ FIFOPIPE);
        p.waitFor();
        System.out.println("fifo pipe made.");
    }

    /// TEXT hello world
    @Test
    public void SimpleAlgoRequest()throws Exception{

        JsonObject expectedResponse = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("content_type", "text");
        expectedResponse.add("meta_data", metadata);
        expectedResponse.addProperty("result", "Hello james");

        BasicAlgorithm algo = new BasicAlgorithm();
        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Foo);
        InputStream fakeIn = new ByteArrayInputStream("{\"content_type\":\"text\", \"data\":\"james\")".getBytes());

        System.setIn(fakeIn);
        handler.run();


        byte[] fifoBytes = Files.readAllBytes(Paths.get(FIFOPIPE));
        String rawData = new String(fifoBytes);
        JsonObject actualResponse  = parser.parse(rawData).getAsJsonObject();
        Assert.assertEquals(expectedResponse, actualResponse);

    }
    @After
    public void TeardownPipe(){
        File pipe = new File(FIFOPIPE);
        pipe.delete();
    }
}