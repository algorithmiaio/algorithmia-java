public class DataDirectoryS3Test extends DataDirectoryGenericTest {
    private static final String pathPrefix = "s3://algo-client-data-test/";  // This could be an environment variable or something

    @Override
    public String getFullPath(String thing) {
        return pathPrefix + thing;
    }
}