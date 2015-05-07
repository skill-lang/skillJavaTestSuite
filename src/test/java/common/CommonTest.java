package common;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

import org.junit.Ignore;

@Ignore
abstract public class CommonTest {

    public CommonTest() {
        super();
    }

    /**
     * TODO move to common tests
     */
    protected static Path tmpFile(String string) throws Exception {
        File r = File.createTempFile(string, ".sf");
        r.deleteOnExit();
        return r.toPath();
    }

    /**
     * TODO move to common tests
     */
    protected final static String sha256(String name) throws Exception {
        return sha256(new File("src/test/resources/" + name).toPath());
    }

    /**
     * TODO move to common tests
     */
    protected final static String sha256(Path path) throws Exception {
        byte[] bytes = Files.readAllBytes(path);
        StringBuilder sb = new StringBuilder();
        for (byte b : MessageDigest.getInstance("SHA-256").digest(bytes))
            sb.append(String.format("%02X", b));
        return sb.toString();
    }

}
