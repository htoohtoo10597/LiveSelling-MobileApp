package iss.workshop.livestreamapp.helpers;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter {
    public LocalDateTime dateTimeConvert(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
