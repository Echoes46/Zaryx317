package io.zaryx.content.holiday;

import java.util.List;

/** Token rewards, separate from the first-completion costumes. */
final class HolidayShop {
    static final int PAGE_SIZE = 2; // Two rewards, Previous, Next and Close fit five options.

    static final class Reward {
        final String name;
        final int cost;
        final int[] items;

        Reward(String name, int cost, int... items) {
            this.name = name;
            this.cost = cost;
            this.items = items;
        }
    }

    static final List<Reward> HALLOWEEN = List.of(
            new Reward("Grim reaper hood", 10, 12845),
            new Reward("Witch outfit", 30, 27473, 27475, 27477, 27479, 27481),
            new Reward("Ghostly cloak", 15, 6111),
            new Reward("Zombie outfit", 30, 7594, 7592, 7593, 7595, 7596),
            new Reward("Halloween armor set", 75, 33063, 33059, 33064, 33062, 33060, 33061),
            new Reward("Flying pumpkin pet", 100, 8230),
            new Reward("Jack-O-Kraken pet", 100, 8231));

    static final List<Reward> CHRISTMAS = List.of(
            new Reward("Reindeer hat", 10, 10507),
            new Reward("Snow globe", 30, 20832),
            new Reward("Christmas jumper", 15, 27566));

    static List<Reward> rewards(Holiday holiday) {
        return holiday == Holiday.HALLOWEEN ? HALLOWEEN : CHRISTMAS;
    }

    private HolidayShop() { }
}
