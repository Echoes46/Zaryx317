package io.zaryx.content.skills.agility.impl;

import io.zaryx.content.skills.agility.AgilityHandler;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.items.ImmutableItem;

/**
 * OSRS Agility Pyramid Course
 **/
public class AgilityPyramid {

    public static final int
            PYRAMID_OBSTACLE_1 = 10857,
            PYRAMID_OBSTACLE_2 = 10865,
            PYRAMID_OBSTACLE_3 = 10860,
            PYRAMID_OBSTACLE_4 = 10868,
            PYRAMID_GAP_1 = 10882,
            PYRAMID_LEDGE = 10886,
            PYRAMID_TOP = 10874,
            PYRAMID_REWARD = 6970,
            ROCKS_1 = 11948,
            ROCKS_2 = 11949,
            NPC_SIMON_TEMPLETON = 5786;

    public boolean agilityPyramidCourse(final Player c, final int objectId) {
        switch (objectId) {
            case ROCKS_1: // first rocks down
                if (c.getX() == 3338 && c.getY() == 2826 || c.getY() == 2827 || c.getY() == 2828 || c.getY() == 2829) {
                    AgilityHandler.delayEmote(c, "CLIMB_UP", 3334, c.getY(), 0, 2);
                }
                AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX() + 4, c.getY(), 0, 2);
                return true;
            case ROCKS_2: // second rocks down
                if (c.getX() == 3352 && c.getY() == 2827 || c.getY() == 2828 || c.getY() == 2829) {
                    AgilityHandler.delayEmote(c, "CLIMB_UP", 3348, c.getY(), 0, 2);
                }
                AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX() + 4, c.getY(), 0, 2);
                return true;

            case PYRAMID_OBSTACLE_1: // First climb-up or claim reward
                if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
                if (c.getX() == 3356 || c.getX() == 3357) {
                    c.getAgilityHandler().lapFinished(c, 5, 154, 800);
                    c.sendMessage("You've completed the Agility Pyramid and received your reward!");
                    c.getInventory().addOrDrop(new ImmutableItem(PYRAMID_REWARD, 1));
                    AgilityHandler.delayEmote(c, "CLIMB_UP", 3354, 2830, 0, 2);
                    c.getAgilityHandler().resetAgilityProgress();
                } else {
                    AgilityHandler.delayEmote(c, "CLIMB_UP", 3354, 2833, 1, 2);
                    c.getAgilityHandler().agilityProgress[0] = true;
                }
                return true;

            case PYRAMID_OBSTACLE_2: // Cross wall
                if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
                if (c.getAgilityHandler().agilityProgress[0]) {
                    AgilityHandler.delayEmote(c, "JUMP", 3355, 2850, 1, 2);
                    c.getAgilityHandler().lapProgress(c, 0, objectId);
                    c.getAgilityHandler().agilityProgress[1] = true;
                }
                return true;

            case PYRAMID_OBSTACLE_3: // Cross ledge
                if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
                if (c.getAgilityHandler().agilityProgress[1]) {
                    c.setForceMovement(3368, 2851, 1, 30, "EAST", c.getAgilityHandler().getAnimation(objectId));
                    c.getAgilityHandler().lapProgress(c, 1, objectId);
                    c.getAgilityHandler().agilityProgress[2] = true;
                }
                return true;

            case PYRAMID_OBSTACLE_4: // Walk plank
                if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
                if (c.getAgilityHandler().agilityProgress[2]) {
                    c.setForceMovement(3375, 2840, 2, 100, "SOUTH", c.getAgilityHandler().getAnimation(objectId));
                    c.getAgilityHandler().lapProgress(c, 2, objectId);
                    c.getAgilityHandler().agilityProgress[3] = true;
                }
                return true;

            case PYRAMID_GAP_1: // Jump gap
                if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
                if (c.getAgilityHandler().agilityProgress[3]) {
                    c.setForceMovement(3367, 2832, 3, 100, "WEST", c.getAgilityHandler().getAnimation(objectId));
                    c.getAgilityHandler().lapProgress(c, 3, objectId);
                    c.getAgilityHandler().agilityProgress[4] = true;
                }
                return true;

            case PYRAMID_LEDGE: // Final ledge to top
                if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
                if (c.getAgilityHandler().agilityProgress[4]) {
                    c.setForceMovement(3359, 2832, 3, 100, "WEST", c.getAgilityHandler().getAnimation(objectId));
                    c.getAgilityHandler().lapProgress(c, 4, objectId);
                    c.getAgilityHandler().agilityProgress[5] = true;
                }
                return true;
        }
        return false;
    }

    public void exchangePyramidTop(Player c) {
        if (c.inventoryContains(6970)) {
            c.getItems().deleteItem(6970, 1);
            c.getItems().addItem(995, 10_000);
            c.sendMessage("You hand in your pyramid top and receive 10k! Yay!");
        } else {
            c.sendMessage("You don't have any pyramid tops to exchange.");
        }
    }
}
