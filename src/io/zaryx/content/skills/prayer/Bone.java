package io.zaryx.content.skills.prayer;

import static io.zaryx.content.skills.prayer.Prayer.RESTORE_PRAYER_BABY_DRAG_WYRM;
import static io.zaryx.content.skills.prayer.Prayer.RESTORE_PRAYER_BIG_BONES;
import static io.zaryx.content.skills.prayer.Prayer.RESTORE_PRAYER_BONES;
import static io.zaryx.content.skills.prayer.Prayer.RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH;
import static io.zaryx.content.skills.prayer.Prayer.RESTORE_PRAYER_SUPER_DRAGON;

/**
 * A bone is an item that is used for training the prayer skill. A bone can be buried by clicking the item once, or the bone can be used on an alter to gain additional experience.
 * 
 * <p>
 * Each bone has an item id associated as well as a base amount of experience gained when operating that bone.
 * </p>
 * 
 * @author Jason MacKeigan
 * @date Mar 10, 2015, 2015, 3:24:22 AM
 */
public enum Bone {
	REGULAR(526, 5, RESTORE_PRAYER_BONES),
	BAT(530, 5, RESTORE_PRAYER_BONES),
	WOLF(2859, 5, RESTORE_PRAYER_BONES),
	MONKEY(3179, 5, RESTORE_PRAYER_BONES),

	BIG(532, 10, RESTORE_PRAYER_BIG_BONES),
	JOGRE(3125, 10, RESTORE_PRAYER_BIG_BONES),
	RAURG(4832, 40, RESTORE_PRAYER_BIG_BONES),

	BABY_DRAG(534, 20, RESTORE_PRAYER_BABY_DRAG_WYRM),
	WYRM(22780, 30, RESTORE_PRAYER_BABY_DRAG_WYRM),

	DRAG(536, 50, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	DRAKE(22783, 40, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	DAG(6729, 60, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	WYVERN(6812, 32, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	LAVA(11943, 70, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	HYDRA(22786, 90, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	SUPERIOR(22124, 100, RESTORE_PRAYER_SUPER_DRAGON),


	REGULARNOTED(527, 5, RESTORE_PRAYER_BONES),
	BATNOTED(531, 5, RESTORE_PRAYER_BONES),
	WOLFNOTED(2860, 5, RESTORE_PRAYER_BONES),
	MONKEYNOTED(3180, 5, RESTORE_PRAYER_BONES),
	BIGNOTED(533, 10, RESTORE_PRAYER_BIG_BONES),
	JOGRENOTED(3126, 10, RESTORE_PRAYER_BIG_BONES),
	RAURGNOTED(4833, 40, RESTORE_PRAYER_BIG_BONES),
	BABY_DRAGNOTED(535, 20, RESTORE_PRAYER_BABY_DRAG_WYRM),
	WYRMNOTED(22781, 30, RESTORE_PRAYER_BABY_DRAG_WYRM),
	DRAGNOTED(537, 30, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	DRAKENOTED(22784, 40, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	DAGNOTED(6730, 60, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	WYVERNNOTED(6813, 32, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	LAVANOTED(11944, 70, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	HYDRANOTED(22787, 90, RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH),
	SUPERIORNOTED(22125, 100, RESTORE_PRAYER_SUPER_DRAGON),

	;
	/**
	 * The item identification value for the bone
	 */
	private final int itemId;

	/**
	 * The experience gained from burying a bone
	 */
	private final int experience;

	private final int prayerRestore;

	/**
	 * Creates a new {@code Bone} object that will be used in training prayer
	 *  @param itemId the item id of the bone
	 * @param experience the experience gained
	 * @param prayerRestore
	 */
    Bone(int itemId, int experience, int prayerRestore) {
		this.itemId = itemId;
		this.experience = experience;
		this.prayerRestore = prayerRestore;
	}

	/**
	 * The item identification value that represents the bone
	 *
	 * @return the item
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * The base experience gained from operating the bone
	 *
	 * @return the experience gained in the {@code Skill.PRAYER} skill
	 */
	public int getExperience() {
		return experience;
	}

	public int getPrayerRestore() {
		return prayerRestore;
	}
}