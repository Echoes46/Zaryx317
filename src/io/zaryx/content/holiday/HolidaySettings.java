package io.zaryx.content.holiday;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class HolidaySettings {
    private final Properties values;
    public HolidaySettings(Properties values){this.values=values;}
    public static HolidaySettings load(Path path) throws IOException {
        Properties p=new Properties();
        if(Files.exists(path))try(Reader reader=Files.newBufferedReader(path)){p.load(reader);}
        HolidaySettings settings=new HolidaySettings(p);
        for(Holiday holiday:Holiday.values()){settings.enabled(holiday);settings.edition(holiday);}
        return settings;
    }
    public boolean enabled(Holiday holiday) {
        String value=values.getProperty(holiday.name().toLowerCase(Locale.ROOT)+".enabled","false").trim();
        if(!value.equalsIgnoreCase("true")&&!value.equalsIgnoreCase("false"))throw new IllegalArgumentException("Invalid holiday enabled flag: "+value);
        return Boolean.parseBoolean(value);
    }
    public int edition(Holiday holiday) {
        int value=Integer.parseInt(values.getProperty(holiday.name().toLowerCase(Locale.ROOT)+".edition","2026").trim());
        if(value<1)throw new IllegalArgumentException("Holiday edition must be positive");return value;
    }
}
