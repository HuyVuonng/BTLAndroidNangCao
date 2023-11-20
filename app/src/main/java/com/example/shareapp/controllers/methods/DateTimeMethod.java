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

        long second = timeDiff / 1000;
        long minuteDif = second / 60;
        long hourDiff = minuteDif / 60;
        long dayDiff = hourDiff / 24;

        if(dayDiff >= 1) {
            return dayDiff + " ngày trước";
        } else if(hourDiff >= 1) {
            return hourDiff + " tiếng trước";
        } else if(minuteDif >= 1) {
            return minuteDif + " phút trước";
        } else {
            return "1 phút trước";
        }
    }

        public static int minutesDifference(long timestamp) {
        long currentMillis = System.currentTimeMillis();
        long timeDiff = currentMillis - timestamp;

        return Math.round(timeDiff / (1000 * 60));
    }
}
