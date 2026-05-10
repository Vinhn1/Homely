package com.example.homely.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    /**
     * Trả về chuỗi thời gian tương đối: "Vừa xong", "5 phút trước", "2 giờ trước"...
     */
    public static String getRelativeTime(Date date) {
        if (date == null) return "";

        long now = System.currentTimeMillis();
        long diff = now - date.getTime();

        if (diff < 0) return "Vừa xong";

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours   = TimeUnit.MILLISECONDS.toHours(diff);
        long days    = TimeUnit.MILLISECONDS.toDays(diff);

        if (seconds < 60) {
            return "Vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 7) {
            return days + " ngày trước";
        } else {
            // Hiển thị ngày cụ thể nếu quá 7 ngày
            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            return sdf.format(date);
        }
    }
}