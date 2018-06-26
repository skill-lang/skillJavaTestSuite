package empty;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;
import de.ust.skill.common.java.internal.StoragePool;
import empty.api.SkillFile;

/**
 * Make some instances and delete them afterwards without knowing anything about
 * them.
 * 
 * @author Timm Felden
 */
@SuppressWarnings("static-method")
public class SimpleDelete extends CommonTest {

    private static final SkillFile read(Path path) throws Exception {
        return SkillFile.open(path, Mode.Read, Mode.ReadOnly);
    }

    /**
     * Read age test data and delete age#1.
     */
    @Test
    public void deleteFirst() throws Exception {

        SkillFile sf = SkillFile.open("../../src/test/resources/genbinary/[[empty]]/accept/age.sf");
        sf.changePath(tmpFile("age"));
        sf.delete(((StoragePool<?, ?>) sf.allTypes().iterator().next()).getByID(1));
        sf.flush();

        StoragePool<?, ?> ages = (StoragePool<?, ?>) read(sf.currentPath()).allTypes().iterator().next();
        Assert.assertEquals("check size", 1, ages.size());
        Assert.assertEquals("check content", 28L, ages.fields().next().get(ages.getByID(1)));
    }

    /**
     * Read age test data and delete age#2.
     */
    @Test
    public void deleteSecond() throws Exception {

        SkillFile sf = SkillFile.open("../../src/test/resources/genbinary/[[empty]]/accept/age.sf");
        sf.changePath(tmpFile("age"));
        sf.delete(((StoragePool<?, ?>) sf.allTypes().iterator().next()).getByID(2));
        sf.flush();

        StoragePool<?, ?> ages = (StoragePool<?, ?>) read(sf.currentPath()).allTypes().iterator().next();
        Assert.assertEquals("check size", 1, ages.size());
        Assert.assertEquals("check content", 1L, ages.fields().next().get(ages.getByID(1)));
    }
}
