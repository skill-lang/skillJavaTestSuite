package de.ust.skill.common.java.internal;

import java.util.Iterator;

import de.ust.skill.common.java.api.Access;
import de.ust.skill.common.java.api.FieldDeclaration;
import de.ust.skill.common.java.api.SkillException;
import de.ust.skill.common.java.api.SkillFile;
import de.ust.skill.common.java.internal.exceptions.ParseException;

public abstract class ForceLazyFields {
    private ForceLazyFields() {
        // no instance
    }

    public static void forceFullCheck(SkillFile skillFile) {
        loadAll(skillFile);
        try {
            skillFile.check();
        } catch (SkillException e) {
            // convert to parse exception
            throw new ParseException(e, "a check failed");
        }
    }

    public static void loadAll(SkillFile skillFile) {
        for (Access<? extends SkillObject> t : skillFile.allTypes()) {
            Iterator<? extends FieldDeclaration<?>> fs = t.fields();
            while (fs.hasNext()) {
                FieldDeclaration<?> f = fs.next();
                if (f instanceof LazyField<?, ?>)
                    ((LazyField<?, ?>) f).ensureLoaded();
            }
        }
    }
}
