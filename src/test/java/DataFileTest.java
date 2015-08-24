import com.algorithmia.Algorithmia;
import com.algorithmia.data.*;

import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class DataFileTest {


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
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

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
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

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
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

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
        final String key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeTrue(key != null);

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


}
