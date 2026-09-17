package io.zaryx.content.holiday;

import com.google.gson.Gson;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.save.PlayerSave;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/** Startup-owned scenery and per-account holiday adventures. */
public final class HolidayEvents {
    private static HolidaySettings settings=new HolidaySettings(new Properties());
    private static final Map<Holiday,Layout> layouts=new EnumMap<>(Holiday.class);
    private static boolean started;
    public static final class Layout {
        public Holiday holiday;
        public int entryX,entryY;
        public NpcSpawn[] npcs;
        public Station[] objects;
    }
    public static final class NpcSpawn {
        public int id,x,y,role;
        public boolean home;
    }
    public static final class Station {
        public int id,x,y,role,face;
    }
    private HolidayEvents(){ }
    public static void startup() throws IOException {
        if(started)return;
        settings=HolidaySettings.load(Path.of(Server.getDataDirectory(),"cfg/holiday-events.properties"));
        try(Reader reader=Files.newBufferedReader(Path.of(Server.getDataDirectory(),"cfg/holiday-events.json"))) {
            Layout[] entries=new Gson().fromJson(reader,Layout[].class);
            for(Layout layout:entries) {
                if(layout.holiday==null || layouts.put(layout.holiday,layout)!=null)throw new IllegalArgumentException("Duplicate/invalid holiday layout");
            }
        }
        for(Holiday h:Holiday.values()) {
            Layout layout=layouts.get(h);
            validateLayout(layout);
            if(settings.enabled(h)) {
                for(NpcSpawn npc:layout.npcs)if(io.zaryx.model.definitions.NpcDef.forId(npc.id)==null)
                    throw new IllegalArgumentException("Missing holiday NPC: "+npc.id);
                for(Station station:layout.objects){
                    io.zaryx.model.collisionmap.ObjectDef object=io.zaryx.model.collisionmap.ObjectDef.getObjectDef(station.id);
                    if(object==null||object.name==null)throw new IllegalArgumentException("Missing holiday object: "+station.id);
                }
            }
        }
        for(Holiday h:Holiday.values()) {
            Layout layout=layouts.get(h);
            if(layout==null)throw new IllegalArgumentException("Missing holiday layout: "+h);
            if(!settings.enabled(h))continue;
            for(NpcSpawn npc:layout.npcs)if(npc.home)new HolidayNpc(h,npc.id,npc.x,npc.y,npc.role,true);
            System.out.println("[Holiday] "+h+" enabled, edition "+settings.edition(h));
        }
        started=true;
    }
    static void validateLayout(Layout layout) {
        if(layout==null||layout.holiday==null||layout.npcs==null||layout.objects==null)throw new IllegalArgumentException("Incomplete holiday layout");
        int[] stations=new int[4],guests=new int[3];int hosts=0,entrances=0;
        Set<String> occupied=new HashSet<>();
        for(Station o:layout.objects){
            if(o.id<=0||o.x<=0||o.y<=0||o.role< -1||o.role>3||o.face<0||o.face>3||!occupied.add(o.x+","+o.y))throw new IllegalArgumentException("Invalid holiday station");
            if(o.role>=0)stations[o.role]++;
        }
        for(NpcSpawn n:layout.npcs){
            if(n.id<=0||n.x<=0||n.y<=0||n.role< -2||n.role>2||!occupied.add(n.x+","+n.y))throw new IllegalArgumentException("Invalid holiday NPC placement");
            if(n.role>=0)guests[n.role]++;
            if(n.role==-1){if(n.home)entrances++;else hosts++;}
        }
        if(hosts!=1||entrances!=1||Arrays.stream(stations).anyMatch(n->n!=1)||Arrays.stream(guests).anyMatch(n->n!=1))
            throw new IllegalArgumentException("A holiday needs a home host, event host, three guests and four stations");
    }
    public static boolean enabled(Holiday h){return settings.enabled(h);}
    static HolidayProgress progress(Player p,Holiday h) {
        HolidayProgress state=p.holidayProgress.computeIfAbsent(h,k->new HolidayProgress());
        state.useEdition(settings.edition(h));return state;
    }
    private static boolean available(Player p,Holiday h,int x,int y,int distance) {
        return enabled(h)&&((p.getInstance()==null&&p.heightLevel==0)||(p.getInstance() instanceof HolidayInstance&&((HolidayInstance)p.getInstance()).owns(p,h)))&&Math.max(Math.abs(p.absX-x),Math.abs(p.absY-y))<=distance
                &&p.teleTimer==0&&!p.isDead&&!p.getMovementState().isLocked()&&!p.getLock().cannotInteract(p)
                &&!p.getBankPin().requiresUnlock()&&!Server.getMultiplayerSessionListener().inAnySession(p);
    }
    private static boolean near(Player p,HolidayNpc npc) {return p.getInstance()==npc.getInstance()&&p.heightLevel==npc.heightLevel&&available(p,npc.holiday,npc.absX,npc.absY,4);}
    private static void save(Player p){PlayerSave.saveGame(p);}
    private static void close(Player p){p.getPA().closeAllWindows();}
    private static void say(Player p,String... text){p.start(new DialogueBuilder(p).statement(text));}
    public static boolean clickNpc(Player p,NPC npc) {
        if(!(npc instanceof HolidayNpc))return false;
        HolidayNpc event=(HolidayNpc)npc;
        if(!near(p,event))return true;
        if(event.role==-1)host(p,event);
        else if(event.role>=0)delivery(p,event);
        else p.start(new DialogueBuilder(p).npc(npc.getNpcId(),
            "Gather supplies from the marked festival stations.",
            "Read each crafting clue carefully, then help the guests.",
            "Use ::holiday whenever you need your quest journal."));
        return true;
    }
    private static void host(Player p,HolidayNpc npc) {
        Holiday h=npc.holiday;HolidayProgress s=progress(p,h);
        p.start(new DialogueBuilder(p).npc(h.host,h.quest,
            h==Holiday.HALLOWEEN?"Three restless souls have stolen our lantern light.":"A frosty prank has scattered our Christmas supplies.",
            s.runs==0?"Will you help put things right?":"Welcome back! The festival still needs your help.")
            .option(h.title+" festival",
                new DialogueOption(npc.home?"Visit the festival":"Start / continue / claim quest",pl->{
                    if(!near(pl,npc))return;close(pl);
                    if(npc.home){enterRound(pl,h);return;}
                    else if(progress(pl,h).stage==4)claim(pl,npc);
                    else {HolidayProgress state=progress(pl,h);if(state.start(ThreadLocalRandom.current().nextInt(6))){save(pl);enterRound(pl,h);return;}journal(pl,h);}
                }),
                new DialogueOption("Quest journal and event pouch",pl->{if(near(pl,npc))journal(pl,h);}),
                new DialogueOption("Festival reward shop ("+s.tokens+" tokens)",pl->{if(near(pl,npc))shop(pl,npc);}),
                new DialogueOption("Return home",pl->{if(near(pl,npc)){close(pl);if(pl.getInstance() instanceof HolidayInstance){((HolidayInstance)pl.getInstance()).leave(pl);return;}pl.getPA().spellTeleport(Configuration.START_LOCATION_X,Configuration.START_LOCATION_Y,0,false);}})));
    }
    static void enterRound(Player p,Holiday holiday) {
        HolidayProgress state=progress(p,holiday);
        if(state.stage==0){state.start(ThreadLocalRandom.current().nextInt(6));save(p);}
        // Reserve the destination height while the old round still owns its height.
        // Reusing the same height in one tick can leave the client's old stations visible.
        HolidayInstance next=new HolidayInstance(p,HolidayLayouts.round(layouts.get(holiday),state.layoutSeed));
        if(p.getInstance() instanceof HolidayInstance)((HolidayInstance)p.getInstance()).remove(p);
        next.enter(p);
    }
    /** Re-enter a saved round from its festival area or home host, never from another activity. */
    private static void resumeRound(Player p,Holiday holiday) {
        Layout layout=layouts.get(holiday);
        if(!enabled(holiday)||layout==null)return;
        boolean atEntrance=Arrays.stream(layout.npcs).anyMatch(n->n.home
                && Math.max(Math.abs(p.absX-n.x),Math.abs(p.absY-n.y))<=4);
        boolean atFestival=(holiday==Holiday.HALLOWEEN?HolidayInstance.MANOR:HolidayInstance.CHRISTMAS_GARDEN).in(p);
        if((!atEntrance&&!atFestival) || (p.getInstance()!=null && !(p.getInstance() instanceof HolidayInstance))
                || p.teleTimer!=0 || p.isDead || p.getMovementState().isLocked() || p.getLock().cannotInteract(p)
                || p.getBankPin().requiresUnlock() || Server.getMultiplayerSessionListener().inAnySession(p)) {
            say(p,"Speak to "+(holiday==Holiday.HALLOWEEN?"Jack":"Santa")+" at home to resume your "+holiday.title+" round.");return;
        }
        close(p);enterRound(p,holiday);
    }
    public static void resumeHalloween(Player p) {resumeRound(p,Holiday.HALLOWEEN);}
    public static void resumeChristmas(Player p) {resumeRound(p,Holiday.CHRISTMAS);}
    public static void journal(Player p,Holiday h) {
        if(!enabled(h)){say(p,h.title+" is not currently enabled.");return;}
        HolidayProgress s=progress(p,h);
        String goal;
        switch(s.stage){
            case 1: goal="Collect supplies at all three festival stations.";break;
            case 2: goal=h==Holiday.HALLOWEEN?"Use the cauldron and follow the ritual clues.":"Use the present table to assemble the toys.";break;
            case 3: goal=h==Holiday.HALLOWEEN?"Solve each of the three ghosts' riddles.":"Deliver the right gift to each of three helpers.";break;
            case 4: goal="Return to "+(h==Holiday.HALLOWEEN?"Jack":"Santa")+" at the festival to claim.";break;
            default:goal="Speak to the festival host to begin a round.";
        }
        List<DialogueOption> options=new ArrayList<>();
        for(int i=0;i<3;i++) {
            final int index=i;
            options.add(new DialogueOption(h.supplies[i]+((s.gathered&(1<<i))!=0?" - collected":" - missing"),pl->pouch(pl,h,index)));
        }
        options.add(new DialogueOption("Resume private "+h.title+" round",pl->resumeRound(pl,h)));
        options.add(new DialogueOption("Close",HolidayEvents::close));
        p.start(new DialogueBuilder(p).statement(h.quest,goal,"Completed rounds: "+s.runs+" | Festival tokens: "+s.tokens)
                .option("Event pouch (account-bound)",options.toArray(new DialogueOption[0])));
    }

