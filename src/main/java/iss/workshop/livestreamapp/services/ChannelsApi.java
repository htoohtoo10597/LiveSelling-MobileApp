package iss.workshop.livestreamapp.services;

import java.util.List;

import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Rating;
import iss.workshop.livestreamapp.models.Stream;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChannelsApi {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/api/user/channels/{channelId}")
    Call<ChannelStream> selectChannel(@Path("channelId") String channelId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/api/user/channels")
    Call<List<ChannelStream>> getAllChannels();

    @GET("/api/user/channels/finduser/{userId}")
    Call<ChannelStream> findChannelByUserId(@Path("userId") String userId);



    @GET("/api/user/getverifiedchannels/{channelId}")
    Call<List<ChannelStream>> getAllVerifiedChannels(@Path("channelId") String channelId);

    @GET("/api/rating/channelrating/{channelId}")
    Call<Double> getChannelAvgRating(@Path("channelId") String channelId);

    @POST("/api/rating/sendrating/{channelId}/{userId}")
    Call<Rating> saveRating(@Body Rating rating, @Path("channelId") String channelId, @Path("userId") String userId);

}
