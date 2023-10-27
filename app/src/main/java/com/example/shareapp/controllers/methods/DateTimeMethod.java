package com.example.shareapp.controllers.methods;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeMethod {
    public static String simplifyDateFormat(long date) {
        Date d = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(d);
    }
}
