import com.algorithmia.APIException;
import com.algorithmia.Algorithmia;
import com.algorithmia.data.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

public class DataDirectoryTest {

    private String key;

    @Before
    public void setup() {
        key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeNotNull(key);
    }

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
        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaDataDirCreate");

        // Make sure test starts in clean state
        if(dir.exists()) {
            dir.delete(true);
        }

        dir.create();
        Assert.assertTrue(dir.exists());
        dir.delete(false);
        Assert.assertFalse(dir.exists());
    }

    @Test
    public void dataDirList() throws Exception {
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

    private void dataDirListIterable(String dirName) throws Exception {
        DataDirectory dir = Algorithmia.client(key).dir(dirName);

        if(dir.exists()) {
            dir.delete(true);
        }

        dir.create();
        dir.file("foo").put("bar");
        dir.file("foo2").put("bar2");

        Set<String> filesFound = new HashSet<String>();
        int numFiles = 0;

        for(DataFile file : dir.files()) {
            numFiles++;
            Assert.assertTrue(filesFound.add(file.toString()));
        }

        Assert.assertEquals(2, numFiles);
        Assert.assertTrue(filesFound.contains("data://.my/javaDataDirList/foo"));
        Assert.assertTrue(filesFound.contains("data://.my/javaDataDirList/foo2"));
    }

    @Test
    public void dataDirListIterableWithTrailingSlash() throws Exception {
        dataDirListIterable("data://.my/javaDataDirList1/");
    }

    @Test
    public void dataDirListIterableWithoutTrailingSlash() throws Exception {
        dataDirListIterable("data://.my/javaDataDirList2");
    }

    @Test
    public void dataDirListWithPaging() throws Exception {
        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaLargeDataDirList1");
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
            String fileName = iter.next().getName();
            int endIndex = fileName.length() - EXTENSION.length();
            int index = Integer.parseInt(fileName.substring(0, endIndex));

            seenFiles[index] = true;
        }

        boolean allSeen = true;
        for (boolean cur : seenFiles) {
            allSeen = (allSeen && cur);
        }

        Assert.assertEquals(NUM_FILES, numFiles);
        Assert.assertTrue(allSeen);
    }

    @Test
    public void dataDirListWithPagingIterable() throws Exception {
        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaLargeDataDirList2");
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

        boolean[] seenFiles = new boolean[NUM_FILES];
        int numFiles = 0;

        for(DataFile file : dir.files()) {
            numFiles++;
            String fileName = file.getName();
            int endIndex = fileName.length() - EXTENSION.length();
            int index = Integer.parseInt(fileName.substring(0, endIndex));

            seenFiles[index] = true;
        }

        boolean allSeen = true;
        for (boolean cur : seenFiles) {
            allSeen = (allSeen && cur);
        }

        Assert.assertEquals(NUM_FILES, numFiles);
        Assert.assertTrue(allSeen);
    }

    @Test
    public void listingEmptyDirectory() throws Exception {
        DataDirectory dir = Algorithmia.client(key).dir("data://.my/test_empty_dir");
        if (dir.exists())
            dir.delete(true);

        dir.create();

        int dirCount = 0;
        for (DataDirectory childDir : dir.dirs()) {
            dirCount++;
        }
        Assert.assertEquals(0, dirCount);

        int fileCount = 0;
        for (DataFile childFile : dir.files()) {
            fileCount++;
        }
        Assert.assertEquals(0, fileCount);

        dir.delete(true);
    }

    @Test
    public void dataDirType() {
        DataDirectory dir = Algorithmia.client("").dir("data://.my/javaDataFileGet");
        Assert.assertTrue(dir.isDirectory());
        Assert.assertFalse(dir.isFile());
        Assert.assertEquals(DataObject.DataObjectType.DIRECTORY, dir.getType());
    }

    @Test
    public void dataDirGetPermissions() throws APIException {
        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaGetPermissions");
        if (dir.exists()) {
            dir.delete(true);
        }
        dir.create();
        Assert.assertEquals(DataAclType.MY_ALGOS, dir.getPermissions().getReadPermissions());
    }

    @Test
    public void dataDirCreateWithPermissions() throws APIException {
        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaCreateWithPermissions");
        if (dir.exists()) {
            dir.delete(true);
        }
        dir.create(DataAcl.PUBLIC);
        Assert.assertEquals(DataAclType.PUBLIC, dir.getPermissions().getReadPermissions());
    }

    @Test
    public void dataDirUpdatePermissions() throws APIException {
        DataDirectory dir = Algorithmia.client(key).dir("data://.my/javaUpdatePermissions");
        if (dir.exists()) {
            dir.delete(true);
        }
        dir.create(DataAcl.PUBLIC);
        Assert.assertEquals(DataAclType.PUBLIC, dir.getPermissions().getReadPermissions());

        dir.updatePermissions(DataAcl.PRIVATE);
        Assert.assertEquals(DataAclType.PRIVATE, dir.getPermissions().getReadPermissions());
    }
}
