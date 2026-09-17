package io.zaryx.content.holiday;

/** One question set per manor layout group; an unfinished round keeps its questions. */
final class HalloweenRiddles {
    private static final String[][] QUESTIONS = {
            {
                    "I follow light but flee the dark. What am I?",
                    "Face and hands, yet no arms. What am I?",
                    "Wood feeds me; water kills me. What am I?"
            },
            {
                    "I repeat words without a mouth. What am I?",
                    "I shrink as I burn. What am I?",
                    "I fall from clouds. What am I?"
            },
            {
                    "I have keys, but open no locks. What am I?",
                    "Take from me and I grow. What am I?",
                    "I can be cracked and told. What am I?"
            }
    };
    private static final String[][] ANSWERS = {
            {"A shadow", "A clock", "A fire"},
            {"An echo", "A candle", "Rain"},
            {"A piano", "A hole", "A joke"}
    };

    private HalloweenRiddles() { }

    static int setFor(int layoutSeed) {
        if (layoutSeed < HolidayLayouts.LEGACY_LAYOUTS)
            return Math.floorMod(layoutSeed, QUESTIONS.length);
        return Math.floorMod((layoutSeed - HolidayLayouts.LEGACY_LAYOUTS) / 18, QUESTIONS.length);
    }

    static String question(int layoutSeed, int role) {
        return QUESTIONS[setFor(layoutSeed)][role];
    }

    static String[] answers(int layoutSeed) {
        return ANSWERS[setFor(layoutSeed)];
    }
}
