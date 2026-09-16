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
        final PlayerAssistant assistant=new PlayerAssistant(this) {
            @Override public void object(int id,int x,int y,int face,int type,boolean flush) {
                objects.add(id+":"+x+":"+y);
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
        var oldSettings=settings.get(null);Properties props=new Properties();props.setProperty("halloween.enabled","true");
        settings.set(null,new HolidaySettings(props));
        var ticks=Player.class.getDeclaredMethod("processTickables");ticks.setAccessible(true);
        List<HolidayInstance> instances=new ArrayList<>();
        List<NPC> actors=new ArrayList<>();
        try {
            ObjectDef.loadConfig();NpcDef.load();NpcStats.load();
            TestPlayer p=new TestPlayer();p.absX=3098;p.absY=3512;
            var base=Arrays.stream(new Gson().fromJson(Files.readString(Path.of("etc/cfg/holiday-events.json")),HolidayEvents.Layout[].class))
                    .filter(l->l.holiday==Holiday.HALLOWEEN).findFirst().orElseThrow();
            Integer previousHeight=null;
            for(int seed:new int[]{1,7,13}) {
                var instance=new HolidayInstance(p,HolidayLayouts.round(base,seed));instances.add(instance);
                instance.enter(p);actors.addAll(instance.getNpcs());
                p.getNextPlayerMovement();p.checkInstanceCoords();assertSame(instance,p.getInstance());
                if(previousHeight!=null)assertEquals(previousHeight.intValue(),instance.getHeight());
                previousHeight=instance.getHeight();
                p.objects.clear();instance.tick(p);
                assertEquals(instance.layout.objects.length+HolidayLayouts.DOORS.length,p.objects.size(),"Only this round's scenery should refresh");
                for(var station:instance.layout.objects)assertTrue(p.objects.contains(station.id+":"+station.x+":"+station.y));
                for(int tick=0;tick<10;tick++)for(var n:new ArrayList<>(instance.getNpcs())) {
                    n.process();assertFalse(n.processDeregistration(),"Removed NPC "+n.getNpcId());
                    assertTrue(p.viewable(n,false),"Invisible NPC "+n.getNpcId());
                }
                var progress=HolidayEvents.progress(p,Holiday.HALLOWEEN);progress.stage=0;progress.start(0);
                for(var station:instance.layout.objects)if(station.role>=0&&station.role<3) {
                    progress.nextAction=0;
                    assertTrue(HolidayEvents.walkToObject(p,station.id,station.x,station.y));
                    for(int tick=0;tick<60;tick++){p.getNextPlayerMovement();ticks.invoke(p);}
                    assertNotEquals(0,progress.gathered&(1<<station.role),"Uncollected supply "+station.id);
                }
                assertEquals(2,progress.stage);assertEquals(7,progress.gathered);
                var host=instance.getNpcs().stream().filter(n->((HolidayNpc)n).role==-1).findFirst().orElseThrow();
                host.unregisterInstant();instance.tick(p);
                var replacement=instance.getNpcs().stream().filter(n->((HolidayNpc)n).role==-1).findFirst().orElseThrow();
                actors.add(replacement);assertNotSame(host,replacement);assertTrue(p.viewable(replacement,false));
                assertFalse(replacement.randomWalk);
                instance.leave(p);
            }
        } finally {
            for(var instance:instances)if(!instance.isDisposed())instance.dispose();
            for(var npc:actors)if(npc.getIndex()>0)npc.unregisterInstant();
            Server.getGlobalObjects().pulse();
            settings.set(null,oldSettings);config.set(null,oldConfig);
        }
    }
}
