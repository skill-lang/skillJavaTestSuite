package age;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

import junit.framework.Assert;

import org.junit.Test;

import age.api.SkillFile;
import de.ust.skill.common.java.api.SkillFile.Mode;

@SuppressWarnings("static-method")
public class SimpleWrite {

    /**
     * TODO move to common tests
     */
    protected Path tmpFile(String string) throws Exception {
        File r = File.createTempFile(string, ".sf");
        // r.deleteOnExit();
        return r.toPath();
    }

    /**
     * TODO move to common tests
     */
    protected final String sha256(String name) throws Exception {
        return sha256(new File("src/test/resources/" + name).toPath());
    }

    /**
     * TODO move to common tests
     */
    protected final String sha256(Path path) throws Exception {
        byte[] bytes = Files.readAllBytes(path);
        StringBuilder sb = new StringBuilder();
        for (byte b : MessageDigest.getInstance("SHA-256").digest(bytes))
            sb.append(String.format("%02X", b));
        return sb.toString();
    }

    @Test
    public void writeCopyReflective() throws Exception {
        Path path = tmpFile("write.copy.reflective");

        SkillFile sf = SkillFile.open("src/test/resources/date-example.sf");
        sf.changePath(path);
        sf.close();

        Assert.assertEquals(sha256(path), sha256(new File("src/test/resources/date-example.sf").toPath()));
    }

    // copy of §6.6 example"
    @Test
    public void writeCopy() throws Exception {
        Path path = tmpFile("write.copy");

        SkillFile sf = SkillFile.open("test/age.sf");
        sf.changePath(path);
        sf.close();

        // TODO change to age, after implementation of restrictions!
        Assert.assertEquals(sha256(path), sha256(new File("test/ageUnrestricted.sf").toPath()));
    }

    @Test
    public void writeAgeExample() throws Exception {
        Path path = tmpFile("write.make");

        SkillFile σ = SkillFile.open(path, Mode.Create, Mode.Write);
        σ.Ages().make(1);
        σ.Ages().make(28);
        σ.close();

        Assert.assertEquals(sha256(path), sha256(new File("test/age.sf").toPath()));
    }

    @Test
    public void normalizeAge16() throws Exception {
        SkillFile σ = SkillFile.open("test/age16.sf");

        long min = Long.MAX_VALUE;
        for (Age a : σ.Ages())
            min = Math.min(a.getAge(), min);

        for (Age a : σ.Ages())
            a.age -= min;

        Path tmpFile = tmpFile("normalized");
        σ.changePath(tmpFile);
        σ.close();

        Assert.assertTrue("negative v64 are significantly larger then positive ones",
                Files.size(tmpFile) > Files.size(new File("test/age16.sf").toPath()));
    }
}
