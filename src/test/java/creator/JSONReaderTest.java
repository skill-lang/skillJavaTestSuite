package creator;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import age.api.SkillFile;
import de.ust.skill.common.java.api.Access;
import de.ust.skill.common.java.api.SkillException;
import de.ust.skill.common.java.internal.FieldDeclaration;
import de.ust.skill.common.java.internal.SkillObject;

public class JSONReaderTest extends common.CommonTest{

	@Rule // http://stackoverflow.com/a/2935935
	public final ExpectedException exception = ExpectedException.none();

	private static Path path;
	private static Path skillFilePath;
	private JSONObject currentJSON;

	/**
	 * Tests the object generation capabilities.
	 */
	@BeforeClass
	public static void init() throws JSONException, MalformedURLException, IOException {
		path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test4.json");
		skillFilePath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "age-example.sf");
	}

	@Before
	public void loadNextJSONObject() throws JSONException, MalformedURLException, IOException {
		this.currentJSON = JSONReader.readJSON(path.toFile());
	}

	public void jsonTest() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JSONObject currentTest = currentJSON;
		if(JSONReader.shouldExpectException(currentTest)){
			System.out.println("There should be an exception coming up!");
			exception.expect(Exception.class);
		}
		SkillObject obj = JSONReader.createSkillObjectFromJSON(currentTest);
		System.out.println(obj.prettyString(null));
		assertTrue(true);
	}
	
	@Test
	public void test() throws SkillException, IOException{
        Map<String, Access<?>> types = new HashMap<>();
		Map<String, HashMap<String, FieldDeclaration<?, ?>>> typeFieldMapping = new HashMap<>();
		
		SkillFile sf = SkillFile.open(skillFilePath);
        reflectiveInit(sf);
        
		creator.SkillObjectCreator.generateSkillFileMappings(sf, types, typeFieldMapping);
		
		//Create necessary objects
		SkillObject jsonObjName1 = types.get("Typename").make();
		SkillObject jsonObjName2 = types.get("Typename").make();
		
		jsonObjName1.set(cast(typeFieldMapping.get("Typename").get("Fieldname")), "Value");
		
		sf.close();
	}
	
	protected static <T, U> de.ust.skill.common.java.api.FieldDeclaration<T> cast(de.ust.skill.common.java.api.FieldDeclaration<U> arg){
		return (de.ust.skill.common.java.api.FieldDeclaration<T>)arg;
	}
	
}
