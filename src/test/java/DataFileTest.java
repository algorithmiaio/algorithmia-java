import com.algorithmia.Algorithmia;
import com.algorithmia.data.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

public class DataFileTest {

    private String key;

    @Before
    public void setup() {
        key = System.getenv("ALGORITHMIA_API_KEY");
        Assume.assumeNotNull(key);
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
            file.put(new File("/tmp/3GB"));
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
            file.put(new File("/tmp/3GB"));
        }
    }

    class LargeFileStream extends InputStream {
        public static final long totalSize = 5000000000L;

        private long left;
        public LargeFileStream() {
            left = totalSize;
        }

        @Override
        public int available() {
            return (int) left;
        }

        @Override
        public void close() {
            // do nothing
        }

        @Override
        public void mark(int readlimit) {
            throw new IllegalStateException();
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public int read() {
            return 0;
        }

        @Override
        public int read(byte[] b) {
            int result;
            if (left >= Integer.MAX_VALUE) {
                result = b.length;
            } else {
                result = Math.min(b.length, Math.toIntExact(left));
            }

            for (int i = 0; i < b.length; i++) {
                b[i] = 0;
            }

            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) {
            throw new IllegalStateException();
        }

        @Override
        public void reset() {
            throw new IllegalStateException();
        }

        @Override
        public long skip(long n) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void dataFileType() {
        DataFile file = Algorithmia.client("").file("data://.my/javaDataFileGet/foo.txt");
        Assert.assertTrue(file.isFile());
        Assert.assertFalse(file.isDirectory());
        Assert.assertEquals(DataObject.DataObjectType.FILE, file.getType());
    }
}
