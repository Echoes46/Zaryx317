package io.zaryx.content.holiday;

import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Position;

public final class HolidayNpc extends NPC {
    public final Holiday holiday;
    public final int role;
    public final boolean home;
    public HolidayNpc(Holiday holiday,int id,int x,int y,int role,boolean home){
        super(id,new Position(x,y,0));this.holiday=holiday;this.role=role;this.home=home;
        getBehaviour().setAggressive(false);getBehaviour().setRespawn(false);
        getCombatDefinition().setAggressive(false);walkingType=0;
    }
    @Override public boolean canBeAttacked(Entity entity){return false;}
    @Override public boolean canBeDamaged(Entity entity){return false;}
}
