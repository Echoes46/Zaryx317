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
public enum EctoBones {
    REGULAR(526, 4255, 5),
    BAT(530, 4256, 5),
    BIG(532, 4256, 15),
    BABY_DRAG(534, 4256, 30),
    DRAG(536, 4261, 125),
    LAVA(11943, 11922, 125),
    WYVERN(6812, 6815, 100),
    ;
    /**
     * The item identification value for the bone
     */
    private final int itemId;

    private final int boneMealId;

    /**
     * The experience gained from burying a bone
     */
    private final int experience;

    /**
     * Creates a new {@code Bone} object that will be used in training prayer
     *  @param itemId the item id of the bone
     * @param experience the experience gained
     */
    EctoBones(int itemId, int boneMealId, int experience) {
        this.itemId = itemId;
        this.boneMealId = boneMealId;
        this.experience = experience;
    }

    /**
     * The item identification value that represents the bone
     *
     * @return the item
     */
    public int getItemId() {
        return itemId;
    }

    public int getBoneMealId() {
        return boneMealId;
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