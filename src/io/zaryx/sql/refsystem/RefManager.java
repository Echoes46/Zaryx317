package io.zaryx.sql.refsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.zaryx.Server;
import io.zaryx.annotate.PostInit;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Referral code manager.
 *
 * Claim tracking is based on values already stored on Player:
 * - player.getDisplayName()
 * - player.getIpAddress()
 * - player.getMacAddress()
 * - player.getUUID()
 *
 * This class does not fetch IP addresses directly from the network/session.
 */
public class RefManager {

    private static final int Interface = 61_000;
    private static final int ref_code_interface = 61_010;

    private static final String REF_DIRECTORY = Server.getDataDirectory() + "/refs/";
    private static final String FILE_PATH = REF_DIRECTORY + "referral_codes.yaml";
    private static final String PLAYER_CLAIM_DATA_FILE = REF_DIRECTORY + "player_claims.yaml";

    /**
     * Maximum number of different claim records allowed from the same IP/device identifier.
     * This is mainly an anti-abuse limit for alternate accounts.
     */
    private static final int MAX_CLAIMS_PER_IDENTIFIER = 3;

    private static final Logger logger = LoggerFactory.getLogger(RefManager.class);

    private static Set<PlayerClaimData> playerClaims = new HashSet<>();
    private static List<Referral> referrals = new ArrayList<>();

    public static void openInterface(Player player) {
        updateReferralRewardsInterface(player);
        player.getPA().showInterface(Interface);
    }

    private static void updateReferralRewardsInterface(Player player) {
        int textBoxIndex = ref_code_interface + 2;

        for (int i = 0; i < 102; i += 2) {
            player.getPA().sendString(textBoxIndex + i, " ");
        }

        if (!referrals.isEmpty()) {
            for (int i = 0; i < referrals.size(); i++) {
                player.getPA().sendString((textBoxIndex + (i * 2)), referrals.get(i).getCode());
            }
        }

        player.getPA().setScrollableMaxHeight(ref_code_interface, 19 * referrals.size());
    }

    @PostInit
    public static void loadPlayerClaims() {
        try {
            ensureRefDirectoryExists();

            File file = new File(PLAYER_CLAIM_DATA_FILE);
            if (!file.exists()) {
                playerClaims.clear();
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(Set.class, PlayerClaimData.class);
            Set<PlayerClaimData> loadedClaims = objectMapper.readValue(file, collectionType);

            playerClaims = loadedClaims == null ? new HashSet<>() : loadedClaims;
            playerClaims.forEach(PlayerClaimData::ensureClaimedReferrals);
        } catch (IOException e) {
            logger.error("Error loading player claims: {}", e.getMessage(), e);
        }
    }

    public static void savePlayerClaims() {
        try {
            ensureRefDirectoryExists();

            File file = new File(PLAYER_CLAIM_DATA_FILE);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, playerClaims);
        } catch (IOException e) {
            logger.error("Error saving player claims: {}", e.getMessage(), e);
        }
    }

