package unicode;

import org.junit.Assert;
import org.junit.Test;

import de.ust.skill.common.java.api.SkillFile.Mode;
import unicode.api.SkillFile;

public class ReadTest {

    @SuppressWarnings("static-method")
    @Test
    public void test() throws Exception {
        SkillFile sf = unicode.api.SkillFile.open(
"../../src/test/resources/genbinary/[[empty]]/accept/unicode-reference.sf", Mode.Read);
        Unicode target = sf.Unicodes().getByID(1);
        Assert.assertEquals("1", target.one);
        Assert.assertEquals("ö", target.two);
        Assert.assertEquals("☢", target.three);
    }

}
