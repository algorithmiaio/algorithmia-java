package extras;

import java.util.HashMap;

class AdvancedInput{
    String name;
    Integer age;
}


public class AdvancedAlgorithm {
    String Apply(AdvancedInput input, HashMap<String, String> context) throws Exception {
        if (context.containsKey("local_file")) {
            return "Hello " + input.name + " you are " + input.age +
                    " years old, and your model file is downloaded here " + context.get("local_file");
        }
        return "hello " + input.name + " you are " + input.age + " years old";
    }

    HashMap<String, String> DownloadModel() throws Exception {
        HashMap<String, String> context = new HashMap<>();
        context.put("local_file", "/tmp/somefile");
        return context;
    }
}