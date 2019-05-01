
import com.algorithmia.algorithmHandler.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class AlgorithmHandlerTest {

    @Before
    public void IntializePipe(){
        
    }

    /// TEXT hello world
    @Test
    public void SimpleAlgoRequest(){
        BasicAlgorithm algo = new BasicAlgorithm();
        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Foo);
        try {
            InputStream fakeIn = new ByteArrayInputStream("{\"content_type\":\"text\", \"data\":\"james\")\n".getBytes());
            System.setIn(fakeIn);
            handler.run();

        } catch(Exception e){
            Assert.fail();
        }
    }
}