package io.zaryx.content.holiday;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.instances.*;
import io.zaryx.model.entity.player.*;
import io.zaryx.model.world.objects.GlobalObject;

/** A single account's manor; no other player's round can move its actors. */
public final class HolidayInstance extends InstancedArea {
    static final Boundary MANOR=new Boundary(3072,3328,3135,3391);
    private static final org.slf4j.Logger LOG=org.slf4j.LoggerFactory.getLogger(HolidayInstance.class);
    final HolidayEvents.Layout layout;
    private final Player owner;
    private boolean refreshPending=true;
    HolidayInstance(Player owner,HolidayEvents.Layout layout) {
        super(new InstanceConfigurationBuilder().setCloseOnPlayersEmpty(true).createInstanceConfiguration(),MANOR);
        this.owner=owner;this.layout=layout;
    }
    boolean owns(Player p,Holiday holiday){return !isDisposed()&&p==owner&&holiday==Holiday.HALLOWEEN&&p.heightLevel==getHeight();}
    void enter(Player p) {
        add(p);
        HolidayLayouts.openDoors(this,getHeight());
        for(int[] d:HolidayLayouts.DOORS)Server.getGlobalObjects().add(new GlobalObject(-1,d[1],d[2],getHeight(),d[3],0,-1).setInstance(this));
        for(var s:layout.objects)Server.getGlobalObjects().add(new GlobalObject(s.id,s.x,s.y,getHeight(),s.face,10,-1).setInstance(this));
        for(var n:layout.npcs)if(!n.home)add(new HolidayNpc(layout.holiday,n.id,n.x,n.y,getHeight(),n.role,false));
        p.moveTo(new Position(layout.entryX,layout.entryY,getHeight()));
        LOG.info("Halloween round entry: player={}, height={}, npcs={}",
                p.getLoginName(),getHeight(),getNpcs().size());
        p.sendMessage("Your private Halloween round is ready. Speak to Jack for your journal.");
    }
    void leave(Player p){p.moveTo(new Position(Configuration.START_LOCATION_X,Configuration.START_LOCATION_Y,0));if(p.getInstance()==this)remove(p);}
    @Override public void tick(io.zaryx.model.entity.Entity entity) {
        if(isDisposed() || entity!=owner || owner.getInstance()!=this)return;
        // A public-map teleport must not leave an active round attached at the wrong height.
        // Respect an in-progress teleport out of the manor instead of pulling the player back.
        if(owner.heightLevel==0 && MANOR.in(owner)
                && owner.getTeleportToX()==-1 && owner.getTeleportToY()==-1 && owner.teleTimer==0) {
            LOG.warn("Restoring Halloween instance height for {}: 0 -> {}",owner.getLoginName(),getHeight());
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
                        "Restored missing Halloween host in instance height {}",getHeight());
            }
        }
        // Starting another round can reuse both the same region and the same height.
        // In that case the client sends no map-load packet to request the new stations.
        if(refreshPending && entity==owner && owner.getInstance()==this
                && owner.getTeleportToX()==-1 && owner.getTeleportToY()==-1 && owns(owner,layout.holiday)) {
            Server.getGlobalObjects().updateRegionObjects(owner);
            refreshPending=false;
        }
    }
    @Override public void onDispose(){ }
}
