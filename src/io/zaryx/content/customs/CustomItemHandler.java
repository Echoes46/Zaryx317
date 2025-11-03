package io.zaryx.content.customs;

import io.zaryx.annotate.PostInit;
import io.zaryx.model.Graphic;
import io.zaryx.model.items.ItemAction;
public class CustomItemHandler {

    @PostInit
    public static void handleCustomItem() {

        ItemAction.registerInventory(2520, 1, ((player, item) -> {
            player.startAnimation(918);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(2522, 1, ((player, item) -> {
            player.startAnimation(919);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(2524, 1, ((player, item) -> {
            player.startAnimation(920);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(2526, 1, ((player, item) -> {
            player.startAnimation(921);
            player.forcedChat("Just say neigh to Gambling!");
        }));
        ItemAction.registerInventory(13215, 1, ((player, item) -> {
            player.startAnimation(3414);
            player.forcedChat("Grrrrr!");
        }));
        ItemAction.registerInventory(13216, 1, ((player, item) -> {
            player.startAnimation(3413);
            player.forcedChat("Grrrrr");
        }));
        ItemAction.registerInventory(13217, 1, ((player, item) -> {
            player.startAnimation(3541);
            player.forcedChat("Grrrrr");
        }));
        ItemAction.registerInventory(13218, 1, ((player, item) -> {
            player.startAnimation(3839);
            player.forcedChat("Grrrrr");
        }));
        ItemAction.registerInventory(4613, 1, ((player, item) -> {
            player.startAnimation(1902);
        }));
        ItemAction.registerInventory(3801, 1, ((player, item) -> {
            player.startAnimation(1329);
        }));
        ItemAction.registerInventory(4079, 1, ((player, item) -> {
            player.startAnimation(1457);
        }));
        ItemAction.registerInventory(4079, 2, ((player, item) -> {
            player.startAnimation(1458);
        }));
        ItemAction.registerInventory(4079, 3, ((player, item) -> {
            player.startAnimation(1459);
        }));
        ItemAction.registerInventory(13188, 1, ((player, item) -> {
            player.startAnimation(5283);
            player.startGraphic(new Graphic(1171));
        }));
        ItemAction.registerInventory(23446, 2, ((player, item) -> {
            player.startAnimation(8332);
            player.startGraphic(new Graphic(1680));
        }));
        ItemAction.registerInventory(6722, 1, ((player, item) -> {
            player.startAnimation(2840);
            player.forcedChat("Alas!");
        }));
        ItemAction.registerInventory(6722, 2, ((player, item) -> {
            player.startAnimation(2844);
            player.forcedChat("Mwuhahahaha!");
        }));
        ItemAction.registerInventory(27873, 1, (((player, item) -> {
            player.startAnimation(10045);
        })));
        ItemAction.registerInventory(716, 1, (((player, item) -> {
            player.startAnimation(908);
            player.startGraphic(new Graphic(81));
        })));
        ItemAction.registerInventory(25646, 1, (((player, item) -> {  //$100 Deal
            player.getItems().deleteItem2(25646, 1);
            player.getItems().addItemUnderAnyCircumstance(7776, 1);  //Credits
            player.getItems().addItemUnderAnyCircumstance(13346, 1);  //ultra box
            player.getItems().addItemUnderAnyCircumstance(26545, 1);  //perk
            player.getItems().addItemUnderAnyCircumstance(26886, 1);  //Overcharged cell
            player.sendMessage("Thank you for supporting Zaryx. We hope you enjoy your bundle.");
        })));//250 deal
        ItemAction.registerInventory(25649, 1, (((player, item) -> {  //$250 Deal
            player.getItems().deleteItem2(25649, 1);
            player.getItems().addItemUnderAnyCircumstance(7776, 2);   //Credits
            player.getItems().addItemUnderAnyCircumstance(7775, 1);   //Credits
            player.getItems().addItemUnderAnyCircumstance(13346, 2);  //ultra box
            player.getItems().addItemUnderAnyCircumstance(26545, 2);  //perk
            player.getItems().addItemUnderAnyCircumstance(26886, 3);  //Overcharged cell
            player.getItems().addItemUnderAnyCircumstance(6805, 1);  //fort

            player.sendMessage("Thank you for supporting Zaryx. We hope you enjoy your bundle.");
        })));

        ItemAction.registerInventory(25648, 1, (((player, item) -> {  //$750 Deal
            player.getItems().deleteItem2(25648, 1);
            player.getItems().addItemUnderAnyCircumstance(7776, 7);   //Credits
            player.getItems().addItemUnderAnyCircumstance(7775, 1);   //Credits

            player.getItems().addItemUnderAnyCircumstance(12588, 1);  //ultra box
            player.getItems().addItemUnderAnyCircumstance(6831, 1);  //ultra box
            player.getItems().addItemUnderAnyCircumstance(26545, 5);  //perk
            player.getItems().addItemUnderAnyCircumstance(26886, 5);  //Overcharged cell
            player.getItems().addItemUnderAnyCircumstance(6805, 2);  //fort
            player.sendMessage("Thank you for supporting Zaryx. We hope you enjoy your bundle.");
        })));

        ItemAction.registerInventory(25650, 1, (((player, item) -> {  //$999 Deal
            player.getItems().deleteItem2(25650, 1);
            player.getItems().addItemUnderAnyCircumstance(7776, 10);  //Credits
            player.getItems().addItemUnderAnyCircumstance(7775, 3);  //Credits
            player.getItems().addItemUnderAnyCircumstance(12588, 2);  //ultra box
            player.getItems().addItemUnderAnyCircumstance(6829, 1);  //ultra box
            player.getItems().addItemUnderAnyCircumstance(26545, 10);  //perk
            player.getItems().addItemUnderAnyCircumstance(6805, 2);  //Overcharged cell
            player.getItems().addItemUnderAnyCircumstance(26886, 5);  //Overcharged cell
            player.getItems().addItemUnderAnyCircumstance(10943, 1);  //fort


            player.sendMessage("Thank you for supporting Zaryx. We hope you enjoy your bundle.");
        })));
    }
}
