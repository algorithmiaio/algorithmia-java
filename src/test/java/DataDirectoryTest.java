import algorithmia.Algorithmia;
import algorithmia.data.*;

import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;
import java.util.List;
import java.util.Arrays;

public class DataDirectoryTest {


    @Test
    public void dataDirParent() throws Exception {
        DataDirectory dir = new DataDirectory(null, "/me/javaclienttest");
        DataDirectory parent = new DataDirectory(null, "/me");
        Assert.assertEquals(parent.path, dir.getParent().path);
    }

    @Test
    public void dataDirName() throws Exception {
        DataDirectory dir = new DataDirectory(null, "/me/javaclienttest");
        String expected = "javaclienttest";
        Assert.assertEquals(expected, dir.getName());
    }

    @Test
    public void dataDirCreate() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        Algorithmia algorithmia = new Algorithmia(key);
        DataDirectory dir = algorithmia.dir("/me/javaDataDirCreate");

        // Make sure test starts in clean state
        if(dir.exists()) {
            dir.delete(true);
        }

        dir.create();
        Assert.assertEquals(true, dir.exists());
        dir.delete(false);
        Assert.assertEquals(false, dir.exists());
    }

    @Test
    public void dataDirList() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        Algorithmia algorithmia = new Algorithmia(key);
        DataDirectory dir = algorithmia.dir("/me/javaDataDirList");

        if(dir.exists()) {
            dir.delete(true);
        }

        dir.create();
        dir.file("foo").put("bar");
        dir.file("foo2").put("bar2");
        DataFileIterator iter = dir.getFileIter();

        Assert.assertEquals("foo", iter.next().getName());
        Assert.assertEquals("foo2", iter.next().getName());
    }

}
