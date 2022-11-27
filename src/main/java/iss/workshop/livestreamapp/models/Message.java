package iss.workshop.livestreamapp.models;

import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Message implements Serializable {
    private String id;
    private String message;

    private LocalDateTime timeStamp;

    private StreamLog log;

    public Message(String message, LocalDateTime timeStamp, StreamLog log) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.log = log;
    }
}
