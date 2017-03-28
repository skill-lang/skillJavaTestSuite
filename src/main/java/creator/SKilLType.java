package creator;

import java.util.HashMap;

public enum SKilLTypes {
	Bool("skill.lang.bool", "boolean"),
	Integer8("skill.lang.i8","byte"),
	Integer16("skill.lang.i16","short"),
	Integer32("skill.lang.i32","int"),
	Integer64("skill.lang.i64","long"),
	VariableInt64("skill.lang.v64","java.math.BigInteger"),
	Float32("skill.lang.f32","float"),
	Float64("skill.lang.f64","double"),
	Array("skill.lang.array","array"),
	List("skill.lang.list","java.util.ArrayList"),
	Set("skill.lang.set","java.util.HashSet"),
	Map("skill.lang.map","java.util.HashMap"),
	String("skill.lang.string","java.lang.String"),
	Annotation("skill.lang.annotation","annotation");

	private String skillType;
	private String javaType;
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
		typeMapping.put("skill.lang.array", "array");
		typeMapping.put("skill.lang.list", "java.util.ArrayList");
		typeMapping.put("skill.lang.set", "java.util.HashSet");
		typeMapping.put("skill.lang.map", "java.util.HashMap");
		typeMapping.put("skill.lang.string", "java.lang.String");
		typeMapping.put("skill.lang.annotation", "annotation");
	}

	SKilLTypes(String skillType, String javaType) {
		this.skillType = skillType;
		this.javaType = javaType;
	}
	
	public static String getJavaType(String skillType){
		return typeMapping.get(skillType);
	}
	
}
