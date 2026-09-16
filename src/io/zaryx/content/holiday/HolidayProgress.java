package io.zaryx.content.holiday;

/** Account-owned quest pouch. Event supplies never become tradable inventory items. */
public final class HolidayProgress {
    public int edition, stage, runs, tokens, gathered, puzzle, delivered, order;
    public long nextAction;
    public int layoutSeed;
    private static final int[][] ORDERS={{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
    public void useEdition(int value) {
        if(edition==value)return;
        layoutSeed=0;edition=value;stage=runs=tokens=gathered=puzzle=delivered=order=0;
    }
    public boolean start(int sequence) {
        if(stage!=0)return false;
        layoutSeed=1+Math.floorMod(layoutSeed+sequence, 18);
        stage=1;gathered=puzzle=delivered=0;order=Math.floorMod(sequence,ORDERS.length);return true;
    }
    public boolean gather(int index) {
        if(stage!=1 || index<0 || index>2 || (gathered & 1<<index)!=0)return false;
        gathered|=1<<index;
        if(gathered==7)stage=2;
        return true;
    }
    public int nextIngredient() { return ORDERS[order][Math.min(2,puzzle)]; }
    public boolean mix(int index) {
        if(stage!=2)return false;
        if(index!=nextIngredient()){puzzle=0;return false;}
        if(++puzzle==3)stage=3;
        return true;
    }
    public boolean deliver(int index) {
        if(stage!=3 || index<0 || index>2 || (delivered & 1<<index)!=0)return false;
        delivered|=1<<index;
        if(delivered==7)stage=4;
        return true;
    }
    public boolean complete() {
        if(stage!=4)return false;
        runs=Math.min(1000000,runs+1);tokens=Math.min(1000000,tokens+5);stage=gathered=puzzle=delivered=0;return true;
    }
    public String encode() {
        return edition+","+stage+","+runs+","+tokens+","+gathered+","+puzzle+","+delivered+","+order+","+layoutSeed;
    }
    public static HolidayProgress decode(String text) {
        HolidayProgress p=new HolidayProgress();
        String[] values=text.split(",");
        if(values.length!=8&&values.length!=9)throw new IllegalArgumentException("Invalid holiday progress");
        int[] n=new int[8];for(int i=0;i<n.length;i++)n[i]=Integer.parseInt(values[i]);
        if(n[0]<0||n[1]<0||n[1]>4||n[2]<0||n[2]>1000000||n[3]<0||n[3]>1000000||n[4]<0||n[4]>7||n[5]<0||n[5]>3||n[6]<0||n[6]>7||n[7]<0||n[7]>5)
            throw new IllegalArgumentException("Holiday progress out of range");
        if((n[1]>=2&&n[4]!=7)||(n[1]>=3&&n[5]!=3)||(n[1]==4&&n[6]!=7))
            throw new IllegalArgumentException("Inconsistent holiday progress");
        if(values.length==9){p.layoutSeed=Integer.parseInt(values[8]);if(p.layoutSeed<0||p.layoutSeed>1000000)throw new IllegalArgumentException("Invalid holiday layout");}
        p.edition=n[0];p.stage=n[1];p.runs=n[2];p.tokens=n[3];p.gathered=n[4];p.puzzle=n[5];p.delivered=n[6];p.order=n[7];return p;
    }
}
