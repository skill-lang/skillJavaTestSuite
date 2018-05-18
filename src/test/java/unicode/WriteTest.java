package unicode;

import org.junit.Assert;
import org.junit.Test;

import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;
import unicode.api.SkillFile;

@SuppressWarnings("static-method")
public class WriteTest extends CommonTest {

    /**
     * Ensure deleted objects wont contribute their newly added strings to the
     * closure on write.
     */
    @Test
    public void deadClosure() throws Exception {
        SkillFile sf = unicode.api.SkillFile.open(tmpFile("delete"), Mode.Create, Mode.Write);

        // create node and delete it immediately
        Unicode target = sf.Unicodes().make("a", "a", "a");
        sf.delete(target);

        // write file to disk and ensure that no string survived
        sf.flush();
        for (String s : sf.Strings()) {
            Assert.assertNotEquals("String 'a' survived.", "a", s);
        }

        // read file from disk and ensure that the string is not contained in it
        // either
        for (String s : unicode.api.SkillFile.open(sf.currentPath(), Mode.Read, Mode.ReadOnly).Strings()) {
            Assert.assertNotEquals("String 'a' survived on disk.", "a", s);
        }
    }
}