    private static void pouch(Player p,Holiday h,int index){
        HolidayProgress s=progress(p,h);
        p.start(new DialogueBuilder(p).itemStatement(h.supplies[index],h.supplyItems[index],
            (s.gathered&(1<<index))!=0?"Your event pouch holds this supply.":"Find this supply at its festival station.",
            "Quest supplies cannot be traded or lost on death."));
    }
    /** Route to this round's station instead of trusting the client's cached object footprint. */
    public static boolean walkToObject(Player p,int id,int x,int y) {
        if (!(p.getInstance() instanceof HolidayInstance)) {
            for(Holiday holiday:Holiday.values()) {
                Layout base=layouts.get(holiday);
                boolean inArea=(holiday==Holiday.HALLOWEEN?HolidayInstance.MANOR:HolidayInstance.CHRISTMAS_GARDEN).in(p);
                if(p.getInstance()==null && enabled(holiday) && base!=null && inArea
                        && Arrays.stream(base.objects).anyMatch(station->station.role>=0 && station.x==x && station.y==y)
                        && Arrays.stream(base.objects).anyMatch(station->station.role>=0 && station.id==id)) {
                    p.sendMessage("You are outside your private "+holiday.title+" round.");
                    p.sendMessage("Use ::holiday, choose "+holiday.title+", then resume your round.");
                    return true;
                }
            }
            return false;
        }
        HolidayInstance instance=(HolidayInstance)p.getInstance();
        Holiday holiday=instance.layout.holiday;
        if (!instance.owns(p,holiday)) return false;
        for (Station station:instance.layout.objects) {
            if (station.x!=x || station.y!=y || station.role<0) continue;
            if(!available(p,holiday,x,y,32))return true;
            if (station.id!=id) {
                Server.getGlobalObjects().updateRegionObjects(p);
                p.sendMessage("The festival stations have refreshed. Please click the station again.");
                return true;
            }
            var object=io.zaryx.model.entity.player.packets.ClickObject.getObject(p,id,x,y);
            if(object==null)return true;
            var size=object.getObjectSize();
            io.zaryx.model.entity.player.PathFinder.getPathFinder().findRoute(p,x,y,true,size.getX(),size.getY());
            p.setTickable(new io.zaryx.model.tickable.impl.WalkToTickable(p,
                    new io.zaryx.model.entity.player.Position(x,y,p.heightLevel),size.getX(),size.getY(),pl->{
                if(pl.getInstance()!=instance || !instance.owns(pl,holiday))return;
                pl.facePosition(x,y);
                clickObject(pl,id,x,y);
            }));
            return true;
        }
        return false;
    }
    public static boolean clickObject(Player p,int id,int x,int y) {
        for(Holiday h:Holiday.values()) {
            Layout layout=p.getInstance() instanceof HolidayInstance?((HolidayInstance)p.getInstance()).layout:null;
            if(layout==null||layout.holiday!=h||!((HolidayInstance)p.getInstance()).owns(p,h))continue;
            for(Station station:layout.objects)if(station.id==id&&station.x==x&&station.y==y) {
                if(!available(p,h,x,y,5))return true;
                if(station.role>=0&&station.role<3)gather(p,h,station);
                else if(station.role==3)puzzle(p,h,station);
                else journal(p,h);
                return true;
            }
        }
        return false;
    }
    private static void gather(Player p,Holiday h,Station station) {
        HolidayProgress s=progress(p,h);
        if(System.currentTimeMillis()<s.nextAction)return;
        if(!s.gather(station.role)){journal(p,h);return;}
        s.nextAction=System.currentTimeMillis()+1500;
        p.startAnimation(832);save(p);
        if(p.getInstance() instanceof HolidayInstance)((HolidayInstance)p.getInstance()).refreshSupplyTracker(p);
        p.start(new DialogueBuilder(p).itemStatement("Festival supplies",h.supplyItems[station.role],
            "You collect "+h.supplies[station.role].toLowerCase(Locale.ROOT)+" for your event pouch.",
            s.stage==2?"All supplies found! Visit the crafting station.":"Find the other marked festival stations."));
    }
    private static void puzzle(Player p,Holiday h,Station station) {
        HolidayProgress s=progress(p,h);if(s.stage!=2){journal(p,h);return;}
        int expectedStep=s.puzzle;
        String[] halloweenClues={"Find the harvest's orange heart.","Listen for the rattle of a midnight flier.","Add the ooze of a restless spirit."};
        String[] christmasClues={"Pack the cold white stuffing.","Add something that rings with festive cheer.","Fit the wooden toy into its wrapping."};
        String clue=(h==Holiday.HALLOWEEN?halloweenClues:christmasClues)[s.nextIngredient()];
        List<DialogueOption> options=new ArrayList<>();
        for(int i=0;i<3;i++){final int index=i;options.add(new DialogueOption(h.supplies[i],pl->{
            if(!available(pl,h,station.x,station.y,5))return;
            HolidayProgress state=progress(pl,h);if(state.stage!=2||state.puzzle!=expectedStep)return;
            boolean correct=state.mix(index);save(pl);
            if(!correct)say(pl,h==Holiday.HALLOWEEN?"The mixture fizzles! Follow the clues and try again.":"That assembly does not fit! Start the toy again.","Your supplies remain in the event pouch.");
            else if(state.stage==3)say(pl,h==Holiday.HALLOWEEN?"The lanterns glow! Now help the three ghosts.":"Three gifts are ready! Find the three helpers.","Speak to each one and listen to their request.");
            else puzzle(pl,h,station);
        }));}
        p.start(new DialogueBuilder(p).statement(h==Holiday.HALLOWEEN?"The lantern ritual":"Santa's assembly instructions",clue,"Step "+(expectedStep+1)+" of 3").option("Choose a supply",options.toArray(new DialogueOption[0])));
    }
    private static void delivery(Player p,HolidayNpc npc) {
        Holiday h=npc.holiday;HolidayProgress s=progress(p,h);int role=npc.role;
        if(s.stage!=3){journal(p,h);return;}
        if((s.delivered&(1<<role))!=0){say(p,"Thank you! Please help the other festival guests.");return;}
        int roundSeed=s.layoutSeed;
        String clue=h==Holiday.HALLOWEEN?HalloweenRiddles.question(roundSeed,role):
                new String[]{"I need something that marches to guard the workshop.","I promised a little friend a toy that meows.","I want a gift that shows a tiny winter world."}[role];
        String[] answers=h==Holiday.HALLOWEEN?HalloweenRiddles.answers(roundSeed):new String[]{"Toy soldier","Toy cat","Snow globe"};
        DialogueOption[] options=new DialogueOption[3];
        for(int i=0;i<3;i++){final int choice=i;options[i]=new DialogueOption(answers[i],pl->{
            if(!near(pl,npc))return;HolidayProgress state=progress(pl,h);
            if(state.stage!=3||state.layoutSeed!=roundSeed||(state.delivered&(1<<role))!=0)return;
            if(choice!=role){say(pl,h==Holiday.HALLOWEEN?"That is not the answer. Listen to my riddle again.":"That gift belongs to someone else. Listen to my request.");return;}
            state.deliver(role);save(pl);
            say(pl,h==Holiday.HALLOWEEN?"The ghost's lantern lights up. Its soul is at peace.":"The helper unwraps the perfect Christmas gift!",
                state.stage==4?"All three helped! Return to the festival host.":"Keep going! Other guests still need your help.");
        });}
        p.start(new DialogueBuilder(p).npc(npc.getNpcId(),clue).option(h==Holiday.HALLOWEEN?"Answer the ghost":"Choose a wrapped gift",options));
    }
    private static void claim(Player p,HolidayNpc npc) {
        if(!near(p,npc)||npc.home)return;
        HolidayProgress s=progress(p,npc.holiday);if(s.stage!=4)return;
        boolean first=s.runs==0;
        if(first&&p.getItems().freeSlots()<npc.holiday.rewards.length){say(p,"You need "+npc.holiday.rewards.length+" free inventory slots.","Your completed quest will wait until you have room.");return;}
        if(!s.complete())return;
        if(first)for(int id:npc.holiday.rewards)p.getItems().addItem(id,1);
        save(p);
        if(p.getInstance() instanceof HolidayInstance)((HolidayInstance)p.getInstance()).refreshSupplyTracker(p);
        say(p,"Quest complete: "+npc.holiday.quest,"You earned 5 festival tokens.",first?"Your festive costume has been added to your inventory.":"Thank you for helping the festival again!","Speak to me to replay or browse the cosmetic rewards.");
    }
    private static void shop(Player p,HolidayNpc npc) {
        Holiday h=npc.holiday;
        int[][] items=h==Holiday.HALLOWEEN?new int[][]{{9925},{27473,27475,27477,27479,27481},{6111}}:new int[][]{{10507},{20832},{27566}};
        String[] names=h==Holiday.HALLOWEEN?new String[]{"Skeleton mask","Witch outfit","Ghostly cloak"}:new String[]{"Reindeer hat","Snow globe","Christmas jumper"};
        int[] costs={10,30,15};
        List<DialogueOption> options=new ArrayList<>();
        for(int i=0;i<items.length;i++){final int slot=i;options.add(new DialogueOption(names[i]+" - "+costs[i]+" tokens",pl->{
            if(!near(pl,npc))return;HolidayProgress state=progress(pl,h);
            if(state.tokens<costs[slot]){say(pl,"You need "+costs[slot]+" festival tokens for that reward.");return;}
            if(pl.getItems().freeSlots()<items[slot].length){say(pl,"Please make "+items[slot].length+" free inventory slots first.");return;}
            state.tokens-=costs[slot];for(int id:items[slot])pl.getItems().addItem(id,1);save(pl);close(pl);
            pl.sendMessage("You receive "+names[slot]+". Remaining festival tokens: "+state.tokens);
        }));}
        options.add(new DialogueOption("Close",HolidayEvents::close));
        p.start(new DialogueBuilder(p).option(h.title+" cosmetics | Tokens: "+progress(p,h).tokens,options.toArray(new DialogueOption[0])));
    }
}
