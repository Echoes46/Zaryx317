package io.zaryx.content.skills;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.definitions.NpcStats;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomSkillBenefitsTest {

    private Field configuration;
    private Object oldConfiguration;

    @BeforeEach
    void configureServer() throws Exception {
        configuration = Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        oldConfiguration = configuration.get(null);
        configuration.set(null, ServerConfiguration.getDefault());
    }

    @AfterEach
    void restoreServer() throws Exception {
        configuration.set(null, oldConfiguration);
    }

    private Player playerAt(Skill skill, int level) {
        Player player = new Player(null);
        player.playerLevel[skill.getId()] = level;
        return player;
    }

    private NPC npc(int id) {
        return new NPC(1, id, NpcDef.builder().name("Target").build(),
                NpcStats.builder().setName("Target").setHitpoints(100).createNpcStats());
    }

    @Test
    void fortuneScalesUpgradeLuckAndUnlocksMasteryProtection() {
        Player level50 = playerAt(Skill.FORTUNE, 50);
        Player level99 = playerAt(Skill.FORTUNE, 99);

        assertEquals(5.0, CustomSkillBenefits.fortuneUpgradeSuccessBonus(level50));
        assertEquals(0, CustomSkillBenefits.fortuneFailedUpgradeSaveChance(level50));
        assertEquals(12.0, CustomSkillBenefits.fortuneUpgradeSuccessBonus(level99), 0.0001);
        assertEquals(10, CustomSkillBenefits.fortuneFailedUpgradeSaveChance(level99));
    }

    @Test
    void demonHunterOnlyBoostsDamageAndAccuracyAgainstDemons() {
        Player level98 = playerAt(Skill.DEMON_HUNTER, 98);
        Player level99 = playerAt(Skill.DEMON_HUNTER, 99);
        NPC demon = npc(415);
        NPC regular = npc(1);

        assertEquals(1.098, CustomSkillBenefits.demonHunterCombatMultiplier(level98, demon), 0.0001);
        assertEquals(1.12, CustomSkillBenefits.demonHunterCombatMultiplier(level99, demon), 0.0001);
        assertEquals(1.0, CustomSkillBenefits.demonHunterCombatMultiplier(level99, regular), 0.0001);
    }
}
