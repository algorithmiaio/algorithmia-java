public class DataDirectoryDropboxTest extends DataDirectoryGenericTest {
    private static final String pathPrefix = "dropbox://test/";

    @Override
    public String getFullPath(String thing) {
        return pathPrefix + thing;
    }
}