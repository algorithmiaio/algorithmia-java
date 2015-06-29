import com.algorithmia.Algorithmia;
import com.algorithmia.algo.*;

import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

public class AlgorithmTest {

    @Test
    public void algorithmPipe() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        AlgoResponse res = Algorithmia.client(key).algo("kenny/factor").pipe("14");
        Assert.assertEquals("[2,7]", res.toString());
    }

}
