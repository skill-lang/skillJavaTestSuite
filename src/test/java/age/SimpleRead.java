package age;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import age.api.SkillFile;
import de.ust.skill.common.java.api.SkillFile.Mode;

@SuppressWarnings("static-method")
public class SimpleRead {

    @Test
    public void create() throws Exception {
        // @note: you do not need to delete the file, because it is never created
        SkillFile sf = SkillFile.open("age-test.sf", Mode.Create);
        for (Age a : sf.Ages())
            System.out.println(a.prettyString());
    }

    @Test
    public void readUnrestricted() throws Exception {
        SkillFile sf = SkillFile.open("test/ageUnrestricted.sf", Mode.Read);
        Iterator<Age> as = sf.Ages().iterator();
        Assert.assertEquals(1, as.next().getAge());
        Assert.assertEquals(28, as.next().getAge());
    }

    @Test
    public void read() throws Exception {
        SkillFile sf = SkillFile.open("test/age.sf", Mode.Read);
        Iterator<Age> as = sf.Ages().iterator();
        Assert.assertEquals(1, as.next().getAge());
        Assert.assertEquals(28, as.next().getAge());
    }

    @Test
    public void read16() throws Exception {
        SkillFile sf = SkillFile.open("test/age16.sf", Mode.Read);
        Assert.assertEquals(3400000, sf.Ages().size());
        int zeroes = 0;
        for (Age a : sf.Ages()) {
            Assert.assertTrue("negative ages", a.getAge() >= 0);
            if (0 == a.getAge())
                zeroes++;
        }
        Assert.assertEquals("missed some zeroes?", 53725, zeroes);
    }

}
