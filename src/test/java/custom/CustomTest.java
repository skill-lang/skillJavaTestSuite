
package custom;

import org.junit.Test;

/**
 * Tests the generated field.
 */
@SuppressWarnings("static-method")
public class CustomTest extends common.CommonTest {

	@Test
	public void test_custom_any_exists() throws Exception {
		new custom.Custom().any = new Object();
		// success if it compiles :)
	}

}
