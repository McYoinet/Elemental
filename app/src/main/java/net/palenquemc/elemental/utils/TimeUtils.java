package net.palenquemc.elemental.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeUtils {
    public String longToDateString(long milli) {
        Instant instant = Instant.ofEpochMilli(milli);
        Date date = Date.from(instant);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        
        return formatter.format(date);
    }
}
