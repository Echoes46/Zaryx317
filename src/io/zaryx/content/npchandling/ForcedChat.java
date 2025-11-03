package io.zaryx.content.npchandling;

import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;


public class ForcedChat {

    private static int delay = 0;

    public static void StartForcedChat() {
        delay++;
       if (delay == 20) {
           for (NPC npc : NPCHandler.npcs) {
               if (npc == null) {
                   continue;
               }
               if (npc.getNpcId() == 5792) {
                   npc.forceChat("flash1:Premium Donator Zone");
               }
               if (npc.getNpcId() == 2112) {
                   npc.forceChat("flash3:Normal Donator Zone");
               }
               if (npc.getNpcId() == 11675) {
                   npc.forceChat("flash2:Register Your Number For Free Rewards!!");
               }
               if (npc.getNpcId() == 2309) {
                   npc.forceChat("glow3:Register Your Discord For Free Rewards!!");
               }
               if (npc.getNpcId() == 7041) {
                   npc.forceChat("I'll exchange your vote crystals & pkp points!");
               }
           }
           delay = 0;
       }
    }

}
