package AlgorithmHandler.tests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;

public abstract class AlgorithmHandlerTestBase {

    protected String FIFOPIPE = "/tmp/algoout";

    protected JsonParser parser = new JsonParser();

    public abstract JsonObject GenerateInput();

    public abstract JsonObject GenerateOutput();

    @Before
    public void IntializePipe() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("touch  " + FIFOPIPE);
        p.waitFor();
        System.out.println("fifo pipe made.");
    }

    @After
    public void TeardownPipe() {
        File pipe = new File(FIFOPIPE);
        pipe.delete();
    }
}
