import com.algorithmia.Algorithmia;
import com.algorithmia.algo.*;
import com.algorithmia.TypeToken;
import com.google.gson.JsonElement;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

public class AlgorithmTest {

    @Test
    public void algorithmPipeJson() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        AlgoResponse res = Algorithmia.client(key).algo("docs/JavaAddOne").pipe(41);
        Assert.assertEquals("42", res.as(new TypeToken<JsonElement>(){}).toString());
        int result = res.as(new TypeToken<Integer>(){});
        Assert.assertEquals(42, result);
        Assert.assertEquals(ContentType.Json, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmPipeText() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        AlgoResponse res = Algorithmia.client(key).algo("demo/Hello").pipe("foo");
        Assert.assertEquals("\"Hello foo\"", res.as(new TypeToken<JsonElement>(){}).toString());
        Assert.assertEquals("\"Hello foo\"", res.asJsonString());
        Assert.assertEquals("Hello foo", res.as(new TypeToken<String>(){}));
        Assert.assertEquals("Hello foo", res.asString());
        Assert.assertEquals(ContentType.Text, res.getMetadata().getContentType());
    }

    @Test
    public void algorithmPipeBinary() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        byte[] input = new byte[10];
        AlgoResponse res = Algorithmia.client(key).algo("docs/JavaBinaryInAndOut").pipe(input);
        byte[] output = res.as(new TypeToken<byte[]>(){});
        Assert.assertEquals(Base64.encodeBase64String(input),Base64.encodeBase64String(output));
        Assert.assertEquals(ContentType.Binary, res.getMetadata().getContentType());
    }

}
