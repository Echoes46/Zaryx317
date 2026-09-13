package io.zaryx.content.combat.weapon;

import io.zaryx.content.combat.specials.impl.BloodInfusion;
import io.zaryx.content.combat.specials.impl.ShieldBash;
import io.zaryx.content.combat.specials.Specials;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.core.HitDispatcher;
import io.zaryx.model.CombatType;
import io.zaryx.model.definitions.*;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SpecialAttackTest {
    Field configuration, definitions, ticks;
    Object oldConfig, oldDefinitions;
    long oldTicks;
    Map<Integer, ItemStats> oldStats;
    @BeforeEach void setup() throws Exception {
        configuration = Server.class.getDeclaredField("configuration"); configuration.setAccessible(true);
        oldConfig = configuration.get(null); configuration.set(null, ServerConfiguration.getDefault());
        definitions = ItemDef.class.getDeclaredField("definitions"); definitions.setAccessible(true);
        oldDefinitions = definitions.get(null); Map<Integer, ItemDef> items = new HashMap<>();
        for (int id : new int[]{-1,0,21015,28682,25604,28997,10146,10147,10148,10149})
            items.put(id, ItemDef.builder().id(id).name("Test weapon").build());
        definitions.set(null, items);
        ticks = Server.class.getDeclaredField("tickCount"); ticks.setAccessible(true);
        oldTicks = ticks.getLong(null); ticks.setLong(null, 100);
        oldStats = ItemStats.itemStatsMap; ItemStats.load();
    }
    @AfterEach void restore() throws Exception {
        configuration.set(null, oldConfig); definitions.set(null, oldDefinitions);
        ticks.setLong(null, oldTicks); ItemStats.itemStatsMap = oldStats;
    }
    Player player(int weapon, int mode) {
        Player p = new Player(null);
        p.getPerkSytem().gameItems = new ArrayList<>();
        p.playerEquipment[Player.playerWeapon] = weapon;
        p.playerEquipmentN[Player.playerWeapon] = 1;
        p.getCombatConfigs().setAttackStyle(mode);
        return p;
    }
    NPC npc() {
        return new NPC(1, 1, NpcDef.builder().name("Target").build(),
                NpcStats.builder().setName("Target").setHitpoints(100).createNpcStats());
    }
    int button(int widget) { return Misc.hexToInt(new byte[]{(byte)(widget >> 8), (byte)widget}, 0, 2); }

    void fullSet(Player p) {
        p.playerEquipment[Player.playerHat]=29028;
        p.playerEquipment[Player.playerChest]=29022;
        p.playerEquipment[Player.playerLegs]=29025;
    }
    @Test void registrationAndEnergyCostsMatchAllVariants() {
        for (int id:new int[]{21015,28682,25604}) {
            assertTrue(Specials.forWeaponId(id) instanceof ShieldBash);
            assertEquals(5.0,Specials.forWeaponId(id).getRequiredCost());
        }
        assertTrue(Specials.forWeaponId(28997) instanceof BloodInfusion);
        assertEquals(2.5,Specials.forWeaponId(28997).getRequiredCost());
        assertNull(Specials.forWeaponId(10148));
    }
    @Test void infusionChargesOnceAndHealthSacrificeIgnoresDamageReducingBoots() {
        Player p=player(28997,0);fullSet(p);p.specAmount=10;
        p.playerEquipment[Player.playerFeet]=10558;
        p.getHealth().setMaximumHealth(100);p.getHealth().setCurrentHealth(99);
        assertTrue(new BloodInfusion().start(p));
        assertEquals(7.5,p.specAmount);assertEquals(75,p.getHealth().getCurrentHealth());
    }
    @Test void incompleteSetAndInsufficientEnergyCostNothing() {
        Player p=player(28997,0);p.specAmount=10;
        p.getHealth().setMaximumHealth(100);p.getHealth().setCurrentHealth(100);
        assertFalse(new BloodInfusion().start(p));assertEquals(10,p.specAmount);
        fullSet(p);p.specAmount=2.4;
        assertFalse(new BloodInfusion().start(p));assertEquals(2.4,p.specAmount);
        assertEquals(100,p.getHealth().getCurrentHealth());
        p.specAmount=10;p.playerEquipment[Player.playerHat]=-1;
        assertFalse(new BloodInfusion().start(p));
    }
    @Test void lowHealthSacrificeCannotKillPlayer() {
        for(int hp=1;hp<5;hp++)assertTrue(hp-BloodInfusion.healthCost(hp)>0);
        assertEquals(0,BloodInfusion.healthCost(3));assertEquals(1,BloodInfusion.healthCost(4));
    }
    @Test void shieldDrainsOnlyHighestOffensiveStyleAndNeverDefence() {
        assertArrayEquals(new int[]{95,95,80,70},ShieldBash.drain(100,100,80,70));
        assertArrayEquals(new int[]{80,80,95,90},ShieldBash.drain(80,80,100,90));
        assertArrayEquals(new int[]{80,80,90,95},ShieldBash.drain(80,80,90,100));
        NPC n=npc();
        n.getCombatDefinition().setLevel(io.zaryx.model.entity.npc.stats.NpcCombatSkill.ATTACK,100);
        n.getCombatDefinition().setLevel(io.zaryx.model.entity.npc.stats.NpcCombatSkill.STRENGTH,100);
        int defence=n.getDefenceLevel();
        new ShieldBash().hit(player(21015,0),n,new Damage(0));
        assertEquals(defence,n.getDefenceLevel());
        assertEquals(95,n.getCombatDefinition().getLevel(io.zaryx.model.entity.npc.stats.NpcCombatSkill.ATTACK));
        assertEquals(1,npc().getCombatDefinition().getLevel(io.zaryx.model.entity.npc.stats.NpcCombatSkill.ATTACK));
    }
    @Test void infusionSecondHitCanLandAfterFirstMissAndGuaranteesOneTickReduction() {
        Player p=player(28997,0);fullSet(p);
        Random old=HitDispatcher.rand;
        HitDispatcher.rand=new Random(){int n;public double nextDouble(){return n++==0?0.9:0;}public int nextInt(int bound){return bound-1;}};
        try {
            new HitDispatcher(p,npc()) {
                public void addCombatXP(CombatType t,int amount){}
                public void beforeDamageCalculated(CombatType t){maximumDamage=25;maximumAccuracy=0.5;}
                public void afterDamageCalculated(CombatType t,boolean hit){}
            }.playerHitEntity(CombatType.MELEE,new BloodInfusion());
            List<Damage> hits=new ArrayList<>(p.getDamageQueue().getQueue());
            assertEquals(2,hits.size());assertEquals(0,hits.get(0).getAmount());
            assertTrue(hits.get(1).getAmount()>=2 && hits.get(1).getAmount()<=13);
            assertEquals(3,p.attackTimer);
        } finally {HitDispatcher.rand=old;}
    }
    @Test void bloodragerRequiresSetAndNeverStacksTwoReductions() {
        Random old=HitDispatcher.rand;
        HitDispatcher.rand=new Random(){public double nextDouble(){return 0;}public int nextInt(int bound){return 0;}};
        try {
            for(boolean set:new boolean[]{false,true}) {
                Player p=player(28997,0);if(set)fullSet(p);
                new HitDispatcher(p,npc()) {
                    public void addCombatXP(CombatType t,int amount){}
                    public void beforeDamageCalculated(CombatType t){maximumDamage=20;maximumAccuracy=1;}
                    public void afterDamageCalculated(CombatType t,boolean hit){damage=maximumDamage;}
                }.playerHitEntity(CombatType.MELEE,null);
                assertEquals(set?3:4,p.attackTimer);
            }
        } finally {HitDispatcher.rand=old;}
    }
    @Test void singleCombatNeverAddsShieldBashTargets() {
        assertTrue(ShieldBash.secondaryTargets(player(21015,0),npc()).isEmpty());
    }
    @Test void shieldAreaCapsTargetsAndRejectsWrongPlaneRangeAndOwnership() {
        Player p=player(21015,0);p.absX=3230;p.absY=4080;
        NPC[] old=io.zaryx.model.entity.npc.NPCHandler.npcs;
        NPC[] targets=new NPC[25];
        try {
            io.zaryx.model.entity.npc.NPCHandler.npcs=targets;
            for(int i=1;i<targets.length;i++) {
                targets[i]=new NPC(i,1,NpcDef.builder().name("Target").build(),
                        NpcStats.builder().setName("Target").setHitpoints(100).createNpcStats());
                targets[i].setX(3231);targets[i].setY(4080);
            }
            targets[2].setHeight(1);
            targets[3].setX(3236);
            targets[4].spawnedBy=999;
            targets[5].isDead=true;
            assertTrue(p.getPosition().inMulti());
            List<io.zaryx.model.entity.Entity> selected=ShieldBash.secondaryTargets(p,targets[1]);
            assertEquals(9,selected.size());
            for(int i=1;i<=5;i++)assertFalse(selected.contains(targets[i]));
        } finally {io.zaryx.model.entity.npc.NPCHandler.npcs=old;}
    }

}
