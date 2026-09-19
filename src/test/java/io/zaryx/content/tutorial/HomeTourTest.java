package io.zaryx.content.tutorial;

import com.google.gson.*;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class HomeTourTest {
    Field configuration;
    Object previous;
    @BeforeEach void setup() throws Exception {
        configuration = Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        previous = configuration.get(null);
        configuration.set(null, ServerConfiguration.getDefault());
    }
    @AfterEach void restore() throws Exception { configuration.set(null, previous); }

    @Test void routeUsesCurrentHomeAndFitsNpcDialogues() {
        assertEquals(Configuration.START_LOCATION_X, HomeTour.STOPS.get(0).x);
        assertEquals(Configuration.START_LOCATION_Y, HomeTour.STOPS.get(0).y);
        for (HomeTour.Stop stop : HomeTour.STOPS) {
            assertTrue(stop.x >= 3070 && stop.x <= 3130 && stop.y >= 3470 && stop.y <= 3520, stop.name);
            assertEquals(0, stop.position().getHeight());
            assertTrue(stop.text().length >= 1 && stop.text().length <= 4, stop.name);
            for (String line : stop.text()) assertTrue(line.length() <= 62, stop.name + ": " + line);
        }
    }

    @Test void stopsAreBesideConfiguredServices() throws Exception {
        Map<String,Integer> npcIds = Map.of("Daily rewards",7042,"Companions",8208,"Shops",1577,
                "Slayer",8623,"Trading post",2897);
        Map<String,Integer> objectIds = Map.of("Reward chests",172,"Skilling and deposits",29104,
                "Prayer altar",409,"Equipment upgrades",30943,"Spellbooks",31858,
                "Restoration pool",39651,"Tournaments",31622,"Perk Paradise",4152);
        JsonArray npcs = JsonParser.parseString(Files.readString(Path.of("etc/cfg/npc/spawns/home_area.json"))).getAsJsonArray();
        List<int[]> objects = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of("etc/cfg/obj/global_objects.cfg"))) {
            line = line.split("//",2)[0].trim();
            if (line.isEmpty()) continue;
            String[] fields = line.split("\\s+");
            if (fields.length >= 4) objects.add(new int[]{Integer.parseInt(fields[0]),Integer.parseInt(fields[1]),
                    Integer.parseInt(fields[2]),Integer.parseInt(fields[3])});
        }
        for (HomeTour.Stop stop : HomeTour.STOPS) {
            if (npcIds.containsKey(stop.name)) {
                boolean found = false;
                for (JsonElement e : npcs) {
                    JsonObject n=e.getAsJsonObject(),pos=n.getAsJsonObject("position");
                    found |= n.get("id").getAsInt()==npcIds.get(stop.name)
                            && near(stop,pos.get("x").getAsInt(),pos.get("y").getAsInt(),pos.get("height").getAsInt());
                }
                assertTrue(found,stop.name);
            } else if (objectIds.containsKey(stop.name)) {
                assertTrue(objects.stream().anyMatch(o -> o[0]==objectIds.get(stop.name) && near(stop,o[1],o[2],o[3])),stop.name);
            } else assertEquals("Welcome",stop.name);
        }
    }
    boolean near(HomeTour.Stop stop,int x,int y,int height) {
        return height==0 && Math.abs(stop.x-x)<=3 && Math.abs(stop.y-y)<=3;
    }

    @Test void replayVisitsEveryStopReturnsHomeAndUnlocksWithoutChangingAccount() {
        Player p=new Player(null);
        p.setCompletedTutorial(true);
        p.getRights().setPrimary(Right.STAFF_MANAGER);
        int[] items=p.playerItems.clone();
        TutorialDialogue tour=new TutorialDialogue(p,true,true);
        p.start(tour);
        assertTrue(TutorialDialogue.inTutorial(p));
        assertTrue(p.getMovementState().isLocked());
        for (HomeTour.Stop stop : HomeTour.STOPS) {
            assertEquals(stop.x,p.getTeleportToX(),stop.name);
            assertEquals(stop.y,p.getTeleportToY(),stop.name);
            tour.sendNextDialogue();
        }
        assertEquals(Configuration.START_LOCATION_X,p.getTeleportToX());
        assertEquals(Configuration.START_LOCATION_Y,p.getTeleportToY());
        tour.sendNextDialogue();
        assertFalse(TutorialDialogue.inTutorial(p));
        assertFalse(p.getMovementState().isLocked());
        assertNull(p.getDialogueBuilder());
        assertTrue(p.isCompletedTutorial());
        assertTrue(p.getRights().contains(Right.STAFF_MANAGER));
        assertArrayEquals(items,p.playerItems);
    }

    @Test void skippedTourStillHasAnEndingAndNewPlayersRemainInSetup() {
        Player p=new Player(null);
        TutorialDialogue tour=new TutorialDialogue(p,false,false);
        p.start(tour);
        assertEquals(Configuration.START_LOCATION_X,p.getTeleportToX());
        assertEquals(Configuration.START_LOCATION_Y,p.getTeleportToY());
        assertTrue(tour.hasNext());
        tour.end();
        assertFalse(p.isCompletedTutorial());
        assertTrue(TutorialDialogue.inTutorial(p));
        assertTrue(p.getMovementState().isLocked());
    }
}
