package io.zaryx.content.taskmaster;

public enum Tasks {

     //Skilling
     PRAYER_POT(10,"Craft @whi@Prayer pot's",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    OAK_TREES(50,"Cut @whi@Oak Trees",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    WILLOW_TREES(50,"Cut @whi@Willow Trees",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    MAPLE_TREES(50,"Cut @whi@Maple Trees",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    BURN_LOGS(50,"Burn @whi@Logs",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    FLETCH_BOWS(50,"Fletch @whi@bows",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    THIEVE_STALLS(75,"Steal from @whi@stalls",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    ROOFTOP_COURSES(10,"Complete @whi@Rooftop courses",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    FISH_LOBSTERS(50,"Fish @whi@Lobsters",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    COOK_SWORDFISH(50,"Cook @whi@Swordfish",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    CUT_GEMS(100,"Cut @whi@Gems",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    MINE_ORES(100,"Mine @whi@Ores",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    SMELT_BARS(50,"Smelt @whi@Bars",TaskDifficulty.EASY, TaskType.SKILLING, false, true),
    MAKE_RUNES(100,"Make @whi@Runes",TaskDifficulty.EASY, TaskType.SKILLING, false, true),

    //MEDIUM

    BARROWS_BROTHERS_1(50,"Barrows",TaskDifficulty.MEDIUM, TaskType.COMBAT, false, false),


     //Skilling
     PRAYER_POT_1(25,"Craft @whi@Prayer pot's",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    OAK_TREES_1(100,"Cut @whi@Oak Trees",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    WILLOW_TREES_1(100,"Cut @whi@Willow Trees",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    MAPLE_TREES_1(100,"Cut @whi@Maple Trees",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    BURN_LOGS_1(100,"Burn @whi@Logs",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    FLETCH_BOWS_1(100,"Fletch @whi@bows",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    THIEVE_STALLS_1(100,"Steal from @whi@stalls",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    ROOFTOP_COURSES_1(20,"Complete @whi@Rooftop courses",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    CATCH_FISH_1(150,"Catch @whi@Fish",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    COOK_FISH_1(150,"Cook @whi@Fish",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    CUT_GEMS_1(100,"Cut @whi@Gems",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    MINE_ORES_1(150,"Mine @whi@Ores",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    SMELT_BARS_1(100,"Smelt @whi@Bars",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),
    MAKE_RUNES_1(200,"Make @whi@Runes",TaskDifficulty.MEDIUM, TaskType.SKILLING, false, true),

    //HARD
    DAG_KINGS_2(25,"Dagannoth",TaskDifficulty.HARD, TaskType.COMBAT, false, false),
    SARACHNIS_2(10,"Sarachnis",TaskDifficulty.HARD, TaskType.COMBAT, false, false),
    BARREL_CHEST_2(10,"Barrelchest",TaskDifficulty.HARD, TaskType.COMBAT, false, false),
    BARROWS_BROTHERS_2(50,"Barrows",TaskDifficulty.HARD, TaskType.COMBAT, false, false),
    MOLE_2(10,"Giant Mole",TaskDifficulty.HARD, TaskType.COMBAT, false, false),
    CHAOS_FANATIC_2(10,"Chaos fanatic",TaskDifficulty.HARD, TaskType.COMBAT, false, true),
    CRAZY_ARCH_2(10,"Crazy archaeologist",TaskDifficulty.HARD, TaskType.COMBAT, false, true),
    KBD_2(10,"King Black Dragon",TaskDifficulty.HARD, TaskType.COMBAT, false, true),
     //Skilling
    MAGIC_TREES_2(50,"Cut @whi@Magic Trees",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    BURN_LOGS_2(175,"Burn @whi@Logs",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    FLETCH_BOWS_2(175,"Fletch @whi@bows",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    THIEVE_STALLS_2(175,"Steal from @whi@stalls",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    ROOFTOP_COURSES_2(35,"Complete @whi@Rooftop courses",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    FISH_SHARK_2(100,"Fish @whi@Sharks",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    COOK_FISH_2(175,"Cook @whi@Fish",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    CUT_GEMS_2(175,"Cut @whi@Gems",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    MINE_ORES_2(175,"Mine @whi@Ores",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    MAKE_RUNES_2(350,"Make @whi@Runes",TaskDifficulty.HARD, TaskType.SKILLING, false, true),
    ANY_DHIDE_2(25,"Craft @whi@d'hide body",TaskDifficulty.HARD, TaskType.SKILLING, false, true),

    //ELITE
    BANDOS_3(10,"General Graardor", TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    ZAMMY_3(10,"K'ril Tsutsaroth",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    SARA_3(10,"Commander Zilyana",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    ARMA_3(10,"Kree'arra",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    CORP_3(7,"Corporeal Beast",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    NIGHTMARE_3(7,"Nightmare",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    DAG_KINGS_3(40,"Dagannoth",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    KRAKEN_3(10,"Kraken",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    ZULRAH_3(10,"Zulrah",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    SARACHNIS_3(20,"Sarachnis",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    BARREL_CHEST_3(10,"Barrelchest",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    BARROWS_BROTHERS_3(90,"Barrows",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    MOLE_3(20,"Giant Mole",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    KQ_3(20,"Kalphite Queen",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    HYDRA_3(10,"Alchemical Hydra",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    CERB_3(10,"Cerberus",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),
    NEX_3(5,"Nex",TaskDifficulty.ELITE, TaskType.COMBAT, false, false),

    ARAPHEL_3(5,"Shadow of Araphel",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    VET_ION_3(10,"Vet'ion",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    CALLISTO_3(10,"Callisto",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    SCORPIA_3(10,"Scorpia",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    VENENATIS_3(10,"Venenatis",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    CHAOS_ELE_3(10,"Chaos elemental",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    CHAOS_FANATIC_3(10,"Chaos fanatic",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    CRAZY_ARCH_3(10,"Crazy archaeologist",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),
    KBD_3(20,"King Black Dragon",TaskDifficulty.ELITE, TaskType.COMBAT, false, true),


    //daily
    ARMA_4(20,"Kree'arra",TaskDifficulty.EASY, TaskType.COMBAT, true, false),
    ZAMMY_4(20,"K'ril Tsutsaroth",TaskDifficulty.HARD, TaskType.COMBAT, true, false),
    BANDOS_4(20,"General Graardor", TaskDifficulty.EASY, TaskType.COMBAT, true, false),
    SARA_4(20,"Commander Zilyana",TaskDifficulty.MEDIUM, TaskType.COMBAT, true, false),
    CORP_4(8,"Corporeal Beast",TaskDifficulty.EASY, TaskType.COMBAT, true, true),
    NIGHTMARE_4(8,"Nightmare",TaskDifficulty.EASY, TaskType.COMBAT, true, false),
    JADS(5,"TzTok-Jad",TaskDifficulty.EASY, TaskType.COMBAT, true, true),
    INFERNAL(1,"Inferno",TaskDifficulty.EASY, TaskType.COMBAT, true, false),
    ZULRAH_4(20,"Zulrah",TaskDifficulty.EASY, TaskType.COMBAT, true, false),
    BARROWS_BROTHERS_4(175,"Barrows",TaskDifficulty.EASY, TaskType.COMBAT, true, true),
    SARACHNIS_4(20,"Sarachnis",TaskDifficulty.EASY, TaskType.COMBAT, true, false),
    KQ_4(20,"Kalphite Queen",TaskDifficulty.MEDIUM, TaskType.COMBAT, true, false),
    COX(5,"Chambers",TaskDifficulty.MEDIUM, TaskType.COMBAT, true, false),
    NEX_4(20,"Nex",TaskDifficulty.ELITE, TaskType.COMBAT, true, false),
    CERB_4(20,"Cerberus",TaskDifficulty.MEDIUM, TaskType.COMBAT, true, false),
    TOB(5,"Theatre Of Blood",TaskDifficulty.HARD, TaskType.COMBAT, true, false),
    HYDRA_4(20,"Alchemical Hydra",TaskDifficulty.ELITE, TaskType.COMBAT, true, false),
    ARAPHEL_4(20,"Shadow of Araphel",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    VET_ION_4(20,"Vet'ion",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    CALLISTO_4(20,"Callisto",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    SCORPIA_4(20,"Scorpia",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    VENENATIS_4(20,"Venenatis",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    CHAOS_ELE_4(20,"Chaos elemental",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    CHAOS_FANATIC_4(20,"Chaos fanatic",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    CRAZY_ARCH_4(20,"Crazy archaeologist",TaskDifficulty.ELITE, TaskType.COMBAT, true, true),
    KBD_4(20,"King Black Dragon",TaskDifficulty.ELITE, TaskType.COMBAT, true, true);




    public final int max;
    public final String desc;
    public final boolean daily;
    public final boolean wildy;
    public final TaskDifficulty difficultyType;
    public final TaskType taskType;

    Tasks(int max, String desc, TaskDifficulty difficultyType, TaskType taskType, boolean daily, boolean wildy) {
        this.max = max;
        this.desc = desc;
        this.daily = daily;
        this.difficultyType = difficultyType;
        this.taskType = taskType;
        this.wildy = wildy;
    }
}
