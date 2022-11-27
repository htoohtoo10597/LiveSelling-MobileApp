package iss.workshop.livestreamapp.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.nio.channels.Channel;

import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.User;


public class ChannelDeserializer implements JsonDeserializer<ChannelStream> {

    @Override
    public ChannelStream deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ChannelStream channel = new ChannelStream();
        User user = new User();
        final JsonObject channelJson = json.getAsJsonObject();
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
        user.setIsVerified(isVerified);

        channel.setUser(user);

        return channel;
    }
}
