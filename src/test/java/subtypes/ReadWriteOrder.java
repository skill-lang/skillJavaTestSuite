package subtypes;

import java.nio.file.Path;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;
import subtypes.api.SkillFile;

/**
 * Two short tests that check type order implementation.
 * 
 * @author Timm Felden
 */
@SuppressWarnings("static-method")
public class ReadWriteOrder extends CommonTest {
    static SkillFile read(String s) throws Exception {
        return SkillFile.open("src/test/resources/" + s, Mode.Read, Mode.Append);
    }

    @Test
    public void makeSubtypesWAA() throws Exception {
        Path file = tmpFile("make.subtype.waa");
        {
            final SkillFile sf = SkillFile.open(file, Mode.Create, Mode.Append);

            java.util.function.Supplier<A> addA = () -> {
                A a = sf.As().make();
                a.setA(a);
                return a;
            };
            java.util.function.Supplier<B> addB = () -> {
                B a = sf.Bs().make();
                a.setA(a);
                a.setB(a);
                return a;
            };
            java.util.function.Supplier<C> addC = () -> {
                C a = sf.Cs().make();
                a.setA(a);
                a.setC(a);
                return a;
            };
            java.util.function.Supplier<D> addD = () -> {
                D a = sf.Ds().make();
                a.setA(a);
                a.setB(a);
                a.setD(a);
                return a;
            };
            addC.get();
            addA.get();
            addB.get();
            addA.get();
            addB.get();
            addB.get();
            sf.flush();

            addB.get();
            addD.get();
            addB.get();
            addD.get();
            sf.flush();

            addA.get();
            addC.get();
            addD.get();
            sf.close();
        }
        // write order
        {
            SkillFile sf = SkillFile.open(file, Mode.Read);

            final String types = "aabbbcbbddadc";

            for (int i = 0; i < types.length(); i++) {
                A obj = sf.As().getByID(i + 1);
                Assert.assertEquals(obj.getClass().getSimpleName().toLowerCase().charAt(0), types.charAt(i));
            }
        }
        // type order
        {
            SkillFile sf = SkillFile.open(file, Mode.Read);

            final String types = "aaabbbbbdddcc";

            Iterator<A> as = sf.As().typeOrderIterator();
            for (int i = 0; i < types.length(); i++) {
                A obj = as.next();
                Assert.assertEquals(obj.getClass().getSimpleName().toLowerCase().charAt(0), types.charAt(i));
            }
        }
        // self references
        {
            SkillFile sf = SkillFile.open(file, Mode.Read);

            for (A a : sf.As())
                Assert.assertEquals(a, a.getA());
            for (B a : sf.Bs())
                Assert.assertEquals(a, a.getB());
            for (C a : sf.Cs())
                Assert.assertEquals(a, a.getC());
            for (D a : sf.Ds())
                Assert.assertEquals(a, a.getD());
        }
    }

    @Test
    public void subtypesRead() throws Exception {
        SkillFile state = read("localBasePoolOffset.sf");
        String types = "aabbbcbbddacd";

        // check types
        String actualTypes = state.As().stream().map(a -> a.getClass().getSimpleName().toLowerCase()).reduce("",
                String::concat);
        Assert.assertEquals("type order missmatch", types, actualTypes);

        // check fields (all fields are self-references)
        for (A a : state.As())
            Assert.assertEquals(a.a, a);
        for (B b : state.Bs())
            Assert.assertEquals(b.b, b);
        for (C c : state.Cs())
            Assert.assertEquals(c.c, c);
        for (D d : state.Ds())
            Assert.assertEquals(d.d, d);
    }

    @Test
    public void subtypesCreate() throws Exception {
        Path path = tmpFile("lbpo.create");
        SkillFile sf = SkillFile.open(path, Mode.Create, Mode.Append);

        String[] blocks = new String[] { "aabbbc", "bbdd", "acd" };

        for (String b : blocks) {
            b.chars().forEach(c -> {
                switch (c) {
                case 'a': {
                    A i = sf.As().make();
                    i.a = i;
                    break;
                }

                case 'b': {
                    B i = sf.Bs().make();
                    i.a = i;
                    i.b = i;
                    break;
                }

                case 'c': {
                    C i = sf.Cs().make();
                    i.a = i;
                    i.c = i;
                    break;
                }

                case 'd': {
                    D i = sf.Ds().make();
                    i.a = i;
                    i.b = i;
                    i.d = i;
                    break;
                }
                }
            });
            sf.flush();
        }
    }

    @Test
    public void subtypesWrite() throws Exception {
        Path path = tmpFile("lbpo.write");
        SkillFile state = read("localBasePoolOffset.sf");
        // check self references

        long index = 1L;
        for (A instance : state.As()) {
            Assert.assertEquals("index missmatch", instance.a.getSkillID(), index++);
            Assert.assertEquals("self reference corrupted", instance.a, instance);
        }

        state.changePath(path);
        state.flush();

        // check self references again (write might not have restored them)
        index = 1L;
        for (A instance : state.As()) {
            Assert.assertEquals("index missmatch", instance.a.getSkillID(), index++);
            Assert.assertEquals("self reference corrupted", instance.a, instance);
        }

        SkillFile state2 = SkillFile.open(path);

        // check type of deserialized instances
        Iterator<A> is1 = state.As().typeOrderIterator();
        Iterator<A> is2 = state2.As().typeOrderIterator();
        while (is1.hasNext() || is2.hasNext()) {
            assert is1.hasNext() && is2.hasNext() : "same size";
            Assert.assertEquals("check type of deserialized instances", is1.next().getClass(), is2.next().getClass());
        }
    }
}
