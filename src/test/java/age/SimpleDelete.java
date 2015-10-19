package age;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import age.api.SkillFile;
import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;

/**
 * Make some instances and delete them aftewards.
 * 
 * @author Timm Felden
 */
@SuppressWarnings("static-method")
public class SimpleDelete extends CommonTest {

    private static final SkillFile open(String name) throws Exception {
        return SkillFile.open(tmpFile(name), Mode.Create, Mode.Write);
    }

    private static final SkillFile read(Path path) throws Exception {
        return SkillFile.open(path, Mode.Read, Mode.ReadOnly);
    }

    @Test
    public void writeDelete() throws Exception {
        final int count = 100;

        SkillFile sf = open("writeDelete");
        for (int i = 0; i < count; i++)
            sf.Ages().make(i);

        sf.flush();

        Assert.assertEquals("check size", count, read(sf.currentPath()).Ages().size());

        // delete all instances
        for (Age a : sf.Ages())
            a.delete();

        sf.close();

        Assert.assertEquals("ensure that no instance remains", 0, read(sf.currentPath()).Ages().size());
    }

}
