package creator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import de.ust.skill.common.java.internal.SkillObject;

public class JSONReaderTest {

	/**
	 * Tests the object generation capabilities.
	 */
	@Test
	public void test() {
		Path path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources");
		path = path.resolve("values.json");
		
		try {
			JSONArray currentJSON = JSONReader.readJSON(path.toFile());
			for (int i = 0; i < currentJSON.length(); i++) {
				JSONObject currentTest = currentJSON.getJSONObject(i);
				SkillObject obj = JSONReader.createSkillObjectFromJSON(currentTest);
				System.out.println(obj.prettyString());
				assertTrue(true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (SecurityException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (InstantiationException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

}
