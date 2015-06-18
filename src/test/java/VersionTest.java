import com.algorithmia.algo.Version;

import org.junit.Test;
import org.junit.Assert;

public class VersionTest {

    @Test
    public void version() throws Exception {
        Assert.assertEquals("1.2.3", (new Version("1.2.3")).toString());
    }

    @Test
    public void versionRevision() throws Exception {
        Assert.assertEquals("1.2.3", Version.Revision(1L,2L,3L).toString());
    }

    @Test
    public void versionMinor() throws Exception {
        Assert.assertEquals("1.2", Version.Minor(1L,2L).toString());
    }

    @Test
    public void versionLatest() throws Exception {
        Assert.assertEquals("latest", Version.Latest().toString());
    }
}
