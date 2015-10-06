package age;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import age.api.SkillFile;
import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;

@SuppressWarnings("static-method")
public class SimpleWrite extends CommonTest {

    @Test
    public void writeCopyReflective() throws Exception {
        Path path = tmpFile("write.copy.reflective");

        SkillFile sf = SkillFile.open("src/test/resources/date-example.sf");
        sf.changePath(path);
        sf.close();

        Assert.assertTrue(
                sha256(path).equals(sha256(new File("src/test/resources/date-example.sf").toPath())) || sha256(path)
                        .equals(sha256(new File("src/test/resources/date-example-with-empty-age-pool.sf").toPath())));
    }

    @Test
    public void reflectiveWriteNode() throws Exception {
        SkillFile sf = SkillFile.open("src/test/resources/fourColoredNodes.sf", Mode.Read, Mode.Write);
        sf.changePath(tmpFile("reflective.write"));
        sf.close();
    }

    // copy of §6.6 example"
    @Test
    public void writeCopy() throws Exception {
        Path path = tmpFile("write.copy");

        SkillFile sf = SkillFile.open("test/age.sf");
        sf.changePath(path);
        sf.close();

        // TODO change to age, after implementation of restrictions!
        Assert.assertEquals(sha256(path), sha256(new File("test/ageUnrestricted.sf").toPath()));
    }

    @Test
    public void writeAgeExample() throws Exception {
        Path path = tmpFile("write.make");

        SkillFile σ = SkillFile.open(path, Mode.Create, Mode.Write);
        Assert.assertEquals(1, σ.Ages().make(1).getAge());
        Assert.assertEquals(28, σ.Ages().make(28).getAge());
        σ.close();

        Assert.assertEquals(sha256(path), sha256(new File("test/ageUnrestricted.sf").toPath()));
    }

    @Test
    public void writeMultiByteValues() throws Exception {
        Path path = tmpFile("write.multibyte");

        // write file
        {
            SkillFile sf = SkillFile.open(path, Mode.Create, Mode.Write);
            sf.Ages().make(-1);
            sf.Ages().make(31337);
            sf.Ages().make(1091986);
            sf.close();
        }
        // read file
        {
            SkillFile sf = SkillFile.open(path);
            Assert.assertEquals(-1, sf.Ages().getByID(1).age);
            Assert.assertEquals(31337, sf.Ages().getByID(2).age);
            Assert.assertEquals(1091986, sf.Ages().getByID(3).age);
        }
    }

    @Test
    public void randomValues() throws Exception {
        Path path = tmpFile("write.multibyte");

        // write file
        {
            SkillFile sf = SkillFile.open(path, Mode.Create, Mode.Write);
            Random rand = new Random(31337);
            for (int i = 1000000; i != 0; i--)
                sf.Ages().make(rand.nextLong());
            sf.close();
        }
        // read file
        {
            SkillFile sf = SkillFile.open(path);
            Random rand = new Random(31337);
            for (Age age : sf.Ages())
                Assert.assertEquals(rand.nextLong(), age.age);
        }
    }

    @Test
    public void normalizeAge16() throws Exception {
        SkillFile σ = SkillFile.open("test/age16.sf");

        long max = Long.MIN_VALUE;
        for (Age a : σ.Ages())
            max = Math.max(a.getAge(), max);

        for (Age a : σ.Ages())
            a.setAge(a.getAge() - max);

        Path tmpFile = tmpFile("normalized");
        σ.changePath(tmpFile);
        σ.close();

        Assert.assertTrue("negative v64 are significantly larger then positive ones",
                Files.size(tmpFile) > Files.size(new File("test/age16.sf").toPath()));
    }
}
