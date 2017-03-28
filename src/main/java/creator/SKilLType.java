package creator;

import java.util.HashMap;

public enum SKilLType {
	Bool("skill.lang.bool"),
	Integer8("skill.lang.i8"),
	Integer16("skill.lang.i16"),
	Integer32("skill.lang.i32"),
	Integer64("skill.lang.i64"),
	VariableInt64("skill.lang.v64"),
	Float32("skill.lang.f32"),
	Float64("skill.lang.f64"),
	Array("skill.lang.array"),
	List("skill.lang.list"),
	Set("skill.lang.set"),
	Map("skill.lang.map"),
	String("skill.lang.string"),
	Annotation("skill.lang.annotation");

	private String skillType;
	private static HashMap<String, String> typeMapping;
	static {
		typeMapping.put("skill.lang.bool", "boolean");
		typeMapping.put("skill.lang.i8", "byte");
		typeMapping.put("skill.lang.i16", "short");
		typeMapping.put("skill.lang.i32", "int");
		typeMapping.put("skill.lang.i64", "long");
		typeMapping.put("skill.lang.v64", "java.math.BigInteger");
		typeMapping.put("skill.lang.f32", "float");
		typeMapping.put("skill.lang.f64", "double");
		typeMapping.put("skill.lang.array", "java.util.ArrayList");
		typeMapping.put("skill.lang.list", "java.util.LinkedList");
		typeMapping.put("skill.lang.set", "java.util.HashSet");
		typeMapping.put("skill.lang.map", "java.util.HashMap");
		typeMapping.put("skill.lang.string", "java.lang.String");
		typeMapping.put("skill.lang.annotation", "java.lang.Object");
	}

	SKilLType(String skillType) {
		this.skillType = skillType;
	}
	
	public static boolean isCollection(String skillType){
		String mappedValue = typeMapping.get(skillType);
		if(mappedValue == null){
			return false;
		}else{
			return mappedValue.equals("java.util.ArrayList")
					|| mappedValue.equals("java.util.LinkedList")
					|| mappedValue.equals("java.util.HashSet")
					|| mappedValue.equals("java.util.HashMap");
		}
	}
	
	public static String getJavaType(String skillType){
		return typeMapping.get(skillType);
	}
	
	public static SKilLType fromString(String type){
		for(SKilLType currentType : SKilLType.values()){
			if(currentType.skillType.equals(type)){
				return currentType;
			}
		}
		return null;
	}
	
}
