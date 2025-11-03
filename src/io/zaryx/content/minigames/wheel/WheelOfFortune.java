package io.zaryx.content.minigames.wheel;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.items.GameItem;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WheelOfFortune implements Lootable {

    private final int INTERFACE_ID = 23354;
    private final int WHEEL_INTERFACE_ID = 23354+2;
    private final int MODEL_COMPONENT_ID = 23354+27;
    private int[] rewards = {
            24366,  //60m Island scrolls
            24366,  //60m Island scrolls
            26545,  //hope perk
            990, 25, //crystal key
            8902,   //black mask
            6792,   //serens key
            4185,   //porazdir key
            22885,  //kronos seed
            22883,  //iasor seed
            22881,  //attas
            20906,  //golpar
            20903,  //nox seed
            20909,  //buchu
            22869,  //celastrus
            20372,  //saradomin godsword (or)
            4205,   //concecration
            19720,  //occult neck (or)
            20366,  //torture (or)
            22249,  //anguish (or)
            21012,  //dhcb
            11283,  //dfs
            1623,  //gem
            11283,  //dfs
            19493,  //zenyte
            12929,  //Serp helm
            21633,  //ancient wyvern
            12773,  //volcanic whip
            12774,  //frozen whip
            27624,  //ancient sceptre
            12806,   //malediction
            12807,   //odium
            11804,   //bgs
            11808,  //zgs
            11806,  //sgs
            11802,  //ags
            11832, //bcp
            11834,   //tassets
            11826,  //arma helm
            11828, //arma chest
            11830,   //arma legs
            21018 //ancestral hat
    };
    private int[] rares = {
            2400,   //silverlight key
            2400,   //silverlight key
            2400,   //silverlight key
            22324,  //ghrazi rapier
            22875,  //hespori seed
            27285,  //eye of the corrupter
            33110,  //clepto maniac
            20788,  //row(i3)
            26235,  //zaryte vambraces
            25916,  //dhcb (t)
            25918,  //dhcb (b)
            26714,  //arma helm (or)
            26715,  //arma chest (or)
            26716,  //arma skirt (or)
            26710,  //dragon warhammer (or)
            26718,  //bandos (or)
            26719,  //bandos (or)
            26720,  //bandos (or)
            13237, //pegasian
            13235, //eternals
            13239, //primordials
            6805, //fortune spin
            6805, //fortune spin
            25904, //vampyre slayer helm
            22477, //avernic hilt
            12579, //arbo box
            22325, //scythe
            22323, //sanguinesti
            21021, //ancestral body
            21024, //ancestral bottoms
            31014,//YAK
    };
    private final Player player;
    private final int segments = 10;
    private final SecureRandom secureRandom = new SecureRandom();

    private WheelOfFortuneGame game = null;

    public WheelOfFortune(Player player) {
        this.player = player;
    }

    public void open() {
        player.getPA().showInterface(INTERFACE_ID);
        player.getPA().sendInterfaceHidden(23374, true);
        player.getPA().sendInterfaceHidden(23366, true);
    }

    public void start() {
        if(player.getFortuneSpins() <= 0) {
            player.sendMessage("You don't have any spins left.");
            return;
        }
        if (game != null) {
            player.sendMessage("@red@The wheel is already spinning, wait for it to finish before spinning again");
            return;
        }
        initGame();
    }

    private void initGame() {
        List<Integer> left = Arrays.stream(rewards).boxed().collect(Collectors.toList());
        int[] result = Stream.iterate(0, Integer::intValue)
                .limit(10)
                .mapToInt(i -> left.remove(secureRandom.nextInt(left.size())))
                .toArray();

        int randomRare = rares[secureRandom.nextInt(rares.length)];
        int[] newRewards = new int[segments];
        System.arraycopy(result, 0, newRewards, 0, result.length);
        newRewards[newRewards.length - 1] = randomRare;
        int randomRare2 = rares[secureRandom.nextInt(rares.length)];
        newRewards[newRewards.length - 6] = randomRare2;
        game = new WheelOfFortuneGame(newRewards);

        player.getPA()
                .initWheelOfFortune(WHEEL_INTERFACE_ID, game.getWinningIndex(), game.getItems());
        player.FortuneSpins -= 1;
    }

    public void quickSpin(int amount) {
        if (player.getFortuneSpins() < amount) {
            player.sendMessage("You don't have any spin's available.");
            return;
        }
        for (int z = 0; z < amount; z++) {
            List<Integer> left = Arrays.stream(rewards).boxed().collect(Collectors.toList());
            int[] result = Stream.iterate(0, Integer::intValue)
                    .limit(10)
                    .mapToInt(i -> left.remove(secureRandom.nextInt(left.size())))
                    .toArray();

            int randomRare = rares[secureRandom.nextInt(rares.length)];
            int[] newRewards = new int[segments];
            System.arraycopy(result, 0, newRewards, 0, result.length);
            newRewards[newRewards.length - 1] = randomRare;
            int randomRare2 = rares[secureRandom.nextInt(rares.length)];
            newRewards[newRewards.length - 6] = randomRare2;
            game = new WheelOfFortuneGame(newRewards);

            player.getItems().addItemUnderAnyCircumstance(game.getReward().getId(), game.getReward().getAmount());
            if (Arrays.stream(rares).anyMatch(i -> i == game.getReward().getId())) {
                PlayerHandler.executeGlobalMessage("@red@[Fortune]@blu@ " + player.getDisplayName() + " has just received a "+ ItemDef.forId(game.getReward().getId()).getName() + "!");
            }
            game = null;
        }
        player.FortuneSpins -= amount;

        player.sendMessage("[@red@FORTUNE@bla@] @cya@You have " + player.getFortuneSpins() + " remaining!");
    }

    public void onFinish(int index) {
        if (game != null && index != game.getWinningIndex()) {
            return;
        }

        player.getPA()
                .sendInterfaceModel(MODEL_COMPONENT_ID, game.getReward().getId(), 200);
        player.getPA().sendInterfaceHidden(23374, false);
        player.getPA().sendInterfaceHidden(23366, true);
        player.getItems().addItemUnderAnyCircumstance(game.getReward().getId(), game.getReward().getAmount());

        if (Arrays.stream(rares).anyMatch(i -> i == game.getReward().getId())) {
            PlayerHandler.executeGlobalMessage("@red@[Fortune]@blu@ " + player.getDisplayName() + " has just received a "+ ItemDef.forId(game.getReward().getId()).getName() + "!");
        }
        game = null;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of();
    }

    @Override
    public void roll(Player player) {

    }
}
