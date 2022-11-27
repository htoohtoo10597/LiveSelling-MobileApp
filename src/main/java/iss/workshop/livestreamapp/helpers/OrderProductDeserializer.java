package iss.workshop.livestreamapp.helpers;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.OrderProduct;
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.User;

public class OrderProductDeserializer implements JsonDeserializer<OrderProduct> {
    @Override
    public OrderProduct deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonOrderProduct = json.getAsJsonObject();
        OrderProduct oProduct = new OrderProduct();
        Product product = new Product();
        ChannelStream channel = new ChannelStream();
        User user = new User();

        //aLog.d()

        //final JsonObject jsonOrderProduct = json.getAsJsonObject();

        String oProdId = jsonOrderProduct.get("id").getAsString();
        oProduct.setId(oProdId);

        String orderProductQty = jsonOrderProduct.get("quantity").getAsString();
        oProduct.setQuantity(Integer.parseInt(orderProductQty));

        //set product
        JsonObject productJson = jsonOrderProduct.getAsJsonObject("product");
        //Id
        String productId = productJson.get("id").getAsString(); //this is null
        product.setId(productId);
        //name
        String productName = productJson.get("name").getAsString();
        product.setName(productName);
        //Category
        String category = productJson.get("category").getAsString();
        switch(category){
            case("HEALTH"):
                product.setCategory(ProductCategories.HEALTH);
                break;
            case("FURNITURES"):
                product.setCategory(ProductCategories.FURNITURES);
                break;
            case("APPLIANCES"):
                product.setCategory(ProductCategories.APPLIANCES);
                break;
            case("BABY"):
                product.setCategory(ProductCategories.BABY);
                break;
            case("CLOTHING"):
                product.setCategory(ProductCategories.CLOTHING);
                break;
            case("FOOD"):
                product.setCategory(ProductCategories.FOOD);
                break;
            case("GROCERIES"):
                product.setCategory(ProductCategories.GROCERIES);
                break;
            case("SPORTS"):
                product.setCategory(ProductCategories.SPORTS);
                break;
            case("TECHNOLOGY"):
                product.setCategory(ProductCategories.TECHNOLOGY);
                break;
            default:
                product.setCategory(ProductCategories.OTHERS);
                break;
        }
        //description
        String productDesc = productJson.get("description").getAsString();
        product.setDescription(productDesc);
        //price
        String productPrice = productJson.get("price").getAsString();
        product.setPrice(Double.parseDouble(productPrice));
        //Qty
        String productQty = productJson.get("quantity").getAsString();
        product.setQuantity(Integer.parseInt(productQty));

        //set Channel
        JsonObject channelJson = productJson.getAsJsonObject("channel");
        //Id
        String channelId = channelJson.get("id").getAsString();
        channel.setId(channelId);
        //Name
        String channelName = channelJson.get("name").getAsString();
        channel.setName(channelName);

        //setUser
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
        user.setChannel(channel);
        product.setChannel(channel);
        oProduct.setProduct(product);

       return oProduct;
    }
}