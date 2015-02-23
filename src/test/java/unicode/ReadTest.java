package unicode;

import junit.framework.Assert;

import org.junit.Test;

import unicode.api.SkillFile;
import de.ust.skill.common.java.api.SkillFile.Mode;

public class ReadTest {

    @SuppressWarnings("static-method")
    @Test
    public void test() throws Exception {
        SkillFile sf = unicode.api.SkillFile.open(
                "../../src/test/resources/genbinary/<empty>/accept/unicode-reference.sf",
                Mode.Read);
        Unicode target = sf.Unicodes().getByID(1);
        Assert.assertEquals("i", target.one);
        Assert.assertEquals("ö", target.two);
        Assert.assertEquals("☢", target.three);
    }

}
