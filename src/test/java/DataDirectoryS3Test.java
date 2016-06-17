public class DataDirectoryS3Test extends DataDirectoryGenericTest {
    private static final String pathPrefix = "s3://james-what/";  // This could be an environment variable or something

    @Override
    public String getFullPath(String thing) {
        return pathPrefix + thing;
    }
}