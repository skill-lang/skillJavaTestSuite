package unicode;

import org.junit.Assert;
import org.junit.Test;

import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;
import unicode.api.SkillFile;

@SuppressWarnings("static-method")
public class ReadTest extends CommonTest {

    @Test
    public void test() throws Exception {
        SkillFile sf = unicode.api.SkillFile
                .open("../../src/test/resources/genbinary/[[empty]]/accept/unicode-reference.sf", Mode.Read);
        Unicode target = sf.Unicodes().getByID(1);
        Assert.assertEquals("1", target.one);
        Assert.assertEquals("ö", target.two);
        Assert.assertEquals("☢", target.three);
    }

    @Test
    public void iterateOverStrings() throws Exception {
        SkillFile sf = SkillFile.open(tmpFile("iterate"), Mode.Read, Mode.Write);
        for (String s : sf.Strings()) {
            System.out.println(s);
        }
    }

    @Test
    public void nullStrings() throws Exception {
        SkillFile sf = SkillFile.open(tmpFile("nullStrings"), Mode.Read, Mode.Write);
        sf.Unicodes().make(null, null, null);
        sf.close();
    }
}
