package graphInterface;

import org.junit.Assert;
import org.junit.Test;

import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;
import graphInterface.api.SkillFile;

/**
 * Simple write tests.
 * 
 * @author Timm Felden
 */
@SuppressWarnings("static-method")
public class SimpleWrite extends CommonTest {

    @Test
    public void createAndWrite() throws Exception {
        SkillFile sf = SkillFile.open(tmpFile("createAndWrite"), Mode.Create, Mode.Write);

        sf.Nodes().make();

        sf.close();
    }

    @Test
    public void writeColor() throws Exception {
        SkillFile sf = SkillFile.open(tmpFile("writeColor"), Mode.Create, Mode.Write);

        sf.Nodes().build().color("red").make();

        sf.close();

        Assert.assertEquals("red", SkillFile.open(sf.currentPath()).Nodes().getByID(1).color);
    }

}
