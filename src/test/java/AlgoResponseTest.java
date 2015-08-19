import com.algorithmia.algo.*;

import com.algorithmia.client.HttpClientHelpers;
import com.algorithmia.TypeToken;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Integer;
import java.util.List;
import java.util.Arrays;

import org.junit.Test;
import org.junit.Assert;

public class AlgoResponseTest {

    ////
    // Test - algo_failure
    ////
    @Test
    public void algoFailure() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_failure.json");
        Assert.assertEquals(false, response.isSuccess());
        Assert.assertEquals(true, response.isFailure());
    }

    ////
    // Test - algo_success
    ////

    //Void response
    @Test
    public void algoResponseAsVoid() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_void.json");
        Object result = response.as(new TypeToken<Integer>(){});
        Assert.assertEquals(null, result);
    }

    //Text response
    @Test
    public void algoResponseAsString() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_text_string.json");
        String result = response.as(new TypeToken<String>(){});
        String expected = "This is a success test";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void algoResponseAsStringtoInt() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_text_int.json");
        int result = response.as(new TypeToken<Integer>(){});
        Assert.assertEquals(42, result);
    }

    //Json response
    @Test
    public void algoResponseAsInt() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_json_int.json");
        int result = response.as(new TypeToken<Integer>(){});
        Assert.assertEquals(42, result);
    }

    @Test
    public void algoResponseAsIntToString() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_json_int.json");
        String result = response.as(new TypeToken<String>(){});
        Assert.assertEquals("42", result);
    }

    @Test
    public void algoSuccess() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_json_array_long.json");
        Assert.assertEquals(true, response.isSuccess());
        Assert.assertEquals(false, response.isFailure());
        Assert.assertEquals("[2,2,2,3,3]", response.as(new TypeToken<JsonElement>(){}).toString());
        Assert.assertEquals("[2,2,2,3,3]", response.asJsonString());
    }
    @Test
    public void algoResponseMetadata() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_json_array_long.json");
        Metadata meta = response.getMetadata();
        Assert.assertEquals(0.035916637, meta.getDuration(), 0.0001);
    }

    @Test
    public void algoResponseAsList() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_json_array_long.json");
        List<Long> result = response.as(new TypeToken<List<Long>>(){});
        List<Long> expected = Arrays.asList(2L, 2L, 2L, 3L, 3L);
        Assert.assertEquals(expected, result);
    }

    //Binary response
    @Test
    public void algoResponseAsBinary() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_binary.json");
        byte[] result = response.as(new TypeToken<byte[]>(){});
        Assert.assertEquals(10, result.length);
    }


    ////
    // Helpers
    ////
    private AlgoResponse parseResourceAsResponse(String filename) throws Exception {
        final JsonParser parser = new JsonParser();
        final InputStream is = this.getClass().getResourceAsStream(filename);
        final JsonElement jsonOutput = parser.parse(new InputStreamReader(is, "UTF-8"));
        return HttpClientHelpers.jsonToAlgoResponse(jsonOutput);
    }

}
