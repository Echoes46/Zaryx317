package io.zaryx.content.bosses;

import com.google.gson.*;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.combat.formula.rework.MagicCombatFormula;
import io.zaryx.model.Bonus;
import io.zaryx.model.definitions.*;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.stats.*;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ForestGuardianBalanceTest {
    @Test void repeatedDrainsCannotCrippleCombatLevels() {
        assertEquals(90, ForestGuardianBalance.drainedLevel(99,99));
        int level=99;
        for(int i=0;i<100;i++) level=ForestGuardianBalance.drainedLevel(level,99);
        assertEquals(74,level);
        assertEquals(30,ForestGuardianBalance.drainedLevel(30,99));
        assertEquals(1,ForestGuardianBalance.drainedLevel(1,1));
    }
    @Test void runtimeMagicDefenceMatchesPublishedWeakness() throws Exception {
        Gson gson=new Gson();
        JsonArray definitions=JsonParser.parseString(Files.readString(Path.of("etc/cfg/npc/npc_combat_defs.json"))).getAsJsonArray();
        JsonObject record=null;
        for(JsonElement entry:definitions) if(entry.getAsJsonObject().get("id").getAsInt()==13201) record=entry.getAsJsonObject();
        assertNotNull(record);
        NpcCombatDefinition combat=gson.fromJson(record,NpcCombatDefinition.class);
        JsonObject statsRecord=JsonParser.parseString(Files.readString(Path.of("etc/cfg/npc/npc_stats.json"))).getAsJsonObject().getAsJsonObject("13201");
        NpcStats stats=gson.fromJson(statsRecord,NpcStats.class);
        java.lang.reflect.Field config=Server.class.getDeclaredField("configuration");
        config.setAccessible(true); Object previous=config.get(null);
        NpcCombatDefinition old=NpcCombatDefinition.definitions.put(13201,combat);
        config.set(null,ServerConfiguration.getDefault());
        try {
            NPC npc=new NPC(1,13201,NpcDef.builder().name("Forest Guardian").build(),stats);
            int magicRoll=new MagicCombatFormula().getEffectiveDefenceLevel(npc)*(npc.getBonus(Bonus.DEFENCE_MAGIC)+64);
            assertEquals(18126,magicRoll);
            assertTrue(npc.getBonus(Bonus.DEFENCE_MAGIC)<npc.getBonus(Bonus.DEFENCE_SLASH));
            assertTrue(npc.getBonus(Bonus.DEFENCE_MAGIC)<npc.getBonus(Bonus.DEFENCE_RANGED));
            assertEquals(statsRecord.get("magicLevel").getAsInt(),combat.getLevel(NpcCombatSkill.MAGIC));
            assertEquals(statsRecord.get("magicDef").getAsInt(),npc.getBonus(Bonus.DEFENCE_MAGIC));
            assertEquals(1000,npc.getHealth().getMaximumHealth());
        } finally {
            config.set(null,previous);
            if(old==null) NpcCombatDefinition.definitions.remove(13201); else NpcCombatDefinition.definitions.put(13201,old);
        }
    }
}
