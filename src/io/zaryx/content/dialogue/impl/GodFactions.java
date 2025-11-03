package io.zaryx.content.dialogue.impl;

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueExpression;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;

/**
 * Mac is an npc that sells max cape on ::maxisland.
 */
public class GodFactions extends DialogueBuilder {

    private static final int NPC = 9656;

    public GodFactions(Player player, boolean initial) {
        super(player);
        setNpcId(NPC);

        if (player.saradominFaction || player.zamorakFaction || player.guthixFaction) {
            npc(DialogueExpression.HAPPY, "Would you like to view the faction shop?");
            option(new DialogueOption("Yes, please.", GodFactions::factionShop),
                    new DialogueOption("No, thank you.", plr -> plr.getPA().closeAllWindows()));
            return;
        }
        if (player.totalLevel < 500) {
            player("Hello.");
            statement("The man glances at you and grunts something unintelligible.");
        }
        if (!player.startedFaction) {
            npc(DialogueExpression.HAPPY,"Hello, Would you like to.", "Join a God Faction and fight alongside your brothers?");
            option(new DialogueOption("Can i get some more information?", GodFactions::information),
                    new DialogueOption("No, I'm good.", plr -> plr.getPA().closeAllWindows()));
            player.startedFaction = true;
        } else {
            npc("Have you decided on a faction yet?");
            option(new DialogueOption("Yes, I have.", GodFactions::information),
                    (new DialogueOption("No, I'm still thinking.", plr -> plr.getPA().closeAllWindows())));
        }
    }

    private static void factionShop(Player player) {
        if (player.saradominFaction) {
            //player.getShops().openShop(195); //EDIT SHOP IDS & ADD SHOPS
            player.sendMessage("@red@This shop is not available yet.");
            player.getPA().closeAllWindows();
        } else if (player.zamorakFaction) {
            player.sendMessage("@red@This shop is not available yet.");
            player.getPA().closeAllWindows();
            //player.getShops().openShop(23); //EDIT SHOP IDS & ADD SHOPS
        } else if (player.guthixFaction) {
            player.sendMessage("@red@This shop is not available yet.");
            player.getPA().closeAllWindows();
            //player.getShops().openShop(198); //EDIT SHOP IDS & ADD SHOPS
        }
    }

    private static void information(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("The Current factions you can join are, ", "@blu@Saradomin, ", "the god of order and wisdom, ");
        builder.npc("Or @red@Zamorak@bla@, ", "the god of chaos and destruction,");
        builder.npc("Or @gre@Guthix@bla@, ",  "the god of balance and nature.");
        builder.option(new DialogueOption("Join @blu@Saradomin", GodFactions::Saradomin),
                new DialogueOption("Join @red@Zamorak", GodFactions::Zamorak),
                new DialogueOption("Join @gre@Guthix", GodFactions::Guthix));
        builder.next(new GodFactions(player, false));
        player.start(builder);
    }

    private static void joinSaradomin(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Welcome to the Saradomin faction, ", "may you fight with Wisdom.");
        player.saradominFaction = true;
        /*player.getPA().itemOnInterface(12637, 1, 27664, 0);
        player.getRights().add(Right.get(81));
        player.getRights().buildCrownString();*/
        player.start(builder);
    }

    private static void joinZamorak(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Welcome to the @red@Zamorak@bla@  faction, ", "may you fight with Chaos.");
        player.zamorakFaction = true;
//        player.getPA().itemOnInterface(12638, 1, 27664, 0);
//        player.getRights().add(Right.get(82));
//        player.getRights().add(Right.FACTION_Z);
        player.start(builder);
    }

    private static void joinGuthix(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Welcome to the @gre@Guthix@bla@ faction, ", "may you fight with Balance.");
        player.guthixFaction = true;
//        player.getPA().itemOnInterface(12639, 1, 27664, 0);
//        player.getRights().add(Right.FACTION_G);
//        player.getRights().add(Right.get(83));
//        player.getRights().buildCrownString();


        player.start(builder);
    }

    private static void Saradomin(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Are you sure you would like to, ", "Join Saradomin, you won't be, ", "able to change your faction.");
        builder.npc("or be able to equip items from other gods");
        builder.option(new DialogueOption("Yes, I'm sure.", GodFactions::joinSaradomin
        ), new DialogueOption("Let me think about it.", plr -> plr.getPA().closeAllWindows()));
        player.start(builder);
    }

    private static void Zamorak(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Are you sure you would like to, ", "Join Zamorak, you will not be, ", "able to change your faction.");
        builder.npc("or join another faction,", "or be able to equip items from other gods");
        builder.option(new DialogueOption("Yes, I'm sure.", GodFactions::joinZamorak),
         new DialogueOption("Let me think about it.", plr -> plr.getPA().closeAllWindows()));
        player.start(builder);
    }

    private static void Guthix(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Are you sure you would like to, ", "Join Guthix, you will not be, ", "able to change your faction.");
        builder.npc("or join another faction,", "or be able to equip items from other gods");
        builder.option(new DialogueOption("Yes, I'm sure.", GodFactions::joinGuthix),
                new DialogueOption("Let me think about it.", plr -> plr.getPA().closeAllWindows()));
        player.start(builder);
    }
}
