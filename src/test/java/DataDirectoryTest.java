import com.algorithmia.algo.APIException;
import com.algorithmia.algo.Algorithmia;
import com.algorithmia.data.DataAcl;
import com.algorithmia.data.DataAclType;
import com.algorithmia.data.DataDirectory;

import org.junit.Test;
import org.junit.Assert;

public class DataDirectoryTest extends DataDirectoryGenericTest {

    private static final String pathPrefix = "data://.my/";

    @Override
    public String getFullPath(String thing) {
        return pathPrefix + thing;
    }

    @Test
    public void dataDirGetPermissions() throws APIException {
        DataDirectory dir = Algorithmia.client(defaultKey).dir("data://.my/javaGetPermissions");
        if (dir.exists()) {
            dir.delete(true);
        }
        dir.create();
        Assert.assertEquals(DataAclType.MY_ALGOS, dir.getPermissions().getReadPermissions());
    }

    @Test
    public void dataDirCreateWithPermissions() throws APIException {
        DataDirectory dir = Algorithmia.client(defaultKey).dir("data://.my/javaCreateWithPermissions");
        if (dir.exists()) {
            dir.delete(true);
        }
        dir.create(DataAcl.PUBLIC);
        Assert.assertEquals(DataAclType.PUBLIC, dir.getPermissions().getReadPermissions());
    }

    @Test
    public void dataDirUpdatePermissions() throws APIException {
        DataDirectory dir = Algorithmia.client(defaultKey).dir("data://.my/javaUpdatePermissions");
        if (dir.exists()) {
            dir.delete(true);
        }
        dir.create(DataAcl.PUBLIC);
        Assert.assertEquals(DataAclType.PUBLIC, dir.getPermissions().getReadPermissions());

        dir.updatePermissions(DataAcl.PRIVATE);
        Assert.assertEquals(DataAclType.PRIVATE, dir.getPermissions().getReadPermissions());
    }
}
