package creator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.ust.skill.common.java.internal.SkillObject;

public class JSONReader {

	private static final String STANDARD_CLASS_KEY = "ClassName";
	private static final String STANDARD_OBJECTNAME_KEY = "ObjectName";
	private static final String STANDARD_EXPECTED_ERROR_KEY = "ExpectedError";
	private static final String STANDARD_KEY_KEY = "Key";
	private static final String STANDARD_VALUE_KEY = "Value";
	private static final String STANDARD_VALUE_TYPE_KEY = "ValueType";
	private static final String STANDARD_KEY_TYPE_KEY = "KeyType";
	private static final String STANDARD_ENTRY_LIST_KEY = "Entries";

	public static void main(String[] args) {
		Path path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources");
		if (args.length == 0) {
			path = path.resolve("values.json");
		}
		try {
			JSONArray currentJSON = readJSON(path.toFile());
			for (int i = 0; i < currentJSON.length(); i++) {
				JSONObject currentTest = currentJSON.getJSONObject(i);
				System.out.println(currentTest.get(STANDARD_OBJECTNAME_KEY));
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
			if (attributeKey.equals(STANDARD_OBJECTNAME_KEY) 
					|| attributeKey.equalsIgnoreCase(STANDARD_CLASS_KEY)
					|| attributeKey.equalsIgnoreCase(STANDARD_EXPECTED_ERROR_KEY)) {
				continue;
			}
			String fieldName = attributeKey;
			JSONObject attributeValue = jsonObect.getJSONObject(attributeKey);
			String type = attributeValue.getString(STANDARD_CLASS_KEY);
			String parsedType = parseAttributeType(type);
			Object value = getJsonAttributeValue(attributeValue, parsedType);

			values.put(fieldName, value);
			fieldTypes.put(fieldName, parsedType);
		}
	}

	/**
	 * Get the attribute value of an JSON attribute. This method handles
	 * differences between primitive types, collections, maps and complex
	 * objects.
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
		if (SKilLType.isPrimitive(type)) {

			return SkillObjectCreator.valueOf(type, attributeValue.getString(STANDARD_VALUE_KEY));

		} else if (SKilLType.isCollection(type)) {

			JSONArray attributeArray = attributeValue.getJSONArray(STANDARD_VALUE_KEY);
			String valueType = attributeValue.getString(STANDARD_VALUE_TYPE_KEY);
			String parsedValueType = parseAttributeType(valueType);
			return parseJsonCollection(attributeArray, type, parsedValueType);

		} else if (SKilLType.isMap(type)) {
			return parseJsonMap(attributeValue);
		} else {

			return createSkillObjectFromJSON(attributeValue);
		}
	}

	/**
	 * Parse a JSON 'map' to a java map object.
	 * @param jsonMap
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws JSONException
	 */
	private static Map parseJsonMap(JSONObject jsonMap)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		HashMap resultMap = new HashMap<>();

		JSONArray mapEntries = jsonMap.getJSONArray(STANDARD_ENTRY_LIST_KEY);
		String parsedKeyType = parseAttributeType(jsonMap.getString(STANDARD_KEY_TYPE_KEY));
		String parsedValueType = parseAttributeType(jsonMap.getString(STANDARD_VALUE_TYPE_KEY));

		for (int i = 0; i < mapEntries.length(); i++) {
			JSONObject currentEntry = mapEntries.getJSONObject(i);

			if (SKilLType.isMap(parsedValueType)) { //Recursively handly map-type values
				Object key;
				Map value = parseJsonMap(currentEntry.getJSONObject(STANDARD_VALUE_KEY));
				
				// Handle primitive type keys
				if (SKilLType.isPrimitive(parsedKeyType)) {
					key = SkillObjectCreator.valueOf(parsedKeyType, currentEntry.getString(STANDARD_KEY_KEY));
				} else { // Create regular object as key
					key = createSkillObjectFromJSON(currentEntry.getJSONObject(STANDARD_KEY_KEY));
				}
				
				resultMap.put(key, value);
			} else { // Map has non-map object as value
				Object key;
				Object value;

				// Handle primitive type keys
				if (SKilLType.isPrimitive(parsedKeyType)) {
					key = SkillObjectCreator.valueOf(parsedKeyType, currentEntry.getString(STANDARD_KEY_KEY));
				} else { // Create regular object as key
					key = createSkillObjectFromJSON(currentEntry.getJSONObject(STANDARD_KEY_KEY));
				}

				// Handle primitive type values
				if (SKilLType.isPrimitive(parsedValueType)) {
					value = SkillObjectCreator.valueOf(parsedValueType, currentEntry.getString(STANDARD_VALUE_KEY));
				} else { // Create regular object as value
					value = createSkillObjectFromJSON(currentEntry.getJSONObject(STANDARD_VALUE_KEY));
				}

				resultMap.put(key, value);
			}
		}

		return resultMap;
	}

	/**
	 * Parse a collection from the given JSONArray
	 * 
	 * @param attributeArray
	 *            The JSONArray to be parsed
	 * @param collectionType
	 *            The collections type e.g. "java.util.ArrayList"
	 * @param valueType
	 *            The type of the collections values
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws JSONException
	 */
	private static Collection<?> parseJsonCollection(JSONArray attributeArray, String collectionType, String valueType)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		Collection collection;

		switch (collectionType) {
		case "java.util.ArrayList":
			collection = new ArrayList<>();
			break;
		case "java.util.LinkedList":
			collection = new LinkedList<>();
			break;
		case "java.util.HashSet":
			collection = new HashSet<>();
			break;
		default:
			throw new IllegalArgumentException("Unsupported collection type");
		}
		if (SKilLType.isPrimitive(valueType)) {
			for (int i = 0; i < attributeArray.length(); i++) {
				collection.add(SkillObjectCreator.valueOf(valueType, attributeArray.getString(i)));
			}
		} else {
			for (int i = 0; i < attributeArray.length(); i++) {
				collection.add(createSkillObjectFromJSON(attributeArray.getJSONObject(i)));
			}
		}

		return collection;
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

	private static String parseAttributeType(String type) {
		String parsedType = SKilLType.getJavaType(type);
		return (parsedType == null) ? type : parsedType;
	}
}
