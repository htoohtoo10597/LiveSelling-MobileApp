package iss.workshop.livestreamapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Rating {
    private String id;
    private int rate;
    private ChannelStream channel;
    private User user;

    public Rating(int rate, ChannelStream channel, User user) {
        this.rate = rate;
        this.channel = channel;
        this.user = user;
    }
}
