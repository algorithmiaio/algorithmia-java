
import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.Assert;

public class TestCaseBuildFailure {
    @Test
    public void failTestCase(){
        int a=0;
        int b=12;
        Assert.assertEquals(b/a, 12);
    }
}
