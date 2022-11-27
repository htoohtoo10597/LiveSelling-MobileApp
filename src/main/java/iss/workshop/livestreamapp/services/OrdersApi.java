package iss.workshop.livestreamapp.services;

import java.util.List;

import iss.workshop.livestreamapp.models.Orders;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrdersApi {

    @GET("/api/orders/purchases/{userId}")
    Call<List<Orders>> getUserPurchases(@Path("userId") String userId);


    @GET("/api/orders/channelorders/{channelId}")
    Call<List<Orders>> getChannelOrders(@Path("channelId") String channelId);

    @POST("/api/orders/addorder/{userId}/{channelId}/{streamId}")
    Call<Orders> addNewOrder(@Body Orders order, @Path("userId") String userId,
                             @Path("channelId") String channelId, @Path("streamId") String streamId);

    @PUT("/api/orders/updateorderstatus/{orderId}/{status}")
    Call<ResponseBody> updateOrderStatus(@Path("orderId") String orderId, @Path("status") String status);

    @GET("/channelordersuserpending/{userId}")
    Call<List<Orders>> getChannelOrdersPendingByUserId(@Path("userId") String userId);

    @GET("/channelordersuserhistory/{userId}")
    Call<List<Orders>> getChannelOrdersHistory(@Path("userId") String userId);
}
