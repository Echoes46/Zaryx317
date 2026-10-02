package io.zaryx.content.bosspoints;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BossPointActivityTest {
    @Test void inactivityExpiresAndNewInputResumesWithoutLoginCredit() {
        BossPointActivity state = new BossPointActivity();
        assertFalse(state.isActive(1));
        state.recordInput(100);
        assertTrue(state.isActive(100 + BossPointActivity.GRACE_NANOS - 1));
        assertFalse(state.isActive(100 + BossPointActivity.GRACE_NANOS));
        assertTrue(state.warnOnce());
        assertFalse(state.warnOnce());
        state.recordInput(100 + BossPointActivity.GRACE_NANOS);
        assertTrue(state.isActive(101 + BossPointActivity.GRACE_NANOS));
        assertTrue(state.resume());
        assertFalse(state.resume());
        assertFalse(new BossPointActivity().isActive(102 + BossPointActivity.GRACE_NANOS));
    }
    @Test void backgroundPacketsCannotKeepCombatActive() {
        for (int opcode : new int[]{0,3,77,86,121,202,210,241,187})
            assertFalse(BossPointActivity.isGameplayInput(opcode), "Background opcode " + opcode);
        for (int opcode : new int[]{72,164,248,122,185,184,209,213,40})
            assertTrue(BossPointActivity.isGameplayInput(opcode), "Gameplay opcode " + opcode);
    }
    @Test void allRedeemableDonationScrollsAreUntradable() throws Exception {
        io.zaryx.model.definitions.ItemDef.load();
        for (var scroll : io.zaryx.content.dialogue.impl.ClaimDonatorScrollDialogue.DonationScroll.values())
            assertFalse(io.zaryx.model.definitions.ItemDef.forId(scroll.getItemId()).isTradable(), scroll.name());
    }
    @Test void inactiveKillsDoNotAwardPointsEvenDuringBuchu() throws Exception {
        var configuration = io.zaryx.Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        Object previous = configuration.get(null);
        configuration.set(null, io.zaryx.ServerConfiguration.getDefault());
        try {
        var player = new io.zaryx.model.entity.player.Player(null);
        player.bossPoints = 12;
        boolean old = io.zaryx.content.bosses.hespori.Hespori.activeBuchuSeed;
        try {
            io.zaryx.content.bosses.hespori.Hespori.activeBuchuSeed = true;
            assertEquals(0, BossPoints.addPoints(player, 5, false));
            assertEquals(12, player.bossPoints);
        } finally { io.zaryx.content.bosses.hespori.Hespori.activeBuchuSeed = old; }
        } finally { configuration.set(null, previous); }
    }
}
