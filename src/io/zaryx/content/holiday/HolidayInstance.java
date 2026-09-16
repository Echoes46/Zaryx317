package io.zaryx.content.holiday;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.instances.*;
import io.zaryx.model.entity.player.*;
import io.zaryx.model.world.objects.GlobalObject;

/** A single account's manor; no other player's round can move its actors. */
public final class HolidayInstance extends InstancedArea {
    final HolidayEvents.Layout layout;
    private final Player owner;
    HolidayInstance(Player owner,HolidayEvents.Layout layout) {
        super(new InstanceConfigurationBuilder().setCloseOnPlayersEmpty(true).createInstanceConfiguration(),new Boundary(3072,3328,3135,3391));
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
        p.sendMessage("Your private Halloween round is ready. Speak to Jack for your journal.");
    }
    void leave(Player p){p.moveTo(new Position(Configuration.START_LOCATION_X,Configuration.START_LOCATION_Y,0));if(p.getInstance()==this)remove(p);}
    @Override public void onDispose(){ }
}
