package iss.workshop.livestreamapp.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;

public class StreamDeserializer implements JsonDeserializer<Stream> {

    @Override
    public Stream deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        Stream currStream = new Stream();
        ChannelStream channel = new ChannelStream();
        User user = new User();

        //set id
        final String id = jsonObject.get("id").getAsString();
        currStream.setId(id);
        //set title
        final String title = jsonObject.get("title").getAsString();
        currStream.setTitle(title);
        //set status
        final String status = jsonObject.get("status").getAsString();
        switch(status){
            case "PENDING":
                currStream.setStatus(StreamStatus.PENDING);
                break;
            case "ONGOING":
                currStream.setStatus(StreamStatus.ONGOING);
                break;
            case "CANCELLED":
                currStream.setStatus(StreamStatus.CANCELLED);
                break;
            case "COMPLETED":
                currStream.setStatus(StreamStatus.COMPLETED);
                break;
            case "DELETED":
                currStream.setStatus(StreamStatus.DELETED);
                break;
        }
        //set sched
        DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_DATE_TIME;
        String date = jsonObject.get("schedule").getAsString();
        LocalDateTime schedule = LocalDateTime.parse(date, dtFormatter);
        currStream.setSchedule(schedule);

        //set channel
        JsonObject channelJson = jsonObject.getAsJsonObject("channel");
        //set channel id
        String channelId = channelJson.get("id").getAsString();
        channel.setId(channelId);
        //set channel name
        String channelName = channelJson.get("name").getAsString();
        channel.setName(channelName);

        JsonObject userJson = channelJson.getAsJsonObject("user");

        String userId = userJson.get("id").getAsString();
        user.setId(userId);
        String userFirstName = userJson.get("firstName").getAsString();
        user.setFirstName(userFirstName);
        String userLastName = userJson.get("lastName").getAsString();
        user.setLastName(userLastName);
        boolean isVerified = userJson.get("isVerified").getAsBoolean();

        channel.setUser(user);
        currStream.setChannelStream(channel);

        return currStream;
    }
}
