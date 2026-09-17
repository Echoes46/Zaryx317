package io.zaryx.content.holiday;

public enum Holiday {
    HALLOWEEN("Halloween", "The Lanterns of Lost Souls", 2305,
            new String[]{"Pumpkin pulp", "Bat bones", "Ghost ooze"}, new int[]{1959,530,4286},
            new int[]{9925,9924,9923,9922,9921}),
    CHRISTMAS("Christmas", "The Missing Christmas Delivery", 2315,
            new String[]{"Snowballs", "Bell baubles", "Unfinished toys"}, new int[]{10501,6846,7759},
            new int[]{21847,21849,21851,21853,21855,21857});
    public final String title, quest;
    public final int host;
    public final String[] supplies;
    public final int[] supplyItems, rewards;
    Holiday(String title,String quest,int host,String[] supplies,int[] supplyItems,int[] rewards) {
        this.title=title;this.quest=quest;this.host=host;this.supplies=supplies;this.supplyItems=supplyItems;this.rewards=rewards;
    }
}
