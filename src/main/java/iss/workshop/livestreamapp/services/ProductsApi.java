package iss.workshop.livestreamapp.services;

import java.util.List;

import iss.workshop.livestreamapp.models.OrderProduct;
import iss.workshop.livestreamapp.models.Product;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductsApi {

    @POST("/api/product/addtostore/{userId}")
    Call<Product> addToStore(@Path("userId") String userId, @Body Product product);

    @GET("/api/product/products/getchannelproducts/{channelId}")
    Call<List<Product>> getAllProductsInStore(@Path("channelId") String channelId);

    @GET("/api/orders/products/{orderId}")
    Call<List<OrderProduct>> getProductsInOrder(@Path("orderId") String orderId);

    @PUT("api/product/editproduct/{prodId}")
    Call<Product> editProduct(@Path("prodId") String prodId, @Body Product product);

    @DELETE("api/product/products/{prodId}")
    Call<String> deleteProduct(@Path("prodId") String prodId);

}


