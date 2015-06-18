import com.algorithmia.algo.*;
import com.algorithmia.client.HttpClientHelpers;
import com.algorithmia.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Arrays;

import org.junit.Test;
import org.junit.Assert;

public class AlgoResponseTest {

    @Test
    public void algoSuccess() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_array_long.json");
        Assert.assertEquals(true, response.isSuccess());
        Assert.assertEquals(false, response.isFailure());
        Assert.assertEquals("[2,2,2,3,3]", response.get());
    }

    @Test
    public void algoFailure() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_failure.json");
        Assert.assertEquals(false, response.isSuccess());
        Assert.assertEquals(true, response.isFailure());
    }

    @Test
    public void algoResponseAsList() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_array_long.json");
        List<Long> result = response.as(new TypeToken<List<Long>>(){});
        List<Long> expected = Arrays.asList(2L, 2L, 2L, 3L, 3L);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void algoResponseAsString() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_string.json");
        String result = response.as((new TypeToken<String>(){}));
        String expected = "Whuh... I think so, Brain. But this time I get to play the dishwasher repairman!";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void algoResponseMetadata() throws Exception {
        final AlgoResponse response = parseResourceAsResponse("algo_success_array_long.json");
        Metadata meta = response.metadata();
        Assert.assertEquals(0.035916637, meta.duration, 0.0001);
    }


    private AlgoResponse parseResourceAsResponse(String filename) throws Exception {
        final JsonParser parser = new JsonParser();
        final InputStream is = this.getClass().getResourceAsStream(filename);
        final JsonElement jsonOutput = parser.parse(new InputStreamReader(is, "UTF-8"));
        return HttpClientHelpers.jsonToAlgoResponse(jsonOutput);
    }
}



