package io.zaryx.content.taskmaster;
import io.zaryx.model.items.GameItem;
import java.util.*;
/** Existing weighted reward pools, rolled once when a task is assigned. */
public final class TaskRewards {
    private TaskRewards() { }
    public static GameItem[] roll(TaskDifficulty difficulty, boolean daily, Random random) {
        List<int[]> easyRewards = Arrays.asList(
                new int[]{11681, 1000},
                new int[]{995, 1000000},
                new int[]{6679, 1},
                new int[]{2528, 1},
                new int[]{2528, 1},
                new int[]{4447, 1},
                new int[]{6828, 1},
                new int[]{30002, 1},
                new int[]{28417, 1},
                new int[]{11681, 1000},
                new int[]{995, 1000000},
                new int[]{696, 1},
                new int[]{2528, 1},
                new int[]{2528, 1},
                new int[]{4447, 1},
                new int[]{6828, 1},
                new int[]{30002, 1},
                new int[]{28417, 1},
                new int[]{956, 1},
                new int[]{696, 1}
        );

        List<int[]> mediumRewards = Arrays.asList(
                new int[]{11681, 2000},
                new int[]{6679, 1},
                new int[]{30002, 3},
                new int[]{6828, 1},
                new int[]{995, 2500000},
                new int[]{24364, 1},
                new int[]{28418, 1},
                new int[]{11681, 2000},
                new int[]{6679, 1},
                new int[]{30002, 3},
                new int[]{6828, 1},
                new int[]{995, 2500000},
                new int[]{24364, 1},
                new int[]{28418, 1},
                new int[]{28418, 1},
                new int[]{11681, 2000},
                new int[]{6679, 1},
                new int[]{30002, 3},
                new int[]{6828, 1},
                new int[]{995, 2500000},
                new int[]{24364, 1},
                new int[]{28418, 1},
                new int[]{13346, 1},
                new int[]{6769, 1},
                new int[]{696, 5}
        );

        List<int[]> hardRewards = Arrays.asList(
                new int[]{11681, 3000},
                new int[]{6679, 2},
                new int[]{995, 3000000},
                new int[]{30002, 5},
                new int[]{11681, 3000},
                new int[]{6679, 2},
                new int[]{11739, 1},
                new int[]{995, 3000000},
                new int[]{30002, 5},
                new int[]{6828, 1},
                new int[]{13346, 1},
                new int[]{6828, 1},
                new int[]{13346, 1},
                new int[]{696, 8},
                new int[]{696, 8},
                new int[]{13346, 1},
                new int[]{6828, 1},
                new int[]{13346, 1},
                new int[]{696, 8},
                new int[]{696, 8},
                new int[]{28418, 2},
                new int[]{12588, 1},
                new int[]{2403, 1},
                new int[]{24364, 1}
        );

        List<int[]> eliteRewards = Arrays.asList(
                new int[]{11681, 5000},
                new int[]{26545, 2},
                new int[]{6679, 3},
                new int[]{995, 5000000},
                new int[]{13346, 1},
                new int[]{6805, 1},
                new int[]{11681, 5000},
                new int[]{26545, 2},
                new int[]{11681, 5000},
                new int[]{26545, 2},
                new int[]{6679, 3},
                new int[]{995, 5000000},
                new int[]{13346, 1},
                new int[]{6805, 1},
                new int[]{11681, 5000},
                new int[]{2401, 5},
                new int[]{6679, 3},
                new int[]{995, 5000000},
                new int[]{13346, 1},
                new int[]{6805, 1},
                new int[]{28419, 3},
                new int[]{19891, 1},
                new int[]{12582, 1},
                new int[]{12579, 1},
                new int[]{696, 12},
                new int[]{6678, 1},
                new int[]{2396, 1},
                new int[]{24365, 1}
        );

        List<int[]> pool = daily ? Arrays.asList(new int[]{11681,2500}, new int[]{696,25}, new int[]{6828,1})
                : difficulty == TaskDifficulty.EASY ? easyRewards : difficulty == TaskDifficulty.MEDIUM ? mediumRewards
                : difficulty == TaskDifficulty.HARD ? hardRewards : eliteRewards;
        List<int[]> choices = new ArrayList<>(pool);
        GameItem[] result = new GameItem[2];
        for (int i=0; i<2; i++) { int[] r=choices.remove(random.nextInt(choices.size())); result[i]=new GameItem(r[0],r[1]); }
        return result;
    }
}