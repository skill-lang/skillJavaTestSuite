package de.ust.skill.common.java.internal;

import org.junit.Assert;
import org.junit.Test;

import common.CommonTest;
import empty.internal.SkillState;
import empty.api.SkillFile;

/**
 * Perform some corner case test abusing internal structure for its
 * implementation.
 * 
 * @author Timm Felden
 */
@SuppressWarnings("static-method")
public class SimpleDelete extends CommonTest {

    /**
     * Read age test data and delete everything.
     */
    @Test
    public void reflectiveDeletAll() throws Exception {

        SkillFile sf = SkillFile.open("../../src/test/resources/genbinary/[[empty]]/accept/age-example.sf");
        sf.changePath(tmpFile("age"));

        SkillState σ = (SkillState) sf;
        σ.types.clear();
        σ.Strings().clear();

        sf.flush();

        Assert.assertEquals("check file size", 2, sf.currentPath().toFile().length());
    }
}
