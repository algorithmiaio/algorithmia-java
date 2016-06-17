public class DataFileTest extends DataFileGenericTest {
    private static final String pathPrefix = "data://.my/";

    @Override
    public String getFullPath(String thing) {
        return pathPrefix + thing;
    }
}
