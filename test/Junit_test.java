import org.junit.*;
import static org.junit.Assert.*;

public class Junit_test {
    @Test
    public void testNothing() {
    }

    @Test
    public void testWillAlwaysFail() {
        assertEquals(0,1);
    }

    @Test
    public void testWillAlwaysPass() {
        assertEquals(42,42);
    }
}
