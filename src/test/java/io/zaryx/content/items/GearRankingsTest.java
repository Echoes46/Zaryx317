package io.zaryx.content.items;

import io.zaryx.content.combat.specials.impl.DragonDagger;
import io.zaryx.model.definitions.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class GearRankingsTest {
    private final Map<Integer, ItemStats> stats = new HashMap<>();
    private final Map<Integer, ItemDef> definitions = new HashMap<>();
    void item(int id, String name, ItemEquipmentStats equipment) {
        stats.put(id, ItemStats.builder().name(name).equipable(true).equipment(equipment).build());
        definitions.put(id, ItemDef.builder().id(id).name(name).build());
    }
    List<GearRankings.Entry> top(GearRankings.Style style, GearRankings.Slot slot) {
        return GearRankings.rank(stats, definitions, style, slot);
    }
    @Test void styleSeparatesDamageBonuses() {
        item(1,"Melee helm",ItemEquipmentStats.builder().slot(0).str(20).build());
        item(2,"Ranged helm",ItemEquipmentStats.builder().slot(0).rstr(30).build());
        item(3,"Magic helm",ItemEquipmentStats.builder().slot(0).mdmg(40).build());
        assertEquals(1,top(GearRankings.Style.MELEE,GearRankings.Slot.HELMETS).get(0).id);
        assertEquals(2,top(GearRankings.Style.RANGED,GearRankings.Slot.HELMETS).get(0).id);
        assertEquals(3,top(GearRankings.Style.MAGIC,GearRankings.Slot.HELMETS).get(0).id);
    }
    @Test void capsAtTenAndExcludesNotesBrokenAndDuplicateNames() {
        for(int i=1;i<=15;i++)item(i,"Helm "+i,ItemEquipmentStats.builder().slot(0).str(i).build());
        item(100,"Helm 15",ItemEquipmentStats.builder().slot(0).str(15).build());
        item(101,"Broken helm",ItemEquipmentStats.builder().slot(0).str(999).build());
        item(102,"Noted helm",ItemEquipmentStats.builder().slot(0).str(999).build());
        definitions.put(102,ItemDef.builder().id(102).name("Noted helm").noted(true).build());
        List<GearRankings.Entry> ranked=top(GearRankings.Style.MELEE,GearRankings.Slot.HELMETS);
        assertEquals(10,ranked.size()); assertEquals(15,ranked.get(0).id);
        assertEquals(6,ranked.get(9).id);
    }
    @Test void jewelryCombinesNeckAndRingButNotOtherSlots() {
        item(1,"Necklace",ItemEquipmentStats.builder().slot(2).str(5).build());
        item(2,"Ring",ItemEquipmentStats.builder().slot(12).str(10).build());
        item(3,"Cape",ItemEquipmentStats.builder().slot(1).str(100).build());
        item(773,"'perfect' ring",ItemEquipmentStats.builder().slot(12).str(500000).build());
        List<GearRankings.Entry> ranked=top(GearRankings.Style.MELEE,GearRankings.Slot.JEWELRY);
        assertEquals(2,ranked.size()); assertEquals(2,ranked.get(0).id);
        assertTrue(TopEquipment.lines(ranked.get(0),GearRankings.Style.MELEE,1)[1].contains("Ring"));
    }
    @Test void actualSpecialsAndSpeedAffectScoreWithoutMutatingStats() {
        ItemEquipmentStats fast=ItemEquipmentStats.builder().slot(3).astab(40).str(30).aspeed(4).build();
        ItemEquipmentStats slow=ItemEquipmentStats.builder().slot(3).astab(40).str(30).aspeed(6).build();
        double base=GearRankings.score(fast,GearRankings.Style.MELEE,null);
        assertEquals(160,base,0.001);
        assertTrue(GearRankings.score(fast,GearRankings.Style.MELEE,new DragonDagger())>base);
        assertTrue(GearRankings.score(slow,GearRankings.Style.MELEE,null)<base);
        assertEquals(4,fast.getAttackSpeed()); assertEquals(30,fast.getStr());
        item(1215,"Dragon dagger",fast);
        assertNotNull(top(GearRankings.Style.MELEE,GearRankings.Slot.WEAPON).get(0).special);
        assertTrue(top(GearRankings.Style.RANGED,GearRankings.Slot.WEAPON).isEmpty());
    }
    @Test void missingDataIsEmptyAndTieOrderIsStable() {
        assertTrue(GearRankings.rank(null,null,GearRankings.Style.MELEE,GearRankings.Slot.HELMETS).isEmpty());
        item(5,"Same",ItemEquipmentStats.builder().slot(0).str(10).build());
        item(4,"Same",ItemEquipmentStats.builder().slot(0).str(10).build());
        assertEquals(4,top(GearRankings.Style.MELEE,GearRankings.Slot.HELMETS).get(0).id);
    }
}
