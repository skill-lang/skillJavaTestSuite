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
		
		try {				
			Class<?> refClass = Class.forName(className);
			Constructor<?> refConstructor = refClass.getConstructor();			
		    SkillObject refVar = (SkillObject) refConstructor.newInstance();
		    
		    Map<String, Type> fieldMapping = getFieldMapping(refClass);
			for(String key : fieldMapping.keySet()){
				String type = fieldMapping.get(key).getTypeName();
				System.out.println("Present field: " + key + "(Type: " + type + ")");
				if(key.equals(fieldToSet)){
					System.out.println("Setting field '" + key + "'");
					reflectiveSetValue(refVar, valueToSet , fieldToSet, fieldTypeToSet);
				}
			}
			
			System.out.println(refVar.prettyString());
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
	 * 
	 * @param obj
	 * @param value
	 * @param fieldName
	 * @param fieldType fully qualified class name of the field class
	 * @return
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
