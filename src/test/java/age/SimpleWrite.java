package age;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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

        Assert.assertTrue(sha256(path).equals(sha256(new File("src/test/resources/date-example.sf").toPath()))
                || sha256(path).equals(
                        sha256(new File("src/test/resources/date-example-with-empty-age-pool.sf").toPath())));
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
