package iss.workshop.livestreamapp.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.User;

public class ProductDeserializer implements JsonDeserializer<Product> {
    @Override
    public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Product product = new Product();
        ChannelStream channel = new ChannelStream();
        User user = new User();
        final JsonObject productJson = json.getAsJsonObject();

        String productId = productJson.get("id").getAsString();
        product.setId(productId);

        String productName = productJson.get("name").getAsString();
        product.setName(productName);

        String productPrice = productJson.get("price").getAsString();
        product.setPrice(Double.parseDouble(productPrice));

        String productQty = productJson.get("quantity").getAsString();
        product.setQuantity(Integer.parseInt(productQty));

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
        return null;
    }
}
