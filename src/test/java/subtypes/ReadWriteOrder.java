package subtypes;

import java.nio.file.Path;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import subtypes.api.SkillFile;

import common.CommonTest;

import de.ust.skill.common.java.api.SkillFile.Mode;

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
    public void subtypesRead() throws Exception {
        SkillFile state = read("localBasePoolOffset.sf");
        String types = "aabbbcbbddacd";

        // check types
        String actualTypes = state.As().stream().map(a -> a.getClass().getSimpleName().toLowerCase())
                .reduce("", String::concat);
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
