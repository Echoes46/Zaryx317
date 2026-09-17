package io.zaryx.content.holiday;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.instances.*;
import io.zaryx.model.entity.player.*;
import io.zaryx.model.world.objects.GlobalObject;

/** A single account's holiday round; no other player's round can move its actors. */
public final class HolidayInstance extends InstancedArea {
    static final Boundary MANOR=new Boundary(3072,3328,3135,3391);
    static final Boundary CHRISTMAS_GARDEN=new Boundary(2944,3328,3007,3455);
    private static final org.slf4j.Logger LOG=org.slf4j.LoggerFactory.getLogger(HolidayInstance.class);
    final HolidayEvents.Layout layout;
    private final Player owner;
    private boolean refreshPending=true;
    private boolean trackerShown;
    private int trackedSupplies=-1;
    HolidayInstance(Player owner,HolidayEvents.Layout layout) {
        super(new InstanceConfigurationBuilder().setCloseOnPlayersEmpty(true).createInstanceConfiguration(),
                layout.holiday==Holiday.HALLOWEEN?MANOR:CHRISTMAS_GARDEN);
        this.owner=owner;this.layout=layout;
    }
    boolean owns(Player p,Holiday holiday){return !isDisposed()&&p==owner&&layout.holiday==holiday&&p.heightLevel==getHeight();}
    void enter(Player p) {
        add(p);
        if(layout.holiday==Holiday.HALLOWEEN){
            HolidayLayouts.openDoors(this,getHeight());
            for(int[] d:HolidayLayouts.DOORS)Server.getGlobalObjects().add(new GlobalObject(-1,d[1],d[2],getHeight(),d[3],0,-1).setInstance(this));
        }
        for(var s:layout.objects)Server.getGlobalObjects().add(new GlobalObject(s.id,s.x,s.y,getHeight(),s.face,10,-1).setInstance(this));
        for(var n:layout.npcs)if(!n.home)add(new HolidayNpc(layout.holiday,n.id,n.x,n.y,getHeight(),n.role,false));
        p.moveTo(new Position(layout.entryX,layout.entryY,getHeight()));
        refreshSupplyTracker(p);
        LOG.info("{} round entry: player={}, height={}, npcs={}",
                layout.holiday,p.getLoginName(),getHeight(),getNpcs().size());
        p.sendMessage("Your private "+layout.holiday.title+" round is ready. Speak to the host for your journal.");
    }
    void leave(Player p){p.moveTo(new Position(Configuration.START_LOCATION_X,Configuration.START_LOCATION_Y,0));if(p.getInstance()==this)remove(p);}
    void refreshSupplyTracker(Player p) {
        if(p!=owner || p.getInstance()!=this || !owns(p,layout.holiday))return;
        if(!trackerShown) {
            p.getPA().sendString(61410,layout.holiday.title+" supplies");
            for(int i=0;i<3;i++)p.getPA().sendString(61411+i,layout.holiday.supplies[i]);
            trackerShown=true;
        }
        int gathered=HolidayEvents.progress(p,layout.holiday).gathered;
        if(gathered!=trackedSupplies){p.getPA().sendString(61414,Integer.toString(gathered));trackedSupplies=gathered;}
    }
    @Override public void remove(Player p) {
        if(p==owner && trackerShown){
            for(int id=61410;id<=61414;id++)p.getPA().sendString(id,"");
            trackerShown=false;
            trackedSupplies=-1;
        }
        super.remove(p);
    }
    @Override public void tick(io.zaryx.model.entity.Entity entity) {
        if(isDisposed() || entity!=owner || owner.getInstance()!=this)return;
        // A public-map teleport must not leave an active round attached at the wrong height.
        // Respect an in-progress teleport out of the manor instead of pulling the player back.
        Boundary area=layout.holiday==Holiday.HALLOWEEN?MANOR:CHRISTMAS_GARDEN;
        if(owner.heightLevel==0 && area.in(owner)
                && owner.getTeleportToX()==-1 && owner.getTeleportToY()==-1 && owner.teleTimer==0) {
            LOG.warn("Restoring {} instance height for {}: 0 -> {}",layout.holiday,owner.getLoginName(),getHeight());
            owner.moveTo(new Position(layout.entryX,layout.entryY,getHeight()));
            refreshPending=true;
        }
        // Jack is required to claim/exit; recover if an external NPC cleanup removes him.
        boolean hasHost=getNpcs().stream().anyMatch(n->n instanceof HolidayNpc
                && ((HolidayNpc)n).role==-1 && !n.isUnregister() && !n.isDead() && !n.needRespawn);
        if(!hasHost) {
            for(var n:new java.util.ArrayList<>(getNpcs()))
                if(n instanceof HolidayNpc && ((HolidayNpc)n).role==-1)n.unregister();
            for(var spawn:layout.npcs)if(!spawn.home && spawn.role==-1) {
                add(new HolidayNpc(layout.holiday,spawn.id,spawn.x,spawn.y,getHeight(),spawn.role,false));
                org.slf4j.LoggerFactory.getLogger(HolidayInstance.class).warn(
                        "Restored missing {} host in instance height {}",layout.holiday,getHeight());
            }
        }
        // Starting another round can reuse both the same region and the same height.
        // In that case the client sends no map-load packet to request the new stations.
        if(refreshPending && entity==owner && owner.getInstance()==this
                && owner.getTeleportToX()==-1 && owner.getTeleportToY()==-1 && owns(owner,layout.holiday)) {
            Server.getGlobalObjects().updateRegionObjects(owner);
            refreshPending=false;
        }
        refreshSupplyTracker(owner);
    }
    @Override public void onDispose(){ }
}
