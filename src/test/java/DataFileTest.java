import com.algorithmia.Algorithmia;
import com.algorithmia.data.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class DataFileTest {

    private String key;
    private File largeFile;

    @Before
    public void setup() throws Exception {
        key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeNotNull(key);

        String largeFileName = "/tmp/3GB";
        largeFile = new File(largeFileName);

        synchronized(this) {
            if (!largeFile.exists()) {
                ProcessBuilder procBuilder = new ProcessBuilder("dd", "if=/dev/zero", "of=" + largeFileName, "bs=1G", "count=3");
                Assert.assertEquals(procBuilder.start().waitFor(), 0);
            }
        }

        Assert.assertEquals(largeFile.length(), 3221225472L);
    }

    @Test
    public void dataFileParent() throws Exception {
        DataFile file = new DataFile(null, "data://.my/javaclienttest/foo");
        DataDirectory parent = new DataDirectory(null, "data://.my/javaclienttest");
        Assert.assertEquals(parent.path, file.getParent().path);
    }

    @Test
    public void dataFileName() throws Exception {
        DataDirectory file = new DataDirectory(null, "data://.my/javaDataFileName/foo");
        String expected = "foo";
        Assert.assertEquals(expected, file.getName());
    }

    @Test
    public void dataFileCreate() throws Exception {
        DataFile file = Algorithmia.client(key).file("data://.my/javaDataFileCreate/foo.txt");


        // Make sure test starts in clean state
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        final File sampleFile = new File(this.getClass().getResource("sample.txt").getFile());

        file.put(sampleFile);
        Assert.assertEquals(true, file.exists());
        file.delete();
        Assert.assertEquals(false, file.exists());
    }

    @Test
    public void dataStringUpload() throws Exception {
        DataFile file = Algorithmia.client(key).file("data://.my/javaDataFileUpload/foo.txt");

        // Make sure test starts in clean state
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        // Write expected string to a local temp file
        String expected = "This is a cloud: ☁"; //Unicode codepoint: U+2601

        file.put(expected);
        Assert.assertEquals(true, file.exists());
        Assert.assertEquals(expected, file.getString());
    }

    @Test
    public void dataFileUpload() throws Exception {
        DataFile file = Algorithmia.client(key).file("data://.my/javaDataFileUpload/foo.txt");

        // Make sure test starts in clean state
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        // Write expected string to a local temp file
        String expected = "This is a cloud: ☁"; //Unicode codepoint: U+2601
        File temp = File.createTempFile("tempfile", ".tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(expected);
        bw.close();

        file.put(temp);
        Assert.assertEquals(true, file.exists());
        Assert.assertEquals(expected, file.getString());
    }

    @Test
    public void dataFileGet() throws Exception {
        DataFile file = Algorithmia.client(key).file("data://.my/javaDataFileGet/foo.txt");
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
        DataFile file = Algorithmia.client(key).file("data://.my/largeFiles/3GB_file");

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
        DataFile file = Algorithmia.client(key).file("data://.my/largeFiles/3GB_input_stream");

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
        DataFile file = Algorithmia.client(key).file("data://.my/largeFiles/" + COUNT + "Numbers");
        File largeFile = File.createTempFile("TestGetLargeFile", "Numbers");
        PrintStream ps = new PrintStream(largeFile);
        for (int i = 0; i < COUNT; i ++) {
            ps.println(i);
        }

        if(!file.getParent().exists()) {
            file.getParent().create();
        }

        if(!file.exists()) {
            file.put(largeFile);
        }

        File downloaded = file.getFile();
        Assert.assertEquals(downloaded.length(), largeFile.length());

        Scanner in = new Scanner(downloaded);
        int lines = 0;
        while (in.hasNextLine()) {
            Assert.assertEquals(lines, Integer.parseInt(in.nextLine()));
            lines++;
        }
        Assert.assertEquals(lines, COUNT);
    }

    @Test
    public void dataFileType() {
        DataFile file = Algorithmia.client("").file("data://.my/javaDataFileGet/foo.txt");
        Assert.assertTrue(file.isFile());
        Assert.assertFalse(file.isDirectory());
        Assert.assertEquals(DataObject.DataObjectType.FILE, file.getType());
    }
}
