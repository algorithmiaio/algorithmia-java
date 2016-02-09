import com.algorithmia.Algorithmia;
import com.algorithmia.data.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

public class DataDirectoryTest {


    @Test
    public void dataDirParent() throws Exception {
        DataDirectory dir = new DataDirectory(null, "data://.my/javaclienttest");
        DataDirectory parent = new DataDirectory(null, "data://.my");
        Assert.assertEquals(parent.path, dir.getParent().path);
    }

    @Test
    public void dataDirName() throws Exception {
        DataDirectory dir = new DataDirectory(null, "data://.my/javaclienttest");
        String expected = "javaclienttest";
        Assert.assertEquals(expected, dir.getName());
    }

    @Test
    public void dataDirCreate() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaDataDirCreate");

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

        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaDataDirList");

        if(dir.exists()) {
            dir.delete(true);
        }

        dir.create();
        dir.file("foo").put("bar");
        dir.file("foo2").put("bar2");
        DataFileIterator iter = dir.getFileIter();

        Set<String> filesFound = new HashSet<String>();
        int numFiles = 0;

        while (iter.hasNext()) {
            numFiles++;
            Assert.assertTrue(filesFound.add(iter.next().toString()));
        }

        Assert.assertEquals(2, numFiles);
        Assert.assertTrue(filesFound.contains("data://.my/javaDataDirList/foo"));
        Assert.assertTrue(filesFound.contains("data://.my/javaDataDirList/foo2"));
    }

    @Test
    public void dataDirListWithPaging() throws Exception {
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaLargeDataDirList");
        final int NUM_FILES = 1100;
        final String EXTENSION = ".txt";

        // Since this test uploads a lot of files to the server, we want to recreate
        // this directory only when it does not already exist.
        if(!dir.exists()) {
            dir.create();

            for (int i = 0; i < NUM_FILES; i++) {
                dir.file(i + EXTENSION).put(i + "");
            }
        }

        DataFileIterator iter = dir.getFileIter();

        boolean[] seenFiles = new boolean[NUM_FILES];
        int numFiles = 0;

        while (iter.hasNext()) {
            numFiles++;
            String fileName = iter.next().toString();
            int startIndex = fileName.lastIndexOf('/') + 1;
            int endIndex = fileName.length() - EXTENSION.length();
            int index = Integer.parseInt(fileName.substring(startIndex, endIndex));

            seenFiles[index] = true;
        }

        boolean allSeen = true;
        for (boolean cur : seenFiles) {
            allSeen = (allSeen && cur);
        }

        Assert.assertEquals(NUM_FILES, numFiles);
        Assert.assertTrue(allSeen);
    }
}
