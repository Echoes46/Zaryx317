package io.zaryx.content.upgrade;

import io.zaryx.content.fireofexchange.FireOfExchangeBurnPrice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificateValueTest {
    @Test
    void everyCertificateRedeemsForItsFaceValueIncludingEndgameRewards() {
        int[] ids = {691, 692, 693, 696, 33428, 33429};
        int[] values = {10_000, 25_000, 50_000, 250_000, 1_000_000, 10_000_000};
        for (int i = 0; i < ids.length; i++) {
            assertEquals(values[i], FireOfExchangeBurnPrice.getBurnPrice(null, ids[i], false));
            assertTrue(FireOfExchangeBurnPrice.hasValue(ids[i]));
            assertTrue(FireOfExchangeBurnPrice.isNomadCertificate(ids[i]));
        }
        assertFalse(FireOfExchangeBurnPrice.isNomadCertificate(33237));
        assertFalse(FireOfExchangeBurnPrice.isNomadCertificate(995));
    }
}
