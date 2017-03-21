package creator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

import de.ust.skill.common.java.internal.SkillObject;
import de.ust.skill.common.jvm.streams.FileInputStream;

public class JSONReader {

	private static final int CLASS_INDEX = 0;
	private static final int FIELD_NAME_SUBINDEX = 0;
	private static final int TYPE_SUBINDEX = 1;
	private static final int VALUE_SUBINDEX = 2;
	private static final String STANDARD_CLASS_KEY = "ClassName";
	private static final String STANDARD_OBJECTNAME = "ObjectName";
	private static final String STANDARD_PRIMITIVE_VALUE_KEY = "Value";

	public static void main(String[] args) {
		Path path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources");
		if (args.length == 0) {
			path = path.resolve("values.json");
		}
		try {
			JSONArray currentJSON = readJSON(path.toFile());
			for (int i = 0; i < currentJSON.length(); i++) {
				JSONObject currentTest = currentJSON.getJSONObject(i);
				System.out.println(currentTest.get(STANDARD_OBJECTNAME));
				SkillObject obj = createSkillObjectFromJSON(currentTest);
				System.out.println(obj.prettyString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static SkillObject createSkillObjectFromJSON(JSONObject jsonObject)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> values = new HashMap<>();
		Map<String, String> fieldTypes = new HashMap<>();

		String className = jsonObject.getString(STANDARD_CLASS_KEY);

		createMappingFromJSONObject(jsonObject, values, fieldTypes);
		return SkillObjectCreator.instantiateSkillObject(className, values, fieldTypes);
	}

	public static void createMappingFromJSONObject(JSONObject jsonObect, Map<String, Object> values,
			Map<String, String> fieldTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (String attributeKey : jsonObect.keySet()) {
			if (attributeKey.equals(STANDARD_OBJECTNAME) 
					|| attributeKey.equalsIgnoreCase(STANDARD_CLASS_KEY)) {
				continue;
			}
			String fieldName = attributeKey;
			JSONObject attributeValue = jsonObect.getJSONObject(attributeKey);
			String type = attributeValue.getString(STANDARD_CLASS_KEY);
			Object value = getJsonAttributeValue(attributeValue, type);

			values.put(fieldName, value);
			fieldTypes.put(fieldName, type);
		}
	}

	private static Object getJsonAttributeValue(JSONObject attributeValue, String type) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if (SkillObjectCreator.isPrimitive(type)) {
			return SkillObjectCreator.valueOf(attributeValue.getString(STANDARD_CLASS_KEY),
					attributeValue.getString(STANDARD_PRIMITIVE_VALUE_KEY));
		} else {

			return createSkillObjectFromJSON(attributeValue);
		}
	}

	public static JSONArray readJSON(File file) throws JSONException, MalformedURLException, IOException {
		JSONTokener fileTokens = new JSONTokener(new java.io.FileInputStream(file));
		JSONArray content = new JSONArray(fileTokens);
		return content;

	}
}
