package subtypes;

import org.junit.Test;

import subtypes.api.SkillFile;

import common.CommonTest;

import de.ust.skill.common.java.api.SkillFile.Mode;

/**
 * Simple write tests.
 * 
 * @author Timm Felden
 */
public class SimpleWrite extends CommonTest {

    @Test
    private void createAndWrite() throws Exception {
        SkillFile sf = SkillFile.open(tmpFile("createAndWrite"), Mode.Create, Mode.Write);

        C c = sf.Cs().make();
        c.a = c;
        c.c = c;

        sf.close();
    }

    @Test
    private void insertAB() throws Exception {
        SkillFile sf = SkillFile.open(tmpFile("insertAB"), Mode.Create, Mode.Write);

        sf.As().make(sf.Bs().make());

        B b = sf.Bs().iterator().next();
        b.a = b;
        b.b = b;

        sf.close();
    }

}
