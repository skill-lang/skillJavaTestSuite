package creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.ust.skill.common.java.api.Access;
import de.ust.skill.common.java.api.SkillFile;
import de.ust.skill.common.java.internal.FieldDeclaration;
import de.ust.skill.common.java.internal.SkillObject;

public class SkillObjectCreator {

	/**
	 * Initialise a SKilLObject based on a JSON representation of it and its
	 * data
	 * 
	 * @param sf
	 *            SKilL file from which type and field definitions are loaded
	 * @param jsonObj
	 *            JSON representation of the object which should be initialised
	 * @return
	 */
	public static void generateSkillFileMappings(SkillFile sf,
			Map<String, Access<?>> types,
			Map<String, HashMap<String, FieldDeclaration<?, ?>>> typeFieldMapping) {
		
		// Iterate over all SkilL types present in the given SKilL file
		for (Access<?> currentType : sf.allTypes()) {
			types.put(currentType.name(), currentType);

			// Save all fields defined in this type into a map
			HashMap<String, FieldDeclaration<?, ?>> fieldMapping = new HashMap<String, FieldDeclaration<?, ?>>();
			Iterator<? extends de.ust.skill.common.java.api.FieldDeclaration<?>> iter = currentType.fields();
			while (iter.hasNext()) {
				FieldDeclaration<?, ?> fieldDeclaration = (FieldDeclaration<?, ?>) iter.next();
				fieldMapping.put(fieldDeclaration.name(), fieldDeclaration);
			}

			typeFieldMapping.put(currentType.name(), fieldMapping);
		}
	}

	/**
	 * Create an empty SkillObject from a fully specified class name
	 * 
	 * @param className
	 *            fully classified class name incl. package identifier
	 * @return empty SkillObject of the specified class
	 */
	public static SkillObject createSkillObject(String className)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> refClass = Class.forName(className);
		Constructor<?> refConstructor = refClass.getConstructor();
		SkillObject refVar = (SkillObject) refConstructor.newInstance();
		return refVar;
	}

	/**
	 * Create and fill a SkillObject with the provided values
	 * 
	 * @param className
	 *            fully specified class name incl. package identifier of the
	 *            object to be created
	 * @param values
	 *            mapping of field names to values
	 * @param fieldTypes
	 *            mapping of field names to the corresponding types of values
	 * @return a SkillObject with the provided values as attributes
	 */
	public static SkillObject instantiateSkillObject(String className, Map<String, Object> values,
			Map<String, String> fieldTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SkillObject obj = createSkillObject(className);
		Map<String, Type> fieldMapping = getFieldMapping(className);

		for (String key : fieldMapping.keySet()) {
			String type = fieldMapping.get(key).getTypeName();
			System.out.println("Present field: " + key + "(Type: " + type + ")");
			if (values.containsKey(key)) {
				System.out.println("Setting field '" + key + "' to be " + values.get(key));
				reflectiveSetValue(obj, values.get(key), key, fieldTypes.get(key));
			}
		}
		return obj;
	}

	/**
	 * Set the specified field of a SkillObject to a given value
	 * 
	 * @param obj
	 *            the SkillObject for which the field is to be set
	 * @param value
	 *            the value to set the field of the SkillObject to
	 * @param fieldName
	 *            the name of the field to be set
	 * @param fieldType
	 *            fully qualified class name of the field to be set
	 * @return the provided SkillObject with a set new value
	 */
	public static SkillObject reflectiveSetValue(SkillObject obj, Object value, String fieldName, String fieldType) {
		try {
			Method setterMethod = obj.getClass().getMethod(getSetterName(fieldName), getAptClass(fieldType));
			System.out.println("Found method " + setterMethod.getName());
			setterMethod.invoke(obj, value);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get a Class object from a fully qualified class name incl. package
	 * identifier
	 * 
	 * @param type
	 *            fully qualified class name
	 * @return Class object for the provided class name
	 * @throws ClassNotFoundException
	 */
	private static Class<?> getAptClass(String type) throws ClassNotFoundException {
		if (!SKilLType.isPrimitive(type)) {
			return Class.forName(type);
		} else {
			switch (type) {
			case "byte":
				return byte.class;
			case "short":
				return short.class;
			case "int":
				return int.class;
			case "long":
				return long.class;
			case "float":
				return float.class;
			case "double":
				return double.class;
			case "boolean":
				return boolean.class;
			case "char":
				return char.class;
			default:
				return null;
			}
		}
	}

	/**
	 * Parse a value encoded in a string to the specified type
	 * 
	 * @param type
	 * @param valueString
	 * @return instance of the actual class of the provided value
	 */
	public static Object valueOf(String type, String valueString) {
		switch (type) {
		case "byte":
			return Byte.valueOf(valueString);
		case "short":
			return Short.valueOf(valueString);
		case "int":
			return Integer.valueOf(valueString);
		case "long":
			return Long.valueOf(valueString);
		case "float":
			return Float.valueOf(valueString);
		case "double":
			return Double.valueOf(valueString);
		case "boolean":
			return Boolean.valueOf(valueString);
		case "char":
			return Character.valueOf(valueString.charAt(0));
		case "java.lang.String":
			return valueString;
		default:
			return null;
		}
	}

	/**
	 * Return the method name of the setter method responsible for the field
	 * with the name 'fieldName'.
	 * 
	 * @param fieldName
	 *            the name of the field for which to get the name of the setter
	 *            method
	 * @return the name of the setter method
	 */
	public static String getSetterName(String fieldName) {

		return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	/**
	 * Create a mapping of field names and their corresponding types of the
	 * given class.
	 * 
	 * @param className
	 *            the name of the class for which to generate the mapping
	 * @return a mapping of field names to Type objects
	 */
	public static Map<String, Type> getFieldMapping(String className) {
		Map<String, Type> fieldTypeMapping;
		try {
			Class<?> cls = Class.forName(className);
			Field[] declaredFields = cls.getDeclaredFields();
			fieldTypeMapping = new HashMap<>(declaredFields.length);
			for (Field field : declaredFields) {
				fieldTypeMapping.put(field.getName(), field.getGenericType());
			}
			return fieldTypeMapping;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create a mapping of field names and their corresponding types of the
	 * given class.
	 * 
	 * @param cls
	 *            the class for which to generate the mapping
	 * @return a mapping of field names to Type objects
	 */
	public static Map<String, Type> getFieldMapping(Class<?> cls) {
		Map<String, Type> fieldTypeMapping;
		Field[] declaredFields = cls.getDeclaredFields();
		fieldTypeMapping = new HashMap<>(declaredFields.length);
		for (Field field : declaredFields) {
			fieldTypeMapping.put(field.getName(), field.getGenericType());
		}
		return fieldTypeMapping;
	}
}