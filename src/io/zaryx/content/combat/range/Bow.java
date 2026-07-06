package io.zaryx.content.combat.range;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import io.zaryx.model.entity.player.Player;

public enum Bow {

    /*
     * Standard shortbows / longbows
     */
    SHORTBOW(841, Arrow.IRON),
    LONGBOW(839, Arrow.IRON),

    OAK_SHORTBOW(843, Arrow.STEEL),
    OAK_LONGBOW(845, Arrow.STEEL),

    WILLOW_SHORTBOW(849, Arrow.MITHRIL),
    WILLOW_LONGBOW(847, Arrow.MITHRIL),

    MAPLE_SHORTBOW(853, Arrow.ADAMANT),
    MAPLE_LONGBOW(851, Arrow.ADAMANT),

    YEW_SHORTBOW(857, Arrow.RUNE),
    YEW_LONGBOW(855, Arrow.RUNE),

    MAGIC_SHORTBOW(861, Arrow.AMETHYST),
    MAGIC_LONGBOW(859, Arrow.AMETHYST),


    /*
     * Composite bows
     */
    WILLOW_COMP_BOW(10280, Arrow.MITHRIL),
    YEW_COMP_BOW(10282, Arrow.RUNE),
    MAGIC_COMP_BOW(10284, Arrow.AMETHYST),


    /*
     * Special / clue / cosmetic-style bows that still use normal arrows
     */
    CURSED_GOBLIN_BOW(11707, Arrow.IRON),
    RAIN_BOW(23357, Arrow.IRON),

    BONE_SHORTBOW(8880, Arrow.AMETHYST),
    SEERCULL(6724, Arrow.AMETHYST),

    DARK_BOW(11235, Arrow.DRAGON),
    DARK_BOW_GREEN(12765, Arrow.DRAGON),
    DARK_BOW_BLUE(12766, Arrow.DRAGON),
    DARK_BOW_YELLOW(12767, Arrow.DRAGON),
    DARK_BOW_WHITE(12768, Arrow.DRAGON),

    THIRD_AGE_BOW(12424, Arrow.DRAGON),

    TWISTED_BOW(20997, Arrow.DRAGON),
    VENATOR_BOW(27610, Arrow.DRAGON),
    SCORCHING_BOW(29599, Arrow.DRAGON),


    /*
     * Custom / server-specific bows already present in your source.
     */
    STARTER_BOW(22333, Arrow.STEEL),
    CANDY_TWISTED_BOW(33160, Arrow.DRAGON),
    BECKONING_BOW(33005, Arrow.DRAGON),


    /*
     * Special ranged weapon in your source.
     * This is not a normal bow, but keeping it here because your existing code had it.
     */
    ECLIPSE_ATLATL(29000, Arrow.ATLATL_DART),

    MAGIC_SHORTBOW_I(12788, Arrow.AMETHYST);

    private final int bowId;
    private final Arrow maxArrow;

    /**
     * Construct an enum entry.
     *
     * @param bowId    The item id of the bow.
     * @param maxArrow The best {@link Arrow} the bow can use.
     */
    Bow(int bowId, Arrow maxArrow) {
        this.bowId = bowId;
        this.maxArrow = maxArrow;
    }

    /**
     * Unmodifiable Set of the enum.
     */
    private static final Set<Bow> VALUES = Collections.unmodifiableSet(EnumSet.allOf(Bow.class));

    /**
     * Test whether a given bow can use a given arrow.
     *
     * @param c The player.
     * @return True if the arrow can be used with the equipped bow.
     */
    public static boolean canUseArrow(Player c) {
        int bowId = c.playerEquipment[Player.playerWeapon];
        int arrowId = c.playerEquipment[Player.playerArrows];

        Optional<Bow> bow = VALUES.stream().filter(b -> b.bowId == bowId).findFirst();
        Optional<Arrow> arrow = Arrow.getArrow(arrowId);

        if (bow.isPresent() && arrow.isPresent()) {
            if (bow.get().maxArrow == arrow.get()) {
                return true;
            }

            try {
                return Arrow.indexOf(arrow.get()) <= Arrow.indexOf(bow.get().maxArrow);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return false;
    }

}