    @PostInit
    public static void loadReferralRewards() {
        try {
            ensureRefDirectoryExists();

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                referrals.clear();
                logger.info("No referral codes file found: {}", FILE_PATH);
                return;
            }

            Yaml yaml = createYaml();

            try (FileReader fileReader = new FileReader(file)) {
                List<Map<String, Object>> referralMaps = yaml.load(fileReader);
                referrals.clear();

                if (referralMaps == null || referralMaps.isEmpty()) {
                    logger.info("No referral codes found in the file: {}", FILE_PATH);
                    return;
                }

                for (Map<String, Object> referralMap : referralMaps) {
                    if (referralMap == null) {
                        continue;
                    }

                    Object codeObj = referralMap.get("code");
                    Object rewardsObj = referralMap.get("rewards");

                    if (!(codeObj instanceof String)) {
                        logger.warn("Skipped referral entry with missing/invalid code: {}", referralMap);
                        continue;
                    }

                    String code = ((String) codeObj).trim();
                    if (code.isEmpty()) {
                        logger.warn("Skipped referral entry with blank code: {}", referralMap);
                        continue;
                    }

                    List<GameItem> rewards = parseRewardMaps(rewardsObj);
                    referrals.add(new Referral(code, rewards));
                }
            }
        } catch (IOException e) {
            logger.error("Error loading referral rewards: {}", e.getMessage(), e);
        }
    }

    public static void saveReferralRewards() {
        try {
            ensureRefDirectoryExists();

            File file = new File(FILE_PATH);
            Yaml yaml = createYaml();
            List<Map<String, Object>> referralMaps = new ArrayList<>();

            for (Referral referral : referrals) {
                Map<String, Object> referralMap = new LinkedHashMap<>();
                referralMap.put("code", referral.getCode());

                List<Map<String, Integer>> rewardMaps = new ArrayList<>();
                for (GameItem reward : referral.getRewards()) {
                    Map<String, Integer> rewardMap = new LinkedHashMap<>();
                    rewardMap.put("id", reward.getId());
                    rewardMap.put("amount", reward.getAmount());
                    rewardMaps.add(rewardMap);
                }

                referralMap.put("rewards", rewardMaps);
                referralMaps.add(referralMap);
            }

            try (FileWriter writer = new FileWriter(file)) {
                yaml.dump(referralMaps, writer);
            }
        } catch (IOException e) {
            logger.error("Error saving referral rewards: {}", e.getMessage(), e);
        }
    }

    private static List<GameItem> parseRewardMaps(Object rewardsObj) {
        List<GameItem> rewards = new ArrayList<>();

        if (!(rewardsObj instanceof List<?>)) {
            return rewards;
        }

        for (Object rewardObj : (List<?>) rewardsObj) {
            if (!(rewardObj instanceof Map<?, ?>)) {
                continue;
            }

            Map<?, ?> rewardMap = (Map<?, ?>) rewardObj;
            Integer id = toInteger(rewardMap.get("id"));
            Integer amount = toInteger(rewardMap.get("amount"));

            if (id == null || amount == null || id <= 0 || amount <= 0) {
                logger.warn("Skipped invalid referral reward: {}", rewardMap);
                continue;
            }

            rewards.add(new GameItem(id, amount));
        }

        return rewards;
    }

    private static Integer toInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {
            try {
                return Integer.parseInt(((String) value).trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private static Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }

    private static void ensureRefDirectoryExists() {
        Misc.createDirectory(REF_DIRECTORY);
    }

    private static final int[] buttonIds = {
            61011, 61013, 61015, 61017, 61019,
            61021, 61023, 61025, 61027, 61029,
            61031, 61033, 61035, 61037, 61039,
            61041, 61043, 61045, 61047, 61049,
            61051, 61053, 61055, 61057, 61059,
            61061, 61063, 61065, 61067, 61069,
            61071, 61073, 61075, 61077, 61079
    };

    public static boolean handleButton(Player player, int realButton) {
        if (Arrays.stream(buttonIds).noneMatch(id -> id == realButton)) {
            return false;
        }

        Optional<Referral> referralOpt = getReferralByButtonId(realButton);

        if (referralOpt.isPresent()) {
            Referral referral = referralOpt.get();

            if (player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                player.start(new DialogueBuilder(player)
                        .option("Referral found. Would you like to alter it?",
                                new DialogueOption("Yes", plr -> {
                                    plr.getPA().closeAllWindows();
                                    plr.getPA().sendEnterString("Enter referral code:", (pl, newReferralCode) -> {
                                        pl.getPA().closeAllWindows();
                                        handleReferralUpdate(pl, newReferralCode, realButton);
                                    });
                                }),
                                new DialogueOption("No", p -> {
                                    p.getPA().closeAllWindows();
                                    claimReferral(p, referral, false);
                                })));
            } else {
                claimReferral(player, referral, true);
            }

            return true;
        }

        if (player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
            player.start(new DialogueBuilder(player)
                    .option("Referral not found. Would you like to create it?",
                            new DialogueOption("Yes", plr -> {
                                plr.getPA().closeAllWindows();
                                plr.getPA().sendEnterString("Enter referral code:", (pl, newReferralCode) -> {
                                    pl.getPA().closeAllWindows();
                                    handleReferralUpdate(pl, newReferralCode, realButton);
                                });
                            }),
                            new DialogueOption("No", p -> p.getPA().closeAllWindows())));
            return true;
        }

        return false;
    }

    private static void claimReferral(Player player, Referral referral, boolean enforceClaimRules) {
        if (referral == null) {
            player.sendErrorMessage("That referral code could not be found.");
            return;
        }

        if (referral.getRewards() == null || referral.getRewards().isEmpty()) {
            player.sendErrorMessage("That referral code does not have any rewards set.");
            return;
        }

        if (enforceClaimRules) {
            if (hasPlayerClaimedRewards(player, referral)) {
                player.sendErrorMessage("You have already claimed " + referral.getCode() + "!");
                return;
            }

            if (hasPlayerExceededClaimLimit(player)) {
                player.sendErrorMessage("You have already claimed referral rewards too many times from this IP/device.");
                return;
            }

            claimRefCode(player, referral);
            savePlayerClaims();
        }

        player.sendMessage("You have just claimed referral code: " + referral.getCode() + "!");

        for (GameItem reward : referral.getRewards()) {
            player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
            player.sendMessage("You have been given: " + reward.getDef().getName() + " from the referral code!");
        }
    }

    private static void claimRefCode(Player player, Referral referral) {
        PlayerClaimData existingPlayerClaim = getPlayerClaimData(player);

        if (existingPlayerClaim != null) {
            existingPlayerClaim.ensureClaimedReferrals();
            existingPlayerClaim.setClaimCount(existingPlayerClaim.getClaimCount() + 1);
            existingPlayerClaim.getClaimedReferrals().add(referral.getCode());
            updateKnownIdentifiers(existingPlayerClaim, player);
            return;
        }

        PlayerClaimData playerClaimData = new PlayerClaimData();
        playerClaimData.setUsername(player.getDisplayName());
        playerClaimData.setIpAddress(player.getIpAddress());
        playerClaimData.setMacAddress(player.getMacAddress());
        playerClaimData.setUUID(player.getUUID());
        playerClaimData.setClaimCount(1);
        playerClaimData.getClaimedReferrals().add(referral.getCode());

        playerClaims.add(playerClaimData);
    }

    /**
     * Checks if the same player/device has already claimed this specific referral.
     *
     * This checks more than the exact old tuple. The old logic required username + IP + MAC + UUID
     * all to match, which allowed duplicate claims if a player changed IP or one identifier was missing.
     */
    private static boolean hasPlayerClaimedRewards(Player player, Referral referral) {
        return playerClaims.stream()
                .filter(data -> samePlayerOrDevice(data, player))
                .anyMatch(data -> {
                    data.ensureClaimedReferrals();
                    return data.getClaimedReferrals().contains(referral.getCode());
                });
    }

    /**
     * Limits how many different claim records can exist for the same IP/MAC/UUID.
     */
    private static boolean hasPlayerExceededClaimLimit(Player player) {
        long ipCount = isNotBlank(player.getIpAddress())
                ? playerClaims.stream().filter(data -> Objects.equals(data.getIpAddress(), player.getIpAddress())).count()
                : 0;

        long macCount = isNotBlank(player.getMacAddress())
                ? playerClaims.stream().filter(data -> Objects.equals(data.getMacAddress(), player.getMacAddress())).count()
                : 0;

        long uuidCount = isNotBlank(player.getUUID())
                ? playerClaims.stream().filter(data -> Objects.equals(data.getUUID(), player.getUUID())).count()
                : 0;

        return ipCount >= MAX_CLAIMS_PER_IDENTIFIER
                || macCount >= MAX_CLAIMS_PER_IDENTIFIER
                || uuidCount >= MAX_CLAIMS_PER_IDENTIFIER;
    }

    private static Optional<Referral> getReferralByButtonId(int buttonId) {
        for (int i = 0; i < buttonIds.length; i++) {
            if (buttonIds[i] == buttonId) {
                if (i >= 0 && i < referrals.size()) {
                    return Optional.of(referrals.get(i));
                }
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    public static void handleReferralUpdate(Player player, String referralCode, int realButton) {
        String cleanReferralCode = referralCode == null ? "" : referralCode.trim();

        if (cleanReferralCode.isEmpty()) {
            player.sendErrorMessage("Referral code cannot be blank.");
            return;
        }

        player.start(new DialogueBuilder(player)
                .option("Would you like to update the referral rewards for " + cleanReferralCode + "?",
                        new DialogueOption("Yes", pl -> {
                            pl.getPA().closeAllWindows();
                            pl.getPA().sendEnterString("Enter reward data (id1-amount1,id2-amount2,...):", (plx, rewardData) -> {
                                Optional<List<GameItem>> parsedRewards = parseRewardData(plx, rewardData);

                                if (!parsedRewards.isPresent()) {
                                    return;
                                }

                                updateReferral(plx, cleanReferralCode, parsedRewards.get());
                                plx.sendMessage("@red@Referral rewards updated for " + cleanReferralCode + "!");
                                plx.getPA().closeAllWindows();
                            });
                        }),
                        new DialogueOption("No", p -> p.getPA().closeAllWindows())));
    }

    private static Optional<List<GameItem>> parseRewardData(Player player, String rewardData) {
        if (rewardData == null || rewardData.trim().isEmpty()) {
            player.sendErrorMessage("Reward data cannot be blank.");
            return Optional.empty();
        }

        String[] rewardPairs = rewardData.split(",");
        List<GameItem> rewards = new ArrayList<>();

        for (String pair : rewardPairs) {
            String[] parts = pair.trim().split("-");

            if (parts.length != 2) {
                player.sendErrorMessage("Invalid reward format. Use id-amount,id-amount.");
                return Optional.empty();
            }

            try {
                int itemId = Integer.parseInt(parts[0].trim());
                int amount = Integer.parseInt(parts[1].trim());

                if (itemId <= 0 || amount <= 0) {
                    player.sendErrorMessage("Item IDs and amounts must be greater than 0.");
                    return Optional.empty();
                }

                rewards.add(new GameItem(itemId, amount));
            } catch (NumberFormatException e) {
                player.sendErrorMessage("Invalid reward number. Use id-amount,id-amount.");
                return Optional.empty();
            }
        }

        if (rewards.isEmpty()) {
            player.sendErrorMessage("You must enter at least one reward.");
            return Optional.empty();
        }

        return Optional.of(rewards);
    }

    private static PlayerClaimData getPlayerClaimData(Player player) {
        return playerClaims.stream()
                .filter(data -> samePlayerOrDevice(data, player))
                .findFirst()
                .orElse(null);
    }

    private static boolean samePlayerOrDevice(PlayerClaimData data, Player player) {
        if (data == null || player == null) {
            return false;
        }

        return matchesNonBlank(data.getUsername(), player.getDisplayName())
                || matchesNonBlank(data.getMacAddress(), player.getMacAddress())
                || matchesNonBlank(data.getUUID(), player.getUUID());
    }

    private static boolean matchesNonBlank(String first, String second) {
        return isNotBlank(first) && isNotBlank(second) && first.equalsIgnoreCase(second);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static void updateKnownIdentifiers(PlayerClaimData data, Player player) {
        if (!isNotBlank(data.getUsername())) {
            data.setUsername(player.getDisplayName());
        }

        if (!isNotBlank(data.getIpAddress())) {
            data.setIpAddress(player.getIpAddress());
        }

        if (!isNotBlank(data.getMacAddress())) {
            data.setMacAddress(player.getMacAddress());
        }

        if (!isNotBlank(data.getUUID())) {
            data.setUUID(player.getUUID());
        }
    }

    private static void updateReferral(Player player, String referralCode, List<GameItem> rewards) {
        Referral referral = getReferralByCode(referralCode);

        if (referral != null) {
            referral.setRewards(rewards);
        } else {
            referral = new Referral(referralCode, rewards);
            referrals.add(referral);
        }

        saveReferralRewards();
        player.sendErrorMessage("You have just updated ref code: " + referralCode + "!");
    }

    private static Referral getReferralByCode(String referralCode) {
        return referrals.stream()
                .filter(referral -> referral.getCode().equalsIgnoreCase(referralCode))
                .findFirst()
                .orElse(null);
    }

    @Getter
    @Setter
    static class PlayerClaimData {
        private String username;
        private String ipAddress;
        private String macAddress;
        private String UUID;
        private Set<String> claimedReferrals;
        private int claimCount;

        public PlayerClaimData() {
            claimedReferrals = new HashSet<>();
        }

        public void ensureClaimedReferrals() {
            if (claimedReferrals == null) {
                claimedReferrals = new HashSet<>();
            }
        }
    }
}

@Getter
@Setter
class Referral {
    private String code;
    private List<GameItem> rewards;

    public Referral(String code, List<GameItem> rewards) {
        this.code = code;
        this.rewards = rewards == null ? new ArrayList<>() : rewards;
    }
}
