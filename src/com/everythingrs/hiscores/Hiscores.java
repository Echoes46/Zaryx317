package com.everythingrs.hiscores;

import com.everythingrs.service.call.HTTP;

/**
 * Updated by Khaos
 */
public class Hiscores {

    private static final int REAL_SKILL_COUNT = 24;
    private static final int API_SKILL_COUNT = 25;

    public static void update(String secret, String gameMode, String playerName, int rights, int[] playerXP,
                              boolean debugMessage) {
        submit(secret, gameMode, playerName, rights, getTotalLevel(playerXP), playerXP, debugMessage);
    }

    public static void update(String secret, String gameMode, String playerName, int rights, int totalLevel,
                              int[] playerXP, boolean debugMessage) {
        submit(secret, gameMode, playerName, rights, totalLevel, playerXP, debugMessage);
    }

    public static void submitHiscores(String secret, String gameMode, String playerName, int rights, int[] playerXP,
                                      boolean debugMessage) {
        submit(secret, gameMode, playerName, rights, getTotalLevel(playerXP), playerXP, debugMessage);
    }

    private static void submit(String secret, String gameMode, String playerName, int rights, int totalLevel,
                               int[] playerXP, boolean debugMessage) {
        StringBuilder builder = new StringBuilder();
        long totalExperience = 0;

        builder.append("https://everythingrs.com/api/account/hiscores/server/");
        builder.append(secret).append("/");
        builder.append(gameMode.replace(" ", "%20")).append("/");
        builder.append(playerName.replace(" ", "%20")).append("/");
        builder.append(rights).append("/");
        builder.append(totalLevel).append("/");

        for (int i = 0; i < API_SKILL_COUNT; i++) {
            int experience = 0;

            if (i < REAL_SKILL_COUNT && i < playerXP.length) {
                experience = Math.max(0, playerXP[i]);
            }

            builder.append(experience).append("/");
            totalExperience += experience;
        }

        builder.append(totalExperience).append("/");
        builder.append(calculateCombat(playerXP)).append("/");


        try {
            final String response = HTTP.connection(builder.toString());

            if (debugMessage) {
                System.out.println("EverythingRS response: " + response);
            }

            if (debugMessage && response.equalsIgnoreCase("Sucessfully added record to Hiscores")) {
                System.out.println("Successfully added record for " + playerName + " on the hiscores api.");
            }
        } catch (Exception e) {
            System.out.println("There was an error connecting to EverythingRS.com");
            e.printStackTrace();
        }
    }

    private static int calculateCombat(int[] playerXP) {
        int mage = (int) (getSafeLevel(playerXP, 6) * 1.5);
        int range = (int) (getSafeLevel(playerXP, 4) * 1.5);
        int attstr = getSafeLevel(playerXP, 0) + getSafeLevel(playerXP, 2);

        if (range > attstr && range >= mage) {
            return (int) ((getSafeLevel(playerXP, 1) * 0.25)
                    + (getSafeLevel(playerXP, 3) * 0.25)
                    + (getSafeLevel(playerXP, 5) * 0.125)
                    + (getSafeLevel(playerXP, 4) * 0.4875));
        } else if (mage > attstr && mage >= range) {
            return (int) ((getSafeLevel(playerXP, 1) * 0.25)
                    + (getSafeLevel(playerXP, 3) * 0.25)
                    + (getSafeLevel(playerXP, 5) * 0.125)
                    + (getSafeLevel(playerXP, 6) * 0.4875));
        } else {
            return (int) ((getSafeLevel(playerXP, 1) * 0.25)
                    + (getSafeLevel(playerXP, 3) * 0.25)
                    + (getSafeLevel(playerXP, 5) * 0.125)
                    + (getSafeLevel(playerXP, 0) * 0.325)
                    + (getSafeLevel(playerXP, 2) * 0.325));
        }
    }

    private static int getSafeLevel(int[] playerXP, int id) {
        if (playerXP == null || id < 0 || id >= playerXP.length) {
            return 1;
        }

        return getLevelForXP(playerXP[id]);
    }

    public static int getTotalLevel(int[] playerXP) {
        int totalLevel = 0;

        for (int i = 0; i < REAL_SKILL_COUNT; i++) {
            totalLevel += checkTotal(playerXP, i);
        }

        return totalLevel;
    }

    public static int checkTotal(int[] playerXP, int id) {
        try {
            return getLevelForXP(playerXP[id]);
        } catch (Exception e) {
            return 1;
        }
    }

    public static int getLevelForXP(int experience) {
        if (experience <= 0) {
            return 1;
        }

        int points = 0;

        for (int level = 1; level <= 99; level++) {
            points += Math.floor(level + 300.0 * Math.pow(2.0, level / 7.0));
            int requiredXp = (int) Math.floor(points / 4);

            if (requiredXp > experience) {
                return level;
            }
        }

        return 99;
    }
}