package iss.workshop.livestreamapp.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Orders;
import iss.workshop.livestreamapp.models.User;

public class OrderDeserializer implements JsonDeserializer<Orders> {

    @Override
    public Orders deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Orders order = new Orders();
        User buyer = new User();
        User seller = new User();
        ChannelStream channel = new ChannelStream();

        final JsonObject jsonOrder = json.getAsJsonObject();

        //set id
        String orderId = jsonOrder.get("id").getAsString();
        order.setId(orderId);
        //set statue
        String orderStatus = jsonOrder.get("status").getAsString();
        if (orderStatus.equals("CONFIRMED")) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
        } else if (orderStatus.equals("CANCELLED")) {
            order.setOrderStatus(OrderStatus.CANCELLED);
        } else {
            order.setOrderStatus(OrderStatus.PENDING);
        }

        //set orderdate
        DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_DATE_TIME;
        String date = jsonOrder.get("orderDateTime").getAsString();
        LocalDateTime schedule = LocalDateTime.parse(date, dtFormatter);
        order.setOrderDateTime(schedule);

        //set buyer
        JsonObject buyerJson = jsonOrder.getAsJsonObject("user");
        String userId = buyerJson.get("id").getAsString();
        buyer.setId(userId);
        String userFirstName = buyerJson.get("firstName").getAsString();
        buyer.setFirstName(userFirstName);
        String userLastName = buyerJson.get("lastName").getAsString();
        buyer.setLastName(userLastName);
        boolean isVerified = buyerJson.get("isVerified").getAsBoolean();
        buyer.setIsVerified(isVerified);

        order.setUser(buyer);

        JsonObject channelJson = jsonOrder.getAsJsonObject("channel");
        String channelId = channelJson.get("id").getAsString();
        channel.setId(channelId);
        //set channel name
        String channelName = channelJson.get("name").getAsString();
        channel.setName(channelName);

        JsonObject sellerJson = channelJson.getAsJsonObject("user");

        String sellerId = sellerJson.get("id").getAsString();
        seller.setId(sellerId);
        String sellerFirstName = sellerJson.get("firstName").getAsString();
        seller.setFirstName(sellerFirstName);
        String sellerLastName = sellerJson.get("lastName").getAsString();
        seller.setLastName(sellerLastName);
        boolean sellerIsVerified = sellerJson.get("isVerified").getAsBoolean();
        seller.setIsVerified(sellerIsVerified);

        channel.setUser(seller);
        seller.setChannel(channel);

        order.setChannel(channel);

        return order;
    }
    private void setStatus(){

    }
}
