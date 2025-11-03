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
import org.apache.commons.lang3.Range;

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

        // Ensure modifier is within a reasonable range
        modifier = Math.min(modifier, 1.5); // Cap the modifier to prevent excessive drops

        for (Table table : this) {
            TablePolicy policy = table.getPolicy();

            if (npc instanceof Nightmare) {
                Nightmare nightmare = (Nightmare) npc;
                if (nightmare.getRareRollPlayers().isEmpty()) {
                    int players = nightmare.getInstance() == null ? 0 : nightmare.getInstance().getPlayers().size();
                    System.err.println("No players on nightmare roll table, but " + players + " in instance.");
                } else if (!nightmare.getRareRollPlayers().contains(player) && (policy == TablePolicy.RARE || policy == TablePolicy.VERY_RARE || policy == TablePolicy.EXTREMELY_RARE)) {
                    continue;
                }
            }

            if (policy.equals(TablePolicy.CONSTANT)) {
                // Constant drops: unaffected by modifier, always 100% chance
                Drop drop = table.fetchRandom();
                int minimumAmount = drop.getMinimumAmount();
                items.add(new GameItem(drop.getItemId(), minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount)));
            } else {
                for (int i = 0; i < repeats; i++) {
                    double scaledModifier = modifier;
                    switch (policy) {
                        case COMMON:
                            scaledModifier = 1.0 + ((modifier - 1.0) * 0.5); // 50% effect on common items
                            break;
                        case UNCOMMON:
                            scaledModifier = 1.0 + ((modifier - 1.0) * 0.75); // 75% effect on uncommon items
                            break;
                        case RARE:
                            scaledModifier = 1.0 + ((modifier - 1.0)); // Full effect on rare items
                            break;
                        case VERY_RARE:
                            scaledModifier = 1.0 + ((modifier - 1.0) * 1.25); // 125% effect on very rare items
                            break;
                        case EXTREMELY_RARE:
                            scaledModifier = 1.0 + ((modifier - 1.0) * 1.5); // 150% effect on extremely rare items
                            break;
                    }

                    double chance = (1.0 / table.getAccessibility()) * 100D;
                    chance /= scaledModifier; // Inverse the effect of modifier for higher rarity items

                    double roll = Misc.preciseRandom(Range.between(0.0, 100.0));

                    if (roll <= chance) {
                        Drop drop = table.fetchRandom();
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

                        // Rare drop announcements
                        for (int i1 = 0; i1 < Perks.values().length; i1++) {
                            if (item.getId() == Perks.values()[i1].itemID) {
                                NPCDeath.announce(player, item, npcId);
                                isRareDrop = false;
                                break;
                            }
                        }

                        // Always announce certain items
                        String itemNameLowerCase = ItemDef.forId(item.getId()).getName().toLowerCase();
                        if (itemNameLowerCase.contains("archer ring") || itemNameLowerCase.contains("vasa minirio")
                                || itemNameLowerCase.contains("hydra") || itemNameLowerCase.contains("skeletal visage") ||
                                item.getId() == 26358 || item.getId() == 26360 || item.getId() == 26362 || item.getId() == 26364) {
                            NPCDeath.announce(player, item, npcId);
                        }

                        items.add(item);
                        if (isRareDrop) {
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
                                    && item.getId() < 23490 && item.getId() > 23491 || item.getId() < 23083 && item.getId() > 23084) {
                                NPCDeath.announce(player, item, npcId);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    public List<Integer> getNpcIds() {
        return npcIds;
    }
}
