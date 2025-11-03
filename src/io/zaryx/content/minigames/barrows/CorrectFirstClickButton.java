package io.zaryx.content.minigames.barrows;

import io.zaryx.model.entity.player.Player;

public class CorrectFirstClickButton {

    public static void handleButton(Player client, int doNotUse) {
        int actionButtonID = intTohex(doNotUse);

        if(client.getBarrows().getPuzzle().handleClick(client, actionButtonID)) {
            return;
        }

       // if (client.getRights().greaterOrEqual(Rights.ADMINISTRATOR)) {
           // System.out.println(": Unhandled correct button: " + actionButtonID);
     //   }
    }

    /**
     * Reverses the incorrect data type.
     */
    private static int intTohex(int hex) {
        int first = hex / 1000;
        int second = hex - (first * 1000);
        return (first << 8) | second;
    }
}