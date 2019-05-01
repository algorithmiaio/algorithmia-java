import com.algorithmia.Algorithmia;
import com.algorithmia.AlgorithmiaClient;
import com.algorithmia.algorithmHandler.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Integer;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import org.junit.Test;
import org.junit.Assert;




class AdvancedInput{
    String name;
    Integer age;
}

class BasicAlgroithm{
    String Foo(String input) throws Exception {
        return "Hello " + input;
    }
}

class AdvancedAlgorithm {
    String Apply(AdvancedInput input, HashMap<String, String> context) throws Exception{
        if(context.containsKey("local_file")){
            return "Hello " + input.name + " you are " + input.age +
                    " years old, and your model file is downloaded here " + context.get("local_file");
        }
        return "hello " + input.name+ " you are " + input.age + " years old";
    }
    HashMap<String, String> DownloadModel() throws Exception{
        HashMap<String, String> context = new HashMap<>();
        context.put("local_file", "/tmp/somefile");
        return context;
    }


public class AlgorithmHandlerTest {
    /// TEXT hello world
    @Test
    public void TestSimple(){
        BasicAlgroithm algo = new BasicAlgroithm();
        AlgorithmHandler handler = new AlgorithmHandler<>(algo::Foo);
        try {
            handler.run();
        } catch(Exception e){
            Assert.fail();
        }
    }
}
}