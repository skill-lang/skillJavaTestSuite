package creator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.ust.skill.common.java.internal.SkillObject;

public class JSONReader {

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

	/**
	 * Create a SKilL object from the provided JSON object.
	 * 
	 * @param jsonObject
	 *            a JSON object containing data to be used as attribute values
	 *            for the SKilL object
	 * @return A SKilL representation of the data contained by the provided JSON
	 *         object
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static SkillObject createSkillObjectFromJSON(JSONObject jsonObject)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> values = new HashMap<>();
		Map<String, String> fieldTypes = new HashMap<>();

		String className = jsonObject.getString(STANDARD_CLASS_KEY);

		createMappingFromJSONObject(jsonObject, values, fieldTypes);
		return SkillObjectCreator.instantiateSkillObject(className, values, fieldTypes);
	}

	/**
	 * Create mappings of field names to values and field types for a given
	 * JSONObject object.
	 * 
	 * @param jsonObect
	 *            JSON object to create the mapping from
	 * @param values
	 *            an empty Map for field name to value mappings to be filled by
	 *            this method
	 * @param fieldTypes
	 *            an empty Map for field name to type mappings to be filled by
	 *            this method
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static void createMappingFromJSONObject(JSONObject jsonObect, Map<String, Object> values,
			Map<String, String> fieldTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (String attributeKey : jsonObect.keySet()) {
			if (attributeKey.equals(STANDARD_OBJECTNAME) || attributeKey.equalsIgnoreCase(STANDARD_CLASS_KEY)) {
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

	/**
	 * Get the attribute value of an JSON attribute. This method handles
	 * differences between primitive types and complex objects.
	 * 
	 * @param attributeValue
	 *            attribute of a JSON object
	 * @param type
	 *            fully qualified class name of the attribute provided as
	 *            'attributeValue'
	 * @return the value of the provided attribute
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static Object getJsonAttributeValue(JSONObject attributeValue, String type) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if (SkillObjectCreator.isPrimitive(type)) {
			return SkillObjectCreator.valueOf(attributeValue.getString(STANDARD_CLASS_KEY),
					attributeValue.getString(STANDARD_PRIMITIVE_VALUE_KEY));
		} else {

			return createSkillObjectFromJSON(attributeValue);
		}
	}

	/**
	 * Read list of JSONs from text file and parse it to a JSONArray object
	 * 
	 * @param file
	 *            the file to read from
	 * @return JSONArray
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static JSONArray readJSON(File file) throws JSONException, MalformedURLException, IOException {
		JSONTokener fileTokens = new JSONTokener(new java.io.FileInputStream(file));
		JSONArray content = new JSONArray(fileTokens);
		return content;

	}
}
