package age;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.ust.skill.common.java.internal.SkillObject;

public class DummyTest {
	
	private static final String FIELD_DECLARATION_CLASS_NAME = "de.ust.skill.common.java.api.FieldDeclaration";

	public static void main(String[] args){
		String className = "age.Age";
		String fieldToSet = "age";
		String fieldTypeToSet = "long";
		Long valueToSet = 255L;
		Map<String, Object> values = new HashMap<>();
		Map<String, String> fieldTypes = new HashMap<>();
		
		values.put(fieldToSet, valueToSet);
		fieldTypes.put(fieldToSet, fieldTypeToSet);
		
		try {				
			SkillObject obj = instantiateSkillObject(className, values, fieldTypes);
			
			System.out.println(obj.prettyString());
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create an empty SkillObject from a fully specified class name
	 * @param className fully classified class name incl. package identifier
	 * @return empty SkillObject of the specified class
	 */
	public static SkillObject createSkillObject(String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class<?> refClass = Class.forName(className);
		Constructor<?> refConstructor = refClass.getConstructor();			
	    SkillObject refVar = (SkillObject) refConstructor.newInstance();
		return refVar;	
	}
	
	/**
	 * Create and fill a SkillObject with the provided values
	 * @param className fully specified class name incl. package identifier of the object to be created
	 * @param values mapping of field names to values 
	 * @param fieldTypes mapping of field names to the corresponding types of values
	 * @return a SkillObject with the provided values as attributes
	 */
	public static SkillObject instantiateSkillObject(String className, 
			Map<String, Object> values, 
			Map<String, String> fieldTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		SkillObject obj = createSkillObject(className);
		Map<String, Type> fieldMapping = getFieldMapping(className);
		
		for(String key : fieldMapping.keySet()){
			String type = fieldMapping.get(key).getTypeName();
			System.out.println("Present field: " + key + "(Type: " + type + ")");
			if(values.containsKey(key)){
				System.out.println("Setting field '" + key + "'");
				reflectiveSetValue(obj, values.get(key) , key, fieldTypes.get(key));
			}
		}
		return obj;
	}
	
	/**
	 * Set the specified field of a SkillObject to a given value
	 * @param obj the SkillObject for which the field is to be set
	 * @param value the value to set the field of the SkillObject to
	 * @param fieldName the name of the field to be set
	 * @param fieldType fully qualified class name of the field to be set
	 * @return the provided SkillObject with a set new value
	 */
	public static SkillObject reflectiveSetValue(SkillObject obj, Object value, String fieldName, String fieldType){
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
	 * Get a Class object from a fully qualified class name incl. package identifier
	 * @param type fully qualified class name
	 * @return Class object for the provided class name
	 * @throws ClassNotFoundException
	 */
	private static Class<?> getAptClass(String type) throws ClassNotFoundException {
		if(!isPrimitive(type)){
			return Class.forName(type);
		}else{
			switch(type){
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
	 * Checks whether a provided class name is a primitive type
	 * @param type the name of the class to be checked
	 * @return true, if type is a primitive type. False, otherwise.
	 */
	private static boolean isPrimitive(String type) {
		String[] primitiveTypes = {"byte", "short", "int", "long", "float", "double", "boolean", "char"};
		return Arrays.asList(primitiveTypes).contains(type);
	}

	/**
	 * Return the method name of the setter method responsible for the field
	 * with the name 'fieldName'.
	 * 
	 * @param fieldName the name of the field for which to get the name of the setter method
	 * @return the name of the setter method
	 */
	public static String getSetterName(String fieldName){
		return "setAge";
	}
	
	/**
	 * Create a mapping of field names and their corresponding types of the given class.
	 * @param className the name of the class for which to generate the mapping
	 * @return a mapping of field names to Type objects
	 */
	public static Map<String, Type> getFieldMapping(String className){
		Map<String, Type> fieldTypeMapping;
		try {
			Class<?> cls = Class.forName(className);
			Field[] declaredFields = cls.getDeclaredFields();
			fieldTypeMapping = new HashMap<>(declaredFields.length);
			for(Field field : declaredFields){
				fieldTypeMapping.put(field.getName(), field.getGenericType());
			}
			return fieldTypeMapping;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Create a mapping of field names and their corresponding types of the given class.
	 * @param cls the class for which to generate the mapping
	 * @return a mapping of field names to Type objects
	 */
	public static Map<String, Type> getFieldMapping(Class<?> cls){
		Map<String, Type> fieldTypeMapping;
		Field[] declaredFields = cls.getDeclaredFields();
		fieldTypeMapping = new HashMap<>(declaredFields.length);
		for(Field field : declaredFields){
			fieldTypeMapping.put(field.getName(), field.getGenericType());
		}
		return fieldTypeMapping;
	}	
}
