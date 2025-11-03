package io.zaryx.content.skills.summoning;

public enum SummoningData {
    SPIRIT_WOLF(1, 6829, 3),
    DREADFOWL(2, 6830, 4),
    STEEL_TITAN(99, 7340, 8); // Level requirement, NPC ID, and summon cost

    private final int levelRequired;
    private final int npcId;
    private final int summonCost;

    SummoningData(int levelRequired, int npcId, int summonCost) {
        this.levelRequired = levelRequired;
        this.npcId = npcId;
        this.summonCost = summonCost;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getSummonCost() {
        return summonCost;
    }
}
