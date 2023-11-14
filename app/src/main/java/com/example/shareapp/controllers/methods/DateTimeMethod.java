package com.example.shareapp.controllers.methods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateTimeMethod {
    public static String simplifyDateFormat(long date) {
        Date d = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(d);
    }

    public static String timeDifference(long timestamp) {
        long currentMillis = System.currentTimeMillis();
        long timeDiff = currentMillis - timestamp;

        long minuteDif = timeDiff / (60 * 1000);
        long hourDiff = timeDiff / (60 * 60 * 1000);
        long dayDiff = timeDiff / (60 * 60 * 24 * 1000);

        if(minuteDif < 60) {
            return minuteDif + " phút trước";
        }
        else if(hourDiff < 24)
        {
            return hourDiff + " giờ trước";
        } else {
            return dayDiff + " ngày trước";
        }
    }
}
