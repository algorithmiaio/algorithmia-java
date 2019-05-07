package AlgorithmHandler.algorithms;

import java.util.HashMap;


public class AdvancedAlgorithmOne {

    public class AlgoInput {
        String name;
        Integer age;

        public AlgoInput(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }

    public String Apply(AlgoInput input, HashMap<String, String> context) throws Exception {
        if (context != null && context.containsKey("local_file")) {
            return "Hello " + input.name + " you are " + input.age +
                    " years old, and your model file is downloaded here " + context.get("local_file");
        }
        return "hello " + input.name + " you are " + input.age + " years old";
    }

    public HashMap<String, String> DownloadModel() throws Exception {
        HashMap<String, String> context = new HashMap<>();
        context.put("local_file", "/tmp/somefile");
        return context;
    }
}