package io.zaryx.content.donor;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.zaryx.Server;
import io.zaryx.annotate.PostInit;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CosmeticManager {

    private static final String COSMETIC_FILE_PATH = Server.getDataDirectory() + "/deals/cosmetic_costs.json";
    private static HashMap<Integer, Integer> cosmetic_cost = new HashMap<>();


    public static void onLogin(Player player) {
        if (!cosmetic_cost.isEmpty()) {
            cosmetic_cost.forEach((itemId, cost) -> player.sendMessage("[COSMETICCOST]"+ itemId +"-"+ cost));
        }
    }

    public static void onPurchase(Player player, int itemId, int cost) {
        if (cosmetic_cost.containsKey(itemId) && cosmetic_cost.get(itemId) == cost) {
            return;
        }
        if (cosmetic_cost.containsKey(itemId) && cosmetic_cost.get(itemId) != cost) {
            player.sendErrorMessage("Notify a staff memeber that you brought " + itemId + " for cost of " + cost + " and the system threw an error");
            return;
        }
        cosmetic_cost.put(itemId, cost);
        sendCosmeticCosts();
        saveCosmeticCosts();
    }

    private static void sendCosmeticCosts() {
        if (!cosmetic_cost.isEmpty()) {
            for (Player player : PlayerHandler.getPlayers()) {
                if (player != null) {
                    cosmetic_cost.forEach((itemId, cost) -> player.sendMessage("[COSMETICCOST]"+itemId +"-"+ cost));
                }
            }
        }

    }

    public static void saveCosmeticCosts() {
        try (FileWriter writer = new FileWriter(COSMETIC_FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(cosmetic_cost, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostInit
    public static void loadCosmeticCosts() {
        try (FileReader reader = new FileReader(COSMETIC_FILE_PATH)) {
            Gson gson = new Gson();
            cosmetic_cost = gson.fromJson(reader, new TypeToken<HashMap<Integer, Integer>>() {}.getType());
            if (cosmetic_cost == null) {
                cosmetic_cost = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
