import com.algorithmia.algo.Algorithmia;
import com.algorithmia.data.DataDirectory;
import com.algorithmia.data.DataFile;
import com.algorithmia.data.DataObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

import java.io.*;
import java.util.Scanner;

public abstract class DataFileGenericTest {
    public abstract String getFullPath(String thing);

    private String key;
    private File largeFile;

    @Before
    public void setup() throws Exception {
        key = System.getenv("ALGORITHMIA_DEFAULT_API_KEY");
        Assume.assumeNotNull(key);
        String osName = System.getProperty("os.name");
        String largeFileName;
        String userProfile = System.getenv("USERPROFILE");
        if (osName.contains("Win")) {
            largeFileName = userProfile + "\\AppData\\Local\\3GB";
        } else {
            largeFileName = "/tmp/3GB";
        }
        largeFile = new File(largeFileName);
        synchronized(this) {
            if (!largeFile.exists()) {
                RandomAccessFile tempFile = new RandomAccessFile(largeFile, "rw");
                tempFile.setLength(3221225472L);
            }
        }
        Assert.assertEquals(largeFile.length(), 3221225472L);
    }

    @Test
    public void dataFileParent() throws Exception {
        DataFile file = new DataFile(null, getFullPath("javaclienttest/foo"));
        DataDirectory parent = new DataDirectory(null, getFullPath("javaclienttest"));
        Assert.assertEquals(parent.path, file.getParent().path);
    }

    @Test
    public void dataFileName() throws Exception {
        DataDirectory file = new DataDirectory(null, getFullPath("javaDataFileName/foo"));
        String expected = "foo";
        Assert.assertEquals(expected, file.getName());
    }

    @Test
    public void dataFileCreate() throws Exception {
        DataFile file = Algorithmia.client(key).file(getFullPath("javaDataFileCreate/foo.txt"));


        // Make sure test starts in clean state
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        final File sampleFile = new File(this.getClass().getResource("sample.txt").getFile());

        file.put(sampleFile);
        Assert.assertTrue(file.exists());
        file.delete();
        Assert.assertFalse(file.exists());
    }

    @Test
    public void dataStringUpload() throws Exception {
        DataFile file = Algorithmia.client(key).file(getFullPath("javaDataFileUpload/foo.txt"));

        // Make sure test starts in clean state
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        // Write expected string to a local temp file
        String expected = "This is a cloud: \u2601"; //Unicode codepoint: U+2601

        file.put(expected);
        Assert.assertTrue(file.exists());
        Assert.assertEquals(expected, file.getString());
    }

    @Test
    public void dataFileUpload() throws Exception {
        DataFile file = Algorithmia.client(key).file(getFullPath("javaDataFileUpload/foo.txt"));

        // Make sure test starts in clean state
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        // Write expected string to a local temp file
        String expected = "This is a cloud: \u2601"; //Unicode codepoint: U+2601
        File temp = File.createTempFile("tempfile", ".tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(expected);
        bw.close();

        file.put(temp);
        Assert.assertTrue(file.exists());
        Assert.assertEquals(expected, file.getString());
    }

    @Test
    public void dataFileGet() throws Exception {
        DataFile file = Algorithmia.client(key).file(getFullPath("javaDataFileGet/foo.txt"));
        String expected = "Simple text file";

        // Make sure test starts in clean state
        if(!file.getParent().exists()) {
            file.getParent().create();
        }
        if(!file.exists()) {
            file.put(expected);
        }

        Assert.assertEquals(expected, file.getString());
        file.delete();
    }

    @Test
    public void putLargeFileGet() throws Exception {
        DataFile file = Algorithmia.client(key).file(getFullPath("largeFiles/3GB_file"));
        
        // Make sure test starts in clean state
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        if(!file.exists()) {
            file.put(largeFile);
        }
    }

    @Test
    public void putLargeFileInputStreamGet() throws Exception {
        DataFile file = Algorithmia.client(key).file(getFullPath("largeFiles/3GB_input_stream"));

        // Make sure test starts in clean state
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        if(!file.exists()) {
            file.put(new FileInputStream(largeFile));
        }
    }

    @Test
    public void getLargeFile() throws Exception {
        final int COUNT = 1000000;
        DataFile file = Algorithmia.client(key).file(getFullPath("largeFiles/" + COUNT + "Numbers"));
        File numbersFile = File.createTempFile("TestGetLargeFile", "Numbers");
        PrintStream ps = new PrintStream(numbersFile);
        for (int i = 0; i < COUNT; i ++) {
            ps.println(i);
        }

        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        if(!file.exists()) {
            file.put(numbersFile);
        }

        File downloaded = file.getFile();
        Assert.assertEquals(downloaded.length(), numbersFile.length());

        Scanner in = new Scanner(downloaded);
        int lines = 0;
        while (in.hasNextLine()) {
            Assert.assertEquals(lines, Integer.parseInt(in.nextLine()));
            lines++;
        }
        Assert.assertEquals(lines, COUNT);

        Assert.assertTrue(numbersFile.delete());
    }

    @Test
    public void dataFileType() {
        DataFile file = Algorithmia.client("").file(getFullPath("javaDataFileGet/foo.txt"));
        Assert.assertTrue(file.isFile());
        Assert.assertFalse(file.isDirectory());
        Assert.assertEquals(DataObject.DataObjectType.FILE, file.getType());
    }
}