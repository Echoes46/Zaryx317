package io.zaryx.content.holiday;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.save.PlayerSaveEntry;
import java.util.*;

public class HolidaySaveEntry implements PlayerSaveEntry {
    public List<String> getKeys(Player p){return List.of("holiday-halloween","holiday-christmas");}
    private Holiday type(String key){return key.equals("holiday-halloween")?Holiday.HALLOWEEN:Holiday.CHRISTMAS;}
    public boolean decode(Player p,String key,String value){
        if(!getKeys(p).contains(key))return false;
        p.holidayProgress.put(type(key),HolidayProgress.decode(value));return true;
    }
    public String encode(Player p,String key){return p.holidayProgress.computeIfAbsent(type(key),h->new HolidayProgress()).encode();}
    public void login(Player p){ }
}
