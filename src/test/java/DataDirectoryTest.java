import com.algorithmia.APIException;
import com.algorithmia.Algorithmia;
import com.algorithmia.data.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

public class DataDirectoryTest extends DataDirectoryGenericTest {

    private static final String pathPrefix = "data://.my/";

    @Override
    public String getFullPath(String thing) {
        return pathPrefix + thing;
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
