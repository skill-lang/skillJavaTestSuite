package common;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;

import de.ust.skill.common.java.api.Access;
import de.ust.skill.common.java.api.FieldDeclaration;
import de.ust.skill.common.java.api.FieldType;
import de.ust.skill.common.java.api.GeneralAccess;
import de.ust.skill.common.java.api.SkillException;
import de.ust.skill.common.java.api.SkillFile;
import de.ust.skill.common.java.internal.SkillObject;
import de.ust.skill.common.java.internal.fieldDeclarations.AutoField;
import de.ust.skill.common.java.internal.fieldDeclarations.InterfaceField;
import de.ust.skill.common.java.internal.fieldTypes.ConstantIntegerType;
import de.ust.skill.common.java.internal.fieldTypes.ConstantLengthArray;
import de.ust.skill.common.java.internal.fieldTypes.SingleArgumentType;

/**
 * Some test code commonly used by all tests.
 * 
 * @author Timm Felden
 */
@Ignore
abstract public class CommonTest {

	/**
	 * This constant is used to guide reflective init
	 */
	private static final int reflectiveInitSize = 10;

	public CommonTest() {
		super();
	}
	
	
	protected static Path createFile(String packagePath,String name) throws Exception {
		File dir = new File("src/test/resources/serializedTestfiles/" + packagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return new File("src/test/resources/serializedTestfiles/" + name + ".sf").toPath();
	}

	/**
	 * TODO move to common tests
	 */
	protected static Path tmpFile(String string) throws Exception {
		File r = File.createTempFile(string, ".sf");
		// r.deleteOnExit();
		return r.toPath();
	}

	/**
	 * TODO move to common tests
	 */
	protected final static String sha256(String name) throws Exception {
		return sha256(new File("src/test/resources/" + name).toPath());
	}

	/**
	 * TODO move to common tests
	 */
	protected final static String sha256(Path path) throws Exception {
		byte[] bytes = Files.readAllBytes(path);
		StringBuilder sb = new StringBuilder();
		for (byte b : MessageDigest.getInstance("SHA-256").digest(bytes))
			sb.append(String.format("%02X", b));
		return sb.toString();
	}

	protected static void reflectiveInit(SkillFile sf) {
		// create instances
		sf.allTypesStream().parallel().forEach(t -> {
			try {
				for (int i = reflectiveInitSize; i != 0; i--)
					t.make();
			} catch (SkillException e) {
				// the type can not have more instances
			}
		});

		// set fields
		sf.allTypesStream().parallel().forEach(t -> {
			for (SkillObject o : t) {
				Iterator<? extends FieldDeclaration<?>> it = t.fields();
				while (it.hasNext()) {
					final FieldDeclaration<?> f = it.next();
					if (!(f instanceof AutoField) && !(f.type() instanceof ConstantIntegerType<?>)
							&& !(f instanceof InterfaceField))
						set(sf, o, f);
				}
			}
		});
	}

	private static <T, Obj extends SkillObject> void set(SkillFile sf, Obj o, FieldDeclaration<T> f) {
		T v = value(sf, f.type());
		// System.out.printf("%s#%d.%s = %s\n", o.getClass().getName(),
		// o.getSkillID(), f.name(), v.toString());
		o.set(f, v);
	}

	/**
	 * unchecked, because the insane amount of casts is necessary to reflect the
	 * implicit value based type system
	 */
	@SuppressWarnings("unchecked")
	private static <T> T value(SkillFile sf, FieldType<T> type) {
		if (type instanceof GeneralAccess<?>) {
			// get a random object
			Iterator<T> is = (Iterator<T>) ((GeneralAccess<?>) type).iterator();
			for (int i = ThreadLocalRandom.current().nextInt(reflectiveInitSize) % 200; i != 0; i--)
				is.next();
			return is.next();
		}

		switch (type.typeID()) {
		case 5:
			// random type
			Iterator<? extends Access<? extends SkillObject>> ts = sf.allTypes().iterator();
			Access<? extends SkillObject> t = ts.next();
			for (int i = ThreadLocalRandom.current().nextInt(200); i != 0 && ts.hasNext(); i--)
				t = ts.next();

			// random object
			Iterator<? extends SkillObject> is = t.iterator();
			for (int i = ThreadLocalRandom.current().nextInt(Math.min(200, reflectiveInitSize)); i != 0; i--)
				is.next();
			return (T) is.next();

		case 6:
			return (T) (Boolean) ThreadLocalRandom.current().nextBoolean();
		case 7:
			return (T) (Byte) (byte) ThreadLocalRandom.current().nextInt(reflectiveInitSize);
		case 8:
			return (T) (Short) (short) ThreadLocalRandom.current().nextInt(reflectiveInitSize);
		case 9:
			return (T) (Integer) ThreadLocalRandom.current().nextInt(reflectiveInitSize);
		case 10:
		case 11:
			return (T) (Long) (ThreadLocalRandom.current().nextLong() % reflectiveInitSize);
		case 12:
			return (T) (Float) ThreadLocalRandom.current().nextFloat();
		case 13:
			return (T) (Double) ThreadLocalRandom.current().nextDouble();
		case 14:
			return (T) "☢☢☢";

		case 15: {
			ConstantLengthArray<T> cla = (ConstantLengthArray<T>) type;
			ArrayList<Object> rval = new ArrayList<>((int) cla.length);
			for (int i = (int) cla.length; i != 0; i--)
				rval.add(value(sf, cla.groundType));
			return (T) rval;
		}
		case 17: {
			SingleArgumentType<?, ?> cla = (SingleArgumentType<?, ?>) type;
			int length = (int) Math.sqrt(reflectiveInitSize);
			ArrayList<Object> rval = new ArrayList<>(length);
			while (0 != length--)
				rval.add(value(sf, cla.groundType));
			return (T) rval;
		}
		case 18: {
			SingleArgumentType<?, ?> cla = (SingleArgumentType<?, ?>) type;
			int length = (int) Math.sqrt(reflectiveInitSize);
			LinkedList<Object> rval = new LinkedList<>();
			while (0 != length--)
				rval.add(value(sf, cla.groundType));
			return (T) rval;
		}
		case 19: {
			SingleArgumentType<?, ?> cla = (SingleArgumentType<?, ?>) type;
			int length = (int) Math.sqrt(reflectiveInitSize);
			HashSet<Object> rval = new HashSet<>();
			while (0 != length--)
				rval.add(value(sf, cla.groundType));
			return (T) rval;
		}
		case 20:
			return (T) new HashMap<Object, Object>();
		default:
			throw new IllegalStateException();
		}
	}

	protected static <T, U> de.ust.skill.common.java.api.FieldDeclaration<T> cast(
			de.ust.skill.common.java.api.FieldDeclaration<U> arg) {
		return (de.ust.skill.common.java.api.FieldDeclaration<T>) arg;
	}

	protected static String getProperCollectionType(String type) {
		if (type.contains("list")) {
			return "java.util.LinkedList";
		} else if (type.contains("set")) {
			return "java.util.HashSet";
		} else if (type.contains("[]")) {
			return "java.util.ArrayList";
		} else {
			throw new IllegalArgumentException("Could not parse provided SKilL collection type.\n" + "Type was: " + type
					+ "\n" + "Expected one of { 'list', 'set', 'array' }");
		}
	}

	protected static Object wrapPrimitveTypes(long value,
			de.ust.skill.common.java.api.FieldDeclaration<?> declaration) {

		if (declaration.toString().contains("i8")) {
			if (Math.abs(value) > Byte.MAX_VALUE) {
				return value;
			} else {
				return new Byte((byte) value);
			}
		} else if (declaration.toString().contains("i16")) {
			if (Math.abs(value) > Short.MAX_VALUE) {
				return value;
			} else {
				return new Short((short) value);
			}
		} else if (declaration.toString().contains("i32")) {
			if (Math.abs(value) > Integer.MAX_VALUE) {
				return value;
			} else {
				return new Integer((int) value);
			}
		} else if (declaration.toString().contains("i64") || declaration.toString().contains("v64")) {
			return new Long(value);
		} else {
			throw new IllegalArgumentException(
					"The given fieldDeclaration is not supported.\n" + "Declaration was: " + declaration.toString()
							+ "\n" + "But should contain one of the following : {'i8','i16','i32','i64'}");
		}
	}

	protected static Object wrapPrimitveTypes(double value,
			de.ust.skill.common.java.api.FieldDeclaration<?> declaration) {

		if (declaration.toString().contains("f32")) {
			if (Math.abs(value) > Float.MAX_VALUE) {
				return value;
			} else {
				return new Float((float) value);
			}
		} else if (declaration.toString().contains("f64")) {
			return new Double(value);
		}
		if (declaration.toString().contains("i8")) {
			if (Math.abs(value) > Byte.MAX_VALUE) {
				return value;
			} else {
				return new Byte((byte) value);
			}
		} else if (declaration.toString().contains("i16")) {
			if (Math.abs(value) > Short.MAX_VALUE) {
				return value;
			} else {
				return new Short((short) value);
			}
		} else if (declaration.toString().contains("i32")) {
			if (Math.abs(value) > Integer.MAX_VALUE) {
				return value;
			} else {
				return new Integer((int) value);
			}
		} else if (declaration.toString().contains("i64") || declaration.toString().contains("v64")) {
			if (Math.abs(value) > Long.MAX_VALUE) {
				return value;
			} else {
				return new Long((long) value);
			}
		} else {
			throw new IllegalArgumentException(
					"The given fieldDeclaration is not supported.\n" + "Declaration was: " + declaration.toString()
							+ "\n" + "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64'}");
		}
	}

	protected static Object wrapPrimitveMapTypes(String value,
			de.ust.skill.common.java.api.FieldDeclaration<?> mapDeclaration, boolean isKey) {
		String[] mapDeclarationSplit = mapDeclaration.toString().split(",");

		String declaration = isKey ? mapDeclarationSplit[0] : mapDeclarationSplit[1];

		if (declaration.toString().contains("f32")) {
			return new Float(Float.parseFloat(value));
		} else if (declaration.toString().contains("f64")) {
			return new Double(Double.parseDouble(value));
		} else if (declaration.toString().contains("i8")) {
			return new Byte(Byte.parseByte(value));
		} else if (declaration.toString().contains("i16")) {
			return new Short(Short.parseShort(value));
		} else if (declaration.toString().contains("i32")) {
			return new Integer(Integer.parseInt(value));
		} else if (declaration.toString().contains("i64") || declaration.toString().contains("v64")) {
			return new Long(Long.parseLong(value));
		} else if (declaration.toString().contains("string")) {
			return value;
		} else if (declaration.toString().contains("bool")) {
			return new Boolean(Boolean.parseBoolean(value));
		} else {
			throw new IllegalArgumentException("The given fieldDeclaration is not supported.\n" + "Declaration was: "
					+ declaration.toString() + "\n"
					+ "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64','string','bool'}");
		}
	}
}
