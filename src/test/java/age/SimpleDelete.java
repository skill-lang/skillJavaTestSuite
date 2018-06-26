package age;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import age.api.SkillFile;
import common.CommonTest;
import de.ust.skill.common.java.api.SkillFile.Mode;

/**
 * Make some instances and delete them afterwards.
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
            sf.delete(a);

        sf.close();

        Assert.assertEquals("ensure that no instance remains", 0, read(sf.currentPath()).Ages().size());
    }

    /**
     * Read age test data and delete age#1.
     */
    @Test
    public void deleteFirst() throws Exception {

        SkillFile sf = SkillFile.open("../../src/test/resources/genbinary/[[empty]]/accept/age.sf");
        sf.changePath(tmpFile("age"));
        sf.delete(sf.Ages().getByID(1));
        Age other = sf.Ages().getByID(2);
        sf.flush();

        Assert.assertEquals("check size", 1, read(sf.currentPath()).Ages().size());
        Assert.assertEquals("check content", other.getAge(), read(sf.currentPath()).Ages().getByID(1).getAge());
    }

    /**
     * Read age test data and delete age#2.
     */
    @Test
    public void deleteSecond() throws Exception {

        SkillFile sf = SkillFile.open("../../src/test/resources/genbinary/[[empty]]/accept/age.sf");
        sf.changePath(tmpFile("age"));
        sf.delete(sf.Ages().getByID(2));
        Age other = sf.Ages().getByID(1);
        sf.flush();

        Assert.assertEquals("check size", 1, read(sf.currentPath()).Ages().size());
        Assert.assertEquals("check content", other.getAge(), read(sf.currentPath()).Ages().getByID(1).getAge());
    }

    @Test
    public void writeDeleteSome() throws Exception {
        final int count = 100;

        SkillFile sf = open("writeDeleteSome");
        for (int i = 0; i < count; i++)
            sf.Ages().make(i);

        sf.flush();

        Assert.assertEquals("check size", count, read(sf.currentPath()).Ages().size());

        // delete all instances
        for (Age a : sf.Ages())
            if (a.getAge() < count / 10)
                sf.delete(a);

        sf.close();

        Assert.assertEquals("ensure that no instance remains", (int) (count * .9),
                read(sf.currentPath()).Ages().size());
    }
}
