package iss.workshop.livestreamapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StreamLog implements Serializable {

    private String id;
    private int numLikes;
    private Stream stream;
    private User seller;
    private List<Message> messages;
    private int numViewers;

    public StreamLog(int numLikes) {
        this.numLikes = numLikes;
        this.messages = new ArrayList<Message>();
    }

    public StreamLog(int numLikes, User seller, Stream stream) {
        this.numLikes = numLikes;
        this.seller = seller;
        this.stream = stream;
    }
}
