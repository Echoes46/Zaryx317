package io.zaryx.content.skills.summoning;

import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Player;

public class Summoning {

    public static int npcId;
    public static void summonFamiliar(Player player, int npcId) {
        if (player.getSummonedFamiliar() != null) {
            player.sendMessage("You already have a familiar summoned.");
            return;
        }

        NPCSpawning.spawnPet(player, npcId, player.absX + 1, player.absY + 1,
                player.getHeight(), 0, true, false, true);

        player.sendMessage("You have summoned a " + NpcDef.forId(npcId).getName() + ".");
        Summoning.npcId = npcId;
        applyBonus(player, npcId);
    }

    public static void dismissFamiliar(Player player) {
        if (player.getSummonedFamiliarID() == 0) {
            player.sendMessage("You do not have a familiar summoned.");
            return;
        }

        NPCHandler.despawn(player.getSummonedFamiliarID(), player.getHeight());
        player.setSummonedFamiliarID(-1);
        player.sendMessage("Your familiar has been dismissed.");
        Summoning.npcId = -1;
    }

    public static void applyBonus(Player player, int npcId) {
        switch(npcId) {
            // add specific npc ids here for what u want to have buffs
            // example: case 6000: player.buffDropRate(0.25)
        }
    }

    public static void removeBonus(Player player, int npcId) {
        switch(npcId) {
            // add same npc ids here and de-apply buff
            // example: case 6000: player.lowerDropRate(0.25)
        }
    }
}
