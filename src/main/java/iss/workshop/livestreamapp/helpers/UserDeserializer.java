package iss.workshop.livestreamapp.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;

public class UserDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject userJson = json.getAsJsonObject();
        User user = new User();

        String userId = userJson.get("id").getAsString();
        user.setId(userId);
        String userFirstName = userJson.get("firstName").getAsString();
        user.setFirstName(userFirstName);
        String userLastName = userJson.get("lastName").getAsString();
        user.setLastName(userLastName);
        boolean isVerified = userJson.get("isVerified").getAsBoolean();
        user.setIsVerified(isVerified);

        return user;
    }
}
