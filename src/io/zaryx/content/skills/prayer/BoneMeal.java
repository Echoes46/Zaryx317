package io.zaryx.content.skills.prayer;

/**
 * A bone is an item that is used for training the prayer skill. A bone can be buried by clicking the item once, or the bone can be used on an alter to gain additional experience.
 *
 * <p>
 * Each bone has an item id associated as well as a base amount of experience gained when operating that bone.
 * </p>
 *
 * @author KAI - ZDS_KAI
 */
public enum BoneMeal {
    LAVA_DRAGON_BONEMEAL(11922, 175, 45),
    DRAGON_BONEMEAL(4261, 150, 25),
    WYVERN_BONEMEAL(6810, 150, 15),
    BAT_BONEMEAL(4256, 15, 2),
    REGULAR(4255, 10, 1),

    ;
    private final int itemId;
    private final int experience;
    private final int ectoTokens;

    /**
     * Creates a new {@code Bone} object that will be used in training prayer
     *  @param itemId the item id of the bone
     * @param experience the experience gained
     */
    BoneMeal(int itemId, int experience, int ectoTokens) {
        this.itemId = itemId;
        this.experience = experience;
        this.ectoTokens = ectoTokens;
    }

    /**
     * The item identification value that represents the bone
     *
     * @return the item
     */
    public int getItemId() {
        return itemId;
    }

    public int getEctoTokens() {
        return ectoTokens;
    }

    /**
     * The base experience gained from operating the bone
     *
     * @return the experience gained in the {@code Skill.PRAYER} skill
     */
    public int getExperience() {
        return experience;
    }
}