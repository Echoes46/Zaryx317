package io.zaryx.model.cycleevent.impl.curses;

import io.zaryx.content.combat.formula.rework.MeleeCombatFormula;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.content.skills.Skill;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

public class Turmoil extends Event<Player> {
    double defenceboost = 0.0;
    double attackboost =   0.0;
    double strengthboost =  0.0;
    public Turmoil(String signature,Player attachment,int ticks) {
        super(signature,attachment,ticks);
    }
    @Override
    public void execute() {

        if (attachment == null || attachment.isDead || attachment.isDisconnected() || attachment.getSession() == null) {
            super.stop();
            return;
        }
        if (!attachment.prayerActive[CombatPrayer.TURMOIL]) {
            attachment.turmoilattack= 0;
            attachment.turmoilstrength= 0;
            attachment.turmoildefence= 0;
            attachment.getPA().sendString(99682,"0%");
            attachment.getPA().sendString(99683,"0%");
            attachment.getPA().sendString(99684,"0%");
            super.stop();
            return;
        }


        defenceboost=  15.0;
        attackboost =   15.0;
        strengthboost = 23.0;

        if ( attachment.underAttackByNpc > 0) {
            NPC npc = NPCHandler.npcs[attachment.underAttackByNpc ];
            if(npc !=null){
                defenceboost  +=  (MeleeCombatFormula.get().getEffectiveDefenceLevel(npc) * 0.15);
                attackboost +=   (MeleeCombatFormula.get().getEffectiveAttackLevel(npc) * 0.15);
                strengthboost +=   (MeleeCombatFormula.get().getEffectiveStrengthLevel(npc) * 0.10);


                attachment.turmoilattack = (int) (MeleeCombatFormula.get().getEffectiveAttackLevel(npc) * 0.15);
                attachment.turmoildefence = (int) (MeleeCombatFormula.get().getEffectiveDefenceLevel(npc) * 0.15);
                attachment.turmoilstrength = (int) (MeleeCombatFormula.get().getEffectiveStrengthLevel(npc) * 0.10);

            }
        }
        if ( attachment.underAttackByPlayer > 0) {
            Player theotherplayer =  PlayerHandler.players[attachment.underAttackByPlayer];
            if(theotherplayer !=null) {
                defenceboost+=   theotherplayer.playerLevel[Skill.DEFENCE.getId()] * 0.15;
                attackboost +=  theotherplayer.playerLevel[Skill.ATTACK.getId()] * 0.15;
                strengthboost +=  theotherplayer.playerLevel[Skill.STRENGTH.getId()] * 0.10;
                attachment.turmoilattack = (int) (theotherplayer.playerLevel[Skill.ATTACK.getId()]  * 0.15);
                attachment.turmoildefence = (int) (theotherplayer.playerLevel[Skill.DEFENCE.getId()]  * 0.15);
                attachment.turmoilstrength = (int) (theotherplayer.playerLevel[Skill.STRENGTH.getId()] * 0.10);
            }
        }
        attachment.getPA().sendString(99682,attackboost+"%");//just the visuals lmao...
        attachment.getPA().sendString(99683,strengthboost+"%");
        attachment.getPA().sendString(99684,defenceboost+"%");
    }

}
