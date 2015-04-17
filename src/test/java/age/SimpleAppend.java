package age;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import age.api.SkillFile;

import common.CommonTest;

import de.ust.skill.common.java.api.SkillFile.Mode;

public class SimpleAppend extends CommonTest {

    @Test
    public void appendExample() throws Exception {
        Path path = tmpFile("append.test");

        SkillFile sf = SkillFile.open("src/test/resources/age-example.sf", Mode.Read, Mode.Append);
        sf.Ages().make(2);
        sf.Ages().make(3);
        sf.changePath(path);
        sf.close();

        Assert.assertEquals(sha256(path), sha256(new File("src/test/resources/age-example-append.sf").toPath()));
        Iterator<Age> as = SkillFile.open(path).Ages().iterator();
        Assert.assertEquals(as.next().age, 1);
        Assert.assertEquals(as.next().age, -1);
        Assert.assertEquals(as.next().age, 2);
        Assert.assertEquals(as.next().age, 3);
        Assert.assertFalse(as.hasNext());
    }

    @Test
    public void writeAppendMultiState() throws Exception {
        int limit = (int) 1e5;
        Path path = tmpFile("append");

        // write
        {
            SkillFile sf = SkillFile.open(path, Mode.Create, Mode.Write);
            for (int i = 0; i < limit; i++)
                sf.Ages().make(i);

            sf.close();
        }

        // append
        for (int i = 1; i < 10; i++) {
            SkillFile sf = SkillFile.open(path, Mode.Read, Mode.Append);
            for (int v = i * limit; v < limit + i * limit; v++)
                sf.Ages().make(v);

            sf.close();
        }

        // read & check & write
        Path writePath = tmpFile("write");
        {
            SkillFile state = SkillFile.open(path, Mode.Read, Mode.Write);
            Iterator<Age> d = state.Ages().iterator();
            Assert.assertEquals("we somehow lost " + (10 * limit - state.Ages().size()) + " dates",
                    state.Ages().size(), 10 * limit);

            boolean cond = true;
            for (int i = 0; i < 10 * limit; i++)
                cond = cond && (i == d.next().age);
            Assert.assertTrue("match failed", cond);

            state.changePath(writePath);
            state.close();
        }

        // check append against write
        {
            SkillFile s1 = SkillFile.open(path, Mode.Read);
            SkillFile s2 = SkillFile.open(writePath, Mode.Read);

            Iterator<Age> i1 = s1.Ages().iterator();
            Iterator<Age> i2 = s2.Ages().iterator();

            while (i1.hasNext()) {
                Assert.assertEquals(i1.next().age, i2.next().age);
            }

            Assert.assertEquals("state1 had less elements!", i1.hasNext(), i2.hasNext());
        }
    }

    @Test
    public void writeAppendTwoState() throws Exception {
        int limit = (int) 1e5;
        Path path = tmpFile("append");

        // write
        SkillFile sf = SkillFile.open(path, Mode.Read, Mode.Append);
        for (int i = 0; i < limit; i++)
            sf.Ages().make(i);

        sf.flush();

        // append
        for (int i = 1; i < 10; i++) {
            for (int v = i * limit; v < limit + i * limit; v++)
                sf.Ages().make(v);

            sf.flush();
        }

        // read & check & write
        Path writePath = tmpFile("write");
        {
            SkillFile state = SkillFile.open(writePath, Mode.Create, Mode.Write);

            for (int i = 0; i < 10 * limit; i++)
                state.Ages().make(i);

            state.close();
        }

        // check append against write
        {
            SkillFile s1 = SkillFile.open(path);
            SkillFile s2 = SkillFile.open(writePath);

            Iterator<Age> i1 = s1.Ages().iterator();
            Iterator<Age> i2 = s2.Ages().iterator();

            while (i1.hasNext()) {
                Assert.assertEquals(i1.next().age, i2.next().age);
            }

            Assert.assertEquals("state1 had less elements!", i1.hasNext(), i2.hasNext());
        }
    }

    @Test
    public void writeAppendSingleState() throws Exception {
        int limit = (int) 1e5;
        Path path = tmpFile("append");

        // write
        SkillFile sf = SkillFile.open(path, Mode.Read, Mode.Append);
        for (int i = 0; i < limit; i++)
            sf.Ages().make(i);

        sf.flush();

        // append
        for (int i = 1; i < 10; i++) {
            for (int v = i * limit; v < limit + i * limit; v++)
                sf.Ages().make(v);

            sf.flush();
        }

        // read & check & write
        Path writePath = tmpFile("write");
        {
            SkillFile state = SkillFile.open(path, Mode.Read, Mode.Write);
            Iterator<Age> d = state.Ages().iterator();
            Assert.assertEquals("we somehow lost " + (10 * limit - state.Ages().size()) + " dates",
                    state.Ages().size(), 10 * limit);

            boolean cond = true;
            for (int i = 0; i < 10 * limit; i++)
                cond = cond && (i == d.next().age);
            Assert.assertTrue("match failed", cond);

            sf.changePath(writePath);
            sf.close();
        }

        // check append against write
        {
            SkillFile s1 = SkillFile.open(path);
            SkillFile s2 = SkillFile.open(writePath);

            Iterator<Age> i1 = s1.Ages().iterator();
            Iterator<Age> i2 = s2.Ages().iterator();

            while (i1.hasNext()) {
                Assert.assertEquals(i1.next().age, i2.next().age);
            }

            Assert.assertEquals("state1 had less elements!", i1.hasNext(), i2.hasNext());
        }
    }

    @Test
    public void writeAppendCheck() throws Exception {
        Path path = tmpFile("date.write.append.check");

        {
            SkillFile sf = SkillFile.open(path, Mode.Create, Mode.Write);
            sf.Ages().make(1);
            sf.Ages().make(2);
            sf.Ages().make(3);
            sf.close();
        }

        {
            SkillFile sf = SkillFile.open(path, Mode.Read, Mode.Append);
            sf.Ages().make(1);
            sf.Ages().make(2);
            sf.Ages().make(3);
            sf.close();
        }

        int expect = 1;
        for (Age a : SkillFile.open(path).Ages()) {
            Assert.assertEquals(expect, a.age);
            expect = 3 == expect ? 1 : expect + 1;
        }
    }
}
