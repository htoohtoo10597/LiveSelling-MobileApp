package iss.workshop.livestreamapp.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.livestreamapp.helpers.StreamStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Stream implements Serializable {

    private String id;
    private String title;
    private LocalDateTime schedule;
    private String tempSchedule;
    private ChannelStream channelStream;
    private StreamLog log;
    private StreamStatus status;

    public Stream(String title, LocalDateTime schedule, ChannelStream channel, StreamStatus status) {
        this.title = title;
        this.schedule = schedule;
        this.tempSchedule = schedule.toString();
        this.channelStream = channel;
        this.status = status;
    }

}
