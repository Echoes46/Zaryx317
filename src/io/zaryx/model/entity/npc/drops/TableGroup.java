package io.zaryx.model.entity.npc.drops;

import io.zaryx.content.bosses.nightmare.Nightmare;
import io.zaryx.content.combat.death.NPCDeath;
import io.zaryx.content.perky.Perks;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.Range;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TableGroup extends ArrayList<Table> {

    private final List<Integer> npcIds;

    public TableGroup(List<Integer> npcsIds) {
        this.npcIds = npcsIds;
    }

    public List<GameItem> access(Player player, NPC npc, double modifier, int repeats, int npcId) {
        List<GameItem> items = new ArrayList<>();

        modifier = normalizeModifier(modifier);

        for (Table table : this) {
            TablePolicy policy = table.getPolicy();

            if (npc instanceof Nightmare) {
                Nightmare nightmare = (Nightmare) npc;
                if (nightmare.getRareRollPlayers().isEmpty()) {
                    int players = nightmare.getInstance() == null ? 0 : nightmare.getInstance().getPlayers().size();
                    System.err.println("No players on nightmare roll table, but " + players + " in instance.");
                } else if (!nightmare.getRareRollPlayers().contains(player) && (policy == TablePolicy.RARE || policy == TablePolicy.VERY_RARE || policy == TablePolicy.EXTREMELY_RARE || policy == TablePolicy.NOMAD)) {
                    continue;
                }
            }

            if (policy == TablePolicy.NOMAD) {
                // One currency roll per kill, even when ordinary loot has extra rolls.
                if (repeats > 0 && Misc.preciseRandom(Range.between(0.0, 100.0))
                        <= Math.min(100.0, 100.0 * effectiveMultiplier(policy, modifier) / table.getAccessibility())) {
                    Drop drop = table.fetchRandom();
                    if (drop == null) continue;
                    int amount = drop.getMinimumAmount() + Misc.random(drop.getMaximumAmount() - drop.getMinimumAmount());
                    if (player != null && player.doubleDropRate > 0) amount *= 2;
                    items.add(new GameItem(drop.getItemId(), Math.min(amount, nomadQuantityCap(drop.getItemId()))));
                }
                continue;
            }

            if (policy.equals(TablePolicy.CONSTANT)) {
                // Every entry in a constant table is guaranteed. Selecting one at random caused
                // NPCs such as dragons to drop either their bones or hide instead of both.
                for (Drop drop : table) {
                    int minimumAmount = drop.getMinimumAmount();
                    items.add(new GameItem(drop.getItemId(), minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount)));
                }
            } else {
                for (int i = 0; i < repeats; i++) {
                    double chance = (100D * effectiveMultiplier(policy, modifier)) / table.getAccessibility();
                    chance = Math.min(chance, 100D);

                    double roll = Misc.preciseRandom(Range.between(0.0, 100.0));

                    if (roll <= chance) {
                        Drop drop = table.fetchRandom();
                        if (drop == null) continue;
                        int minimumAmount = drop.getMinimumAmount();
                        int finalAmount = minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount);

                        if (player.doubleDropRate > 0) {
                            finalAmount *= 2;
                        }

                        GameItem item = new GameItem(drop.getItemId(), finalAmount);

                        boolean isRareDrop = policy.equals(TablePolicy.VERY_RARE) || policy.equals(TablePolicy.EXTREMELY_RARE);
                        if (isRareDrop) {
                            player.getCollectionLog().handleDrop(player, drop.getNpcIds().get(0), item.getId(), item.getAmount());
                            String itemName = ItemDef.forId(item.getId()).getName();
                            String message = "<img=18> [DROP] " + player.getDisplayName() + " has received " + item.getAmount() + "x " + itemName + " from " + npc.getDefinition().getName() + "!";
                            new Broadcast(message).submit();
                        }

                        if (item.getId() == 33169 || item.getId() == 33163) {
                            player.getCollectionLog().handleDrop(player, 10, item.getId(), item.getAmount());
                        }

                        // Count each rolled item at most once, even if it matches several announcement rules.
                        boolean announced = false;
                        for (int i1 = 0; i1 < Perks.values().length; i1++) {
                            if (item.getId() == Perks.values()[i1].itemID) {
                                NPCDeath.announce(player, item, npcId);
                                announced = true;
                                isRareDrop = false;
                                break;
                            }
                        }

                        // Always announce certain items
                        String itemNameLowerCase = ItemDef.forId(item.getId()).getName().toLowerCase();
                        if (!announced && isAlwaysAnnouncedDrop(item.getId(), itemNameLowerCase)) {
                            NPCDeath.announce(player, item, npcId);
                            announced = true;
                        }

                        items.add(item);
                        if (isRareDrop && !announced) {
                            // Custom announcement logic
                            String name = itemNameLowerCase;

                            // List of items that should never announce
                            if (!name.contains("cowhide") && !name.contains("feather") && !name.contains("dharok")
                                    && !name.contains("logs") && !name.contains("guthan") && !name.contains("bronze")
                                    && !name.contains("karil") && !name.contains("ahrim") && !name.contains("verac")
                                    && !name.contains("torag") && !name.contains("arrow") && !name.contains("shield")
                                    && !name.contains("staff") && !name.contains("iron") && !name.contains("black")
                                    && !name.contains("steel") && !name.contains("rune warhammer") && !name.contains("rock-shell")
                                    && !name.contains("eye of newt") && !name.contains("silver ore") && !name.contains("spined")
                                    && !name.contains("wine of zamorak") && !name.contains("rune spear") && !name.contains("grimy")
                                    && !name.contains("skeletal") && !name.contains("jangerberries") && !name.contains("goat horn dust")
                                    && !name.contains("yew roots") && !name.contains("white berries") && !name.contains("bars")
                                    && !name.contains("blue dragonscales") && !name.contains("kebab") && !name.contains("potato")
                                    && !name.contains("shark") && !name.contains("red") && !name.contains("spined body")
                                    && !name.contains("prayer") && !name.contains("anchovy") && !name.contains("runite")
                                    && !name.contains("adamant") && !name.contains("magic roots") && !name.contains("earth battlestaff")
                                    && !name.contains("torstol") && !name.contains("dragon battle axe") && !name.contains("helm of neitiznot")
                                    && !name.contains("mithril") && !name.contains("sapphire") && !name.contains("rune")
                                    && !name.contains("toktz") && !name.contains("steal") && !name.contains("seed")
                                    && !name.contains("ancient") && !name.contains("monk") && !name.contains("splitbark")
                                    && !name.contains("pure") && !name.contains("zamorak robe") && !name.contains("null")
                                    && !name.contains("essence") && !name.contains("crushed") && !name.contains("snape")
                                    && !name.contains("unicorn") && !name.contains("mystic") && !name.contains("eye patch")
                                    && !name.contains("steel darts") && !name.contains("steel bar") && !name.contains("limp")
                                    && !name.contains("darts") && !name.contains("dragon longsword") && !name.contains("dust battlestaff")
                                    && !name.contains("granite") && !name.contains("coal") && !name.contains("crystalline key")
                                    && !name.contains("leaf-bladed sword") && !name.contains("dragon plateskirt") && !name.contains("dragon platelegs")
                                    && !name.contains("dragon scimitar") && !name.contains("abyssal head") && !name.contains("cockatrice head")
                                    && !name.contains("dragon chainbody") && !name.contains("dragon battleaxe") && !name.contains("dragon boots")
                                    && !name.contains("overload") && !name.contains("bones") && !name.contains("granite shield")
                                    && !name.contains("granite body") && !name.contains("granite helm") && !name.contains("greanite legs")
                                    && !name.contains("barrlchest anchor") && !name.contains("rune med helm") && !name.contains("dragon med helm")
                                    && !name.contains("red spiders' eggs") && !name.contains("rune battleaxe") && !name.contains("granite maul")
                                    && !name.contains("casket") && !name.contains("ballista limbs") && !name.contains("ballista spring")
                                    && !name.contains("light frame") && !name.contains("heavy frame") && !name.contains("monkey tail")
                                    && !name.contains("shield left half") && !name.contains("clue scroll (master)") && !name.contains("dragon axe")
                                    && !name.contains("the unbearable's key") && !name.contains("corrupted ork's key") && !name.contains("mystic steam staff")
                                    && !name.contains("dragon spear") && !name.contains("ancient staff") && !name.contains("mysterious emblem")
                                    && !name.contains("ancient emblem") && !name.contains("pkp ticket") && !name.contains("crystal body")
                                    && !name.contains("crystal helm") && !name.contains("crystal legs") && !name.contains("dharok's helm")
                                    && !name.contains("dharok's greataxe") && !name.contains("dharok's platebody") && !name.contains("dharok's platelegs")
                                    && !name.contains("verac's flail") && !name.contains("verac's helm") && !name.contains("verac's brassard")
                                    && !name.contains("verac's plateskirt") && !name.contains("guthan's warspear") && !name.contains("guthan's helm")
                                    && !name.contains("guthan's platebody") && !name.contains("guthan's chainskirt") && !name.contains("ahrim's hood")
                                    && !name.contains("ahrim's staff") && !name.contains("ahrim's robetop") && !name.contains("ahrim's robeskirt")
                                    && !name.contains("karil's coif") && !name.contains("karil's crossbow") && !name.contains("karil's leathertop")
                                    && !name.contains("karil's leatherskirt") && !name.contains("torag's hammers") && !name.contains("torag's helm")
                                    && !name.contains("torag's platebody") && !name.contains("torag's platelegs") && !name.contains("rune boots")
                                    && !name.contains("rune longsword") && !name.contains("rune platebody") && !name.contains("adamant platelegs")
                                    && !name.contains("dragon mace") && !name.contains("dragon dagger") && !name.contains("mystic robe top")
                                    && !name.contains("rune chainbody") && !name.contains("rune pickaxe") && !name.contains("grimy dwarf weed")
                                    && !name.contains("brine sabre") && !name.contains("godsword shard 1") && !name.contains("godsword shard 2")
                                    && !name.contains("godsword shard 3") && !name.contains("poison ivy seed") && !name.contains("cactus seed")
                                    && !name.contains("avantoe seed") && !name.contains("kwuarm seed") && !name.contains("snapdragon seed")
                                    && !name.contains("cadantine seed") && !name.contains("lantadyme seed") && !name.contains("dwarf weed seed")
                                    && !name.contains("coins") && !name.contains("pure essence") && !name.contains("dragon bones")
                                    && !name.contains("magic logs") && !name.contains("runite ore") && !name.contains("runite bar")
                                    && !name.contains("divine super combat potion(4)") && !name.contains("lava dragon bones")
                                    && !name.contains("saradomin brew(4)") && !name.contains("bloodier key") && !name.contains("mystery box")
                                    && !name.contains("10,000 nomad point certificate") && !name.contains("amulet of the damned")
                                    && !isExcludedRareDropId(item.getId())) {
                                NPCDeath.announce(player, item, npcId);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    static int nomadQuantityCap(int itemId) {
        switch (itemId) {
            case 691: case 692: case 693: case 696: case 33428: case 33429:
                return 25;
            case 33237:
                return 2;
            default:
                throw new IllegalArgumentException("Not a Nomad currency item: " + itemId);
        }
    }

    static boolean isAlwaysAnnouncedDrop(int itemId, String name) {
        return name.contains("archers ring") || name.contains("vasa minirio")
                || (name.contains("hydra") && !name.contains("hydra bone"))
                || name.contains("skeletal visage")
                || itemId == 26358 || itemId == 26360 || itemId == 26362 || itemId == 26364;
    }

    static boolean isExcludedRareDropId(int itemId) {
        return itemId == 23490 || itemId == 23491 || itemId == 23083 || itemId == 23084;
    }

    /** The caller supplies 1.0 plus the player's displayed drop-rate bonus. */
    static double normalizeModifier(double modifier) {
        return Math.max(1.0, Math.min(modifier, 2.0));
    }

    /**
     * Preserve the existing rarity weighting while using one calculation for live rolls and
     * the drop viewer. A +100% player bonus therefore doubles rare-table access exactly.
     */
    public static double effectiveMultiplier(TablePolicy policy, double modifier) {
        double bonus = normalizeModifier(modifier) - 1.0;
        switch (policy) {
            case COMMON:
                return 1.0 + bonus * 0.5;
            case UNCOMMON:
                return 1.0 + bonus * 0.75;
            case VERY_RARE:
                return 1.0 + bonus * 1.25;
            case EXTREMELY_RARE:
                return 1.0 + bonus * 1.5;
            case NOMAD:
            case RARE:
                return 1.0 + bonus;
            case CONSTANT:
            default:
                return 1.0;
        }
    }

    /** Returns the true per-item 1/N rate shown by the drop viewer. */
    public static int individualDropDenominator(Table table, double modifier) {
        if (table.getPolicy() == TablePolicy.CONSTANT) {
            return 1;
        }
        if (table.isEmpty() || table.getAccessibility() <= 0) {
            throw new IllegalArgumentException("Non-constant drop tables require items and positive accessibility.");
        }
        double tableChance = Math.min(1.0,
                effectiveMultiplier(table.getPolicy(), modifier) / table.getAccessibility());
        double denominator = table.getSelectionSize() / tableChance;
        return Math.max(1, (int) Math.ceil(denominator));
    }

    public List<Integer> getNpcIds() {
        return npcIds;
    }
}
