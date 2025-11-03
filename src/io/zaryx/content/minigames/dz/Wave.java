package io.zaryx.content.minigames.dz;

import io.zaryx.util.Misc;

public class Wave {
    public static final int[][] MAIN = {
            { 1674, 2645, 8367, 2650, 137,1674, 2645, 8367, 2650, 137, 2650, 137, },// Tree Spirit's
            { 3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            {  1674, 2645, 8367,8367, 2650, 2084, 2645, 8367, 2650, 2650, 137,  2650 },// Tree Spirit's
            {3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            { 1674, 2645, 8367,8367, 2650, 2084,3021, 7278, 484, 135, 10, 415,  3021, },// Tree Spirit's
            { 3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            { 1674, 2645, 8367,8367, 2650, 2084,3021, 7278, 484, 135, 10, 415,  3021, },// Tree Spirit's
            { 1674, 2645, 8367, 2650, 137,1674, 2645, 8367, 2650, 137, 2650, 137, },// Tree Spirit's
            { 3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            { 1674, 2645, 8367,8367, 2650, 2084, 2645, 8367, 2650, 2650, 137,  2650 },// Tree Spirit's
            {3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            { 1674, 2645, 8367,8367, 2650, 2084,3021, 7278, 484, 135, 10, 415,  3021, },// Tree Spirit's
            { 3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            { 1674, 2645, 8367,8367, 2650, 2084,3021, 7278, 484, 135, 10, 415,  3021, },// Tree Spirit's
            { 1674, 2645, 8367, 2650, 137,1674, 2645, 8367, 2650, 137, 2650, 137, },// Tree Spirit's
            { 3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            {  1674, 2645, 8367,8367, 2650, 2084, 2645, 8367, 2650, 2650, 137,  2650 },// Tree Spirit's
            {3021, 7278, 484, 135, 10, 415,  3021, 7278, 484, 135, 10, 415, },// Tree Spirit's
            { 1674, 2645, 8367,8367, 2650, 2084,3021, 7278, 484, 135, 10, 415,  3021, },// Tree Spirit's
            { 7410, 2025, 1674, 2645, 8367,8367, 2650, 6766, 6766 },// Tree Spirit's

    };

    //get the npcs hp
    public static int getHp(int npc) {
        //7144, 7145, 7146,
        switch (npc) {
            case 7144:
            case 7145:
            case 7146:
            case 1673:
            case 1672:
            case 1674:
            case 1675:
            case 1676:
            case 1677:
            case 1871:
            case 244:
                return 350;
        }
        return 50 + Misc.random(250);
    }
    //get the npcs max hit
    public static int getMax(int npc) {
        switch (npc) {
            case 1673:
            case 1672:
            case 1674:
            case 1675:
            case 1676:
            case 1677:
                return 20;
            case 7144:
            case 7145:
            case 7146:
                return 15;
            case 1871:
            case 244:
                return 10;
        }
        return 5 + Misc.random(5);
    }
    //get the npcs def lvl
    public static int getDef(int npc) {
        switch (npc) {
            case 1673:
            case 1672:
            case 1674:
            case 1675:
            case 1676:
            case 1677:
                return 200;
            case 7144:
            case 7145:
            case 7146:
                return 190;
            case 1871:
            case 244:
                return 180;
        }
        return 50 + Misc.random(50);
    }
}
