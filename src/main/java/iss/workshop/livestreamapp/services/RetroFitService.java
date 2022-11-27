package iss.workshop.livestreamapp.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import iss.workshop.livestreamapp.helpers.ChannelDeserializer;
import iss.workshop.livestreamapp.helpers.OrderDeserializer;
import iss.workshop.livestreamapp.helpers.OrderProductDeserializer;
import iss.workshop.livestreamapp.helpers.StreamDeserializer;
import iss.workshop.livestreamapp.helpers.UserDeserializer;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.OrderProduct;
import iss.workshop.livestreamapp.models.Orders;
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import lombok.Data;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Data
public class RetroFitService {

    //private final String API_URL = "http://10.0.2.2:8080"; 

    private final String API_URL =   "https://live-stream-team3.azurewebsites.net"; // "http://10.50.4.140:8080"; //

    private final String PREDICTION_API_URL = "http://10.0.2.2:5000";

    private Retrofit retrofit;

    public RetroFitService(String type){
        initializeRetrofit(type);
    }

    private static Converter.Factory createGsonConverter(Type type, Object typeAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(type, typeAdapter)
                .setDateFormat("yyyy-MM-dd'T'HH:mm");
        Gson gson = gsonBuilder.create();

        return GsonConverterFactory.create(gson);
    }

    private void initializeRetrofit(String type) {

        switch(type){
            case("stream"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(createGsonConverter(Stream.class, new StreamDeserializer()))
                        .build();
                break;
            case("login"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(createGsonConverter(User.class, new UserDeserializer()))
                        .build();
                break;
            case("channel"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(createGsonConverter(ChannelStream.class, new ChannelDeserializer()))
                        .build();
                break;
            case("new-orders"):
            case("orders"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(createGsonConverter(Orders.class, new OrderDeserializer()))
                        .build();
                break;
            case("order-product"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(createGsonConverter(OrderProduct.class, new OrderProductDeserializer()))
                        .build();
                break;
            case("product"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(createGsonConverter(Product.class, new OrderDeserializer()))
                        .build();
                break;
            case("verify-user"):
            case("get-rating"):
            case("rating"):
            case("save-logs"):
            case("save-product"):
            case("get-products"):
            case("order-status"):
            case("delete-stream"):
            case("delete-product"):
            case("get-channel-from-id"):
            case("save-user"):
            case("save-channel"):
            case("save-stream"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(GsonConverterFactory.create(new Gson()))
                        .build();
                break;
            case("prediction"):
                retrofit = new Retrofit.Builder()
                        .baseUrl(PREDICTION_API_URL)
                        .addConverterFactory(GsonConverterFactory.create(new Gson()))
                        .build();
                break;

        }

    }

}
