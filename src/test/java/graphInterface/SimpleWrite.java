package graphInterface;

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
    public void insertAB() throws Exception {
        SkillFile sf = SkillFile.open(tmpFile("insertAB"), Mode.Create, Mode.Write);


        sf.close();
    }

}
