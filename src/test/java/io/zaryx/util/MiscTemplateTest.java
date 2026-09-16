package io.zaryx.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiscTemplateTest {
    @Test
    void replacementTreatsDollarSignsAndBackslashesAsText() {
        assertEquals("Claimed $5\\rare achievement",
                Misc.replaceBracketsWithArguments("Claimed {} achievement", "$5\\rare"));
        assertEquals("first {} then null",
                Misc.replaceBracketsWithArguments("first {} then {}", "{}", null));
    }
}
