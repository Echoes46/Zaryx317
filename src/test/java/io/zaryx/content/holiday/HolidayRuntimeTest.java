package io.zaryx.content.holiday;

import com.google.gson.Gson;
import io.zaryx.*;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.model.collisionmap.ObjectDef;
import io.zaryx.model.definitions.*;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.*;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class HolidayRuntimeTest {
    static class TestPlayer extends Player {
        final List<String> objects=new ArrayList<>();
        final Map<Integer,String> trackerText=new HashMap<>();
        final PlayerAssistant assistant=new PlayerAssistant(this) {
            @Override public void object(int id,int x,int y,int face,int type,boolean flush) {
                objects.add(id+":"+x+":"+y);
            }
            @Override public void sendString(int id,String value) {
                if(id>=61410&&id<=61414)trackerText.put(id,value);
                else super.sendString(id,value);
            }
        };
        TestPlayer(){super(null);saveCharacter=false;}
        @Override public PlayerAssistant getPA(){return assistant==null?super.getPA():assistant;}
        @Override public void updateController(){ }
        @Override public void start(DialogueBuilder dialogue){ }
    }

    @Test void gatherAllSuppliesRefreshReusedHeightAndRecoverMissingHost() throws Exception {
        var config=Server.class.getDeclaredField("configuration");config.setAccessible(true);
        var oldConfig=config.get(null);config.set(null,ServerConfiguration.getDefault());
        var settings=HolidayEvents.class.getDeclaredField("settings");settings.setAccessible(true);
        var oldSettings=settings.get(null);Properties props=new Properties();props.setProperty("halloween.enabled","true");props.setProperty("christmas.enabled","true");
        settings.set(null,new HolidaySettings(props));
        var eventsField=io.zaryx.model.cycleevent.CycleEventHandler.class.getDeclaredField("instance");eventsField.setAccessible(true);
        var oldEvents=eventsField.get(null);
        var events=new io.zaryx.model.cycleevent.CycleEventHandler();eventsField.set(null,events);
        var ticks=Player.class.getDeclaredMethod("processTickables");ticks.setAccessible(true);
        var layoutsField=HolidayEvents.class.getDeclaredField("layouts");layoutsField.setAccessible(true);
        Map<Holiday,HolidayEvents.Layout> layouts=(Map<Holiday,HolidayEvents.Layout>)layoutsField.get(null);
        var oldLayouts=new EnumMap<Holiday,HolidayEvents.Layout>(Holiday.class);oldLayouts.putAll(layouts);
        List<HolidayInstance> instances=new ArrayList<>();
        List<NPC> actors=new ArrayList<>();
        try {
            ObjectDef.loadConfig();NpcDef.load();NpcStats.load();
            TestPlayer p=new TestPlayer();p.absX=3098;p.absY=3512;
            var base=Arrays.stream(new Gson().fromJson(Files.readString(Path.of("etc/cfg/holiday-events.json")),HolidayEvents.Layout[].class))
                    .filter(l->l.holiday==Holiday.HALLOWEEN).findFirst().orElseThrow();
            layouts.put(Holiday.HALLOWEEN,base);
            Integer previousHeight=null;
            for(int seed:new int[]{1,7,13,19,37,55}) {
                var instance=new HolidayInstance(p,HolidayLayouts.round(base,seed));instances.add(instance);
                instance.enter(p);actors.addAll(instance.getNpcs());
                p.getNextPlayerMovement();p.checkInstanceCoords();assertSame(instance,p.getInstance());
                assertEquals("Halloween supplies",p.trackerText.get(61410));
                assertEquals(Integer.toString(HolidayEvents.progress(p,Holiday.HALLOWEEN).gathered),p.trackerText.get(61414));
                if(previousHeight!=null)assertEquals(previousHeight.intValue(),instance.getHeight());
                previousHeight=instance.getHeight();
                p.objects.clear();instance.tick(p);
                assertEquals(instance.layout.objects.length+HolidayLayouts.DOORS.length,p.objects.size(),"Only this round's scenery should refresh");
                for(var station:instance.layout.objects)assertTrue(p.objects.contains(station.id+":"+station.x+":"+station.y));
                for(int tick=0;tick<10;tick++)for(var n:new ArrayList<>(instance.getNpcs())) {
                    n.process();assertFalse(n.processDeregistration(),"Removed NPC "+n.getNpcId());
                    assertTrue(p.viewable(n,false),"Invisible NPC "+n.getNpcId());
                }
                for(int objectId:new int[]{156,160,155,160}) {
                    p.teleTimer=0;
                    io.zaryx.model.entity.player.packets.objectoptions.ObjectOptionOne.handleOption(p,objectId,3097,3359);
                    events.process();events.process();p.getNextPlayerMovement();p.checkInstanceCoords();
                    p.teleTimer=0;instance.tick(p);p.getNextPlayerMovement();
                    assertSame(instance,p.getInstance());assertEquals(instance.getHeight(),p.heightLevel);
                    assertEquals(objectId==160?3098:3096,p.absX);
                    assertEquals(objectId==160?3357:3358,p.absY,"Passage must not bounce back to the main room");
                    assertFalse(instance.isDisposed());
                }
                // Reproduce the reported state: an attached owner at public height zero.
                p.heightLevel=0;
                assertTrue(instance.getNpcs().stream().noneMatch(n->p.viewable(n,false)));
                instance.tick(p);p.getNextPlayerMovement();p.checkInstanceCoords();
                assertEquals(instance.getHeight(),p.heightLevel);
                assertTrue(instance.getNpcs().stream().allMatch(n->p.viewable(n,false)));
                var progress=HolidayEvents.progress(p,Holiday.HALLOWEEN);progress.stage=0;progress.start(0);
                instance.refreshSupplyTracker(p);assertEquals("0",p.trackerText.get(61414));
                for(var station:instance.layout.objects)if(station.role>=0&&station.role<3) {
                    progress.nextAction=0;
                    assertTrue(HolidayEvents.walkToObject(p,station.id,station.x,station.y));
                    for(int tick=0;tick<60;tick++){p.getNextPlayerMovement();ticks.invoke(p);}
                    assertNotEquals(0,progress.gathered&(1<<station.role),"Uncollected supply "+station.id);
                    assertEquals(Integer.toString(progress.gathered),p.trackerText.get(61414));
                }
                assertEquals(2,progress.stage);assertEquals(7,progress.gathered);
                var host=instance.getNpcs().stream().filter(n->((HolidayNpc)n).role==-1).findFirst().orElseThrow();
                host.unregisterInstant();instance.tick(p);
                var replacement=instance.getNpcs().stream().filter(n->((HolidayNpc)n).role==-1).findFirst().orElseThrow();
                actors.add(replacement);assertNotSame(host,replacement);assertTrue(p.viewable(replacement,false));
                assertFalse(replacement.randomWalk);
                instance.leave(p);
                instance.tick(p);assertEquals(0,p.heightLevel,"Leaving must not pull a player back");
                assertEquals("",p.trackerText.get(61410));
            }
            p.moveTo(new Position(3108,3361,0));p.getNextPlayerMovement();
            var saved=HolidayEvents.progress(p,Holiday.HALLOWEEN);
            saved.stage=1;saved.gathered=1;saved.puzzle=0;saved.delivered=0;saved.layoutSeed=13;
            String before=saved.encode();
            var publicStation=base.objects[0];
            assertTrue(HolidayEvents.walkToObject(p,publicStation.id,publicStation.x,publicStation.y));
            assertEquals(before,saved.encode(),"Public scenery cannot award private supplies");
            HolidayEvents.resumeHalloween(p);
            assertTrue(p.getInstance() instanceof HolidayInstance);
            var resumed=(HolidayInstance)p.getInstance();instances.add(resumed);actors.addAll(resumed.getNpcs());
            p.getNextPlayerMovement();p.checkInstanceCoords();
            assertTrue(p.heightLevel>0);assertEquals(before,saved.encode(),"Resume must preserve supplies and layout");
            assertTrue(resumed.getNpcs().stream().allMatch(n->p.viewable(n,false)));
            for(var station:resumed.layout.objects)if(station.role==1||station.role==2) {
                saved.nextAction=0;
                assertTrue(HolidayEvents.walkToObject(p,station.id,station.x,station.y));
                for(int tick=0;tick<60;tick++){p.getNextPlayerMovement();ticks.invoke(p);}
            }
            assertEquals(7,saved.gathered);assertEquals(2,saved.stage);
            saved.stage=4;saved.puzzle=3;saved.delivered=7;assertTrue(saved.complete());
            int previousManorHeight=resumed.getHeight();
            HolidayEvents.enterRound(p,Holiday.HALLOWEEN);
            var halloweenReplay=(HolidayInstance)p.getInstance();instances.add(halloweenReplay);actors.addAll(halloweenReplay.getNpcs());
            p.getNextPlayerMovement();p.checkInstanceCoords();
            assertNotEquals(previousManorHeight,halloweenReplay.getHeight(),"A replay needs a fresh map height");
            assertEquals(1,saved.stage);assertEquals(0,saved.gathered);
            assertEquals("0",p.trackerText.get(61414));
            p.objects.clear();halloweenReplay.tick(p);
            var firstHalloweenStation=Arrays.stream(halloweenReplay.layout.objects).filter(station->station.role==0).findFirst().orElseThrow();
            assertTrue(p.objects.contains(firstHalloweenStation.id+":"+firstHalloweenStation.x+":"+firstHalloweenStation.y));
            saved.nextAction=0;
            assertTrue(HolidayEvents.walkToObject(p,firstHalloweenStation.id,firstHalloweenStation.x,firstHalloweenStation.y));
            for(int tick=0;tick<60;tick++){p.getNextPlayerMovement();ticks.invoke(p);}
            assertEquals(1,saved.gathered);
            halloweenReplay.leave(p);
            var christmas=Arrays.stream(new Gson().fromJson(Files.readString(Path.of("etc/cfg/holiday-events.json")),HolidayEvents.Layout[].class))
                    .filter(l->l.holiday==Holiday.CHRISTMAS).findFirst().orElseThrow();
            layouts.put(Holiday.CHRISTMAS,christmas);
            var christmasProgress=HolidayEvents.progress(p,Holiday.CHRISTMAS);
            for(int seed:new int[]{19,37,55}) {
                christmasProgress.stage=0;
                christmasProgress.gathered=0;
                christmasProgress.layoutSeed=seed;
                var round=new HolidayInstance(p,HolidayLayouts.round(christmas,seed));
                instances.add(round);
                round.enter(p);
                actors.addAll(round.getNpcs());
                p.getNextPlayerMovement();p.checkInstanceCoords();
                assertSame(round,p.getInstance());
                assertEquals("Christmas supplies",p.trackerText.get(61410));
                assertEquals("0",p.trackerText.get(61414));
                assertEquals(5,round.getNpcs().size(),"Santa, three helpers and the penguin must appear");
                assertTrue(round.getNpcs().stream().allMatch(n->p.viewable(n,false)));
                p.objects.clear();round.tick(p);
                assertEquals(round.layout.objects.length,p.objects.size());
                for(var station:round.layout.objects)assertTrue(p.objects.contains(station.id+":"+station.x+":"+station.y));
                christmasProgress.stage=1;
                for(var station:round.layout.objects)if(station.role>=0&&station.role<3) {
                    christmasProgress.nextAction=0;
                    assertTrue(HolidayEvents.walkToObject(p,station.id,station.x,station.y));
                    for(int tick=0;tick<60;tick++){p.getNextPlayerMovement();ticks.invoke(p);}
                    assertNotEquals(0,christmasProgress.gathered&(1<<station.role),"Uncollected Christmas supply "+station.id);
                    assertEquals(Integer.toString(christmasProgress.gathered),p.trackerText.get(61414));
                }
                assertEquals(7,christmasProgress.gathered);
                assertEquals(2,christmasProgress.stage);
                round.leave(p);
                assertEquals("",p.trackerText.get(61410));
            }
            christmasProgress.stage=1;
            christmasProgress.gathered=1;
            christmasProgress.layoutSeed=37;
            String christmasSave=christmasProgress.encode();
            p.moveTo(new Position(christmas.entryX,christmas.entryY,0));p.getNextPlayerMovement();
            HolidayEvents.resumeChristmas(p);
            assertTrue(p.getInstance() instanceof HolidayInstance);
            var christmasResumed=(HolidayInstance)p.getInstance();
            instances.add(christmasResumed);actors.addAll(christmasResumed.getNpcs());
            p.getNextPlayerMovement();p.checkInstanceCoords();
            assertEquals(christmasSave,christmasProgress.encode());
            assertEquals(new Gson().toJson(HolidayLayouts.round(christmas,37)),new Gson().toJson(christmasResumed.layout));
            assertTrue(christmasResumed.getNpcs().stream().allMatch(n->p.viewable(n,false)));
            christmasProgress.stage=4;christmasProgress.gathered=7;christmasProgress.puzzle=3;christmasProgress.delivered=7;
            assertTrue(christmasProgress.complete());
            int previousChristmasHeight=christmasResumed.getHeight();
            HolidayEvents.enterRound(p,Holiday.CHRISTMAS);
            var christmasReplay=(HolidayInstance)p.getInstance();instances.add(christmasReplay);actors.addAll(christmasReplay.getNpcs());
            p.getNextPlayerMovement();p.checkInstanceCoords();
            assertNotEquals(previousChristmasHeight,christmasReplay.getHeight());
            assertEquals(1,christmasProgress.stage);assertEquals(0,christmasProgress.gathered);
            p.objects.clear();christmasReplay.tick(p);
            var firstChristmasStation=Arrays.stream(christmasReplay.layout.objects).filter(station->station.role==0).findFirst().orElseThrow();
            assertTrue(p.objects.contains(firstChristmasStation.id+":"+firstChristmasStation.x+":"+firstChristmasStation.y));
            christmasProgress.nextAction=0;
            assertTrue(HolidayEvents.walkToObject(p,firstChristmasStation.id,firstChristmasStation.x,firstChristmasStation.y));
            for(int tick=0;tick<60;tick++){p.getNextPlayerMovement();ticks.invoke(p);}
            assertEquals(1,christmasProgress.gathered);
            p.moveTo(new Position(3098,3508,0));p.getNextPlayerMovement();p.checkInstanceCoords();
            assertNull(p.getInstance());
            assertEquals("",p.trackerText.get(61410),"Leaving the instance must hide the checklist");
        } finally {
            for(var instance:instances)if(!instance.isDisposed())instance.dispose();
            for(var npc:actors)if(npc.getIndex()>0)npc.unregisterInstant();
            Server.getGlobalObjects().pulse();
            layouts.clear();layouts.putAll(oldLayouts);
            eventsField.set(null,oldEvents);
            settings.set(null,oldSettings);config.set(null,oldConfig);
        }
    }
}
