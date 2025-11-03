package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.*;

public class FortuneTable implements Lootable {
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() { return items; }

    public static ArrayList<GameItem> getRareDrops() {
    ArrayList<GameItem> drops = new ArrayList<>();
    List<GameItem> found = items.get(LootRarity.RARE);
    for(GameItem f : found) {
        boolean foundItem = false;
        for(GameItem drop : drops) {
            if (drop.getId() == f.getId()) {
                foundItem = true;
                break;
            }
        }
        if (!foundItem) {
            drops.add(f);
        }
    }
    return drops;
    }
    static {
        items.put(LootRarity.COMMON, Arrays.asList(

                new GameItem(24366),  //60m Island scrolls
                new GameItem(24366),  //60m Island scrolls
                new GameItem( 26545),  //hope perk
                new GameItem( 990, 25), //crystal key
                new GameItem( 8902),   //black mask
                new GameItem( 6792),   //serens key
                new GameItem( 4185),   //porazdir key
                new GameItem(22885),  //kronos seed
                new GameItem( 22883),  //iasor seed
                new GameItem( 22881),  //attas
                new GameItem( 20906),  //golpar
                new GameItem( 20903),  //nox seed
                new GameItem( 20909),  //buchu
                new GameItem( 22869),  //celastrus
                new GameItem( 20372),  //saradomin godsword (or)
                new GameItem( 4205),   //concecration
                new GameItem( 19720),  //occult neck (or)
                new GameItem( 20366),  //torture (or)
                new GameItem( 22249),  //anguish (or)
                new GameItem( 21012),  //dhcb
                new GameItem( 11283),  //dfs
                new GameItem( 1623),  //gem
                new GameItem( 11283),  //dfs
                new GameItem( 19493),  //zenyte
                new GameItem( 12929),  //Serp helm
                new GameItem( 21633),  //ancient wyvern
                new GameItem( 12773),  //volcanic whip
                new GameItem( 12774),  //frozen whip
                new GameItem( 27624),  //ancient sceptre
                new GameItem( 12806),   //malediction
                new GameItem(   12807),   //odium
                new GameItem(  11804),   //bgs
                new GameItem( 11808),  //zgs
                new GameItem( 11806),  //sgs
                new GameItem(   11802),  //ags
                new GameItem(  11832), //bcp
                new GameItem(  11834),   //tassets
                new GameItem(  11826),  //arma helm
                new GameItem( 11828), //arma chest
                new GameItem( 11830),   //arma legs
                new GameItem( 21018) //ancestral hat
        ));

        items.put(LootRarity.UNCOMMON,Arrays.asList(
//                new GameItem(13346,1),
//                new GameItem(13346,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(22088,1),
//                new GameItem(22089,1),
//                new GameItem(22090,1),
//                new GameItem(22091,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(6679,1),
//                new GameItem(11739,1),
//                new GameItem(11739,1),
//                new GameItem(19897,1),
//                new GameItem(19897,1),
//                new GameItem(13346,1),
//                new GameItem(13346,1),
//                new GameItem(13346,1),
//                new GameItem(13346,1),
//                new GameItem(6769,1)


                //end uncommon
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem( 2400),   //silverlight key
                new GameItem(2400),   //silverlight key
                new GameItem(2400),   //silverlight key
                new GameItem( 22324),  //ghrazi rapier
                new GameItem(22875),  //hespori seed
                new GameItem(27285),  //eye of the corrupter
                new GameItem(33110),  //clepto maniac
                new GameItem(20788),  //row(i3)
                new GameItem( 26235),  //zaryte vambraces
                new GameItem( 25916),  //dhcb (t)
                new GameItem( 25918),  //dhcb (b)
                new GameItem( 26714),  //arma helm (or)
                new GameItem( 26715),  //arma chest (or)
                new GameItem( 26716),  //arma skirt (or)
                new GameItem( 26710),  //dragon warhammer (or)
                new GameItem( 26718),  //bandos (or)
                new GameItem( 26719),  //bandos (or)
                new GameItem( 26720),  //bandos (or)
                new GameItem( 13237), //pegasian
                new GameItem( 13235), //eternals
                new GameItem( 13239), //primordials
                new GameItem( 6805), //fortune spin
                new GameItem(6805), //fortune spin
                new GameItem(25904), //vampyre slayer helm
                new GameItem(  22477), //avernic hilt
                new GameItem( 12579), //arbo box
                new GameItem(22325), //scythe
                new GameItem(  22323), //sanguinesti
                new GameItem( 21021), //ancestral body
                new GameItem( 21024), //ancestral bottoms
                new GameItem( 31014)//YAK
                

                //end rare
        ));
    }

    public static ArrayList<GameItem> getRare() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if(drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if(!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    public static ArrayList<GameItem> getAllDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        items.forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) {
                    drops.add(g);
                }
            });
        });
        return drops;
    }
    public FortuneTable(Player player) {
        super();
    }
    @Override
    public Map<LootRarity, List<GameItem>> getLoot() { return getItems(); }

    @Override
    public void roll(Player player) {

    }
}