package iss.workshop.livestreamapp.services;

import java.util.List;

import iss.workshop.livestreamapp.models.Stream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StreamsApi {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/api/user/streams")
    Call<List<Stream>> getAllStreams();

    @GET("/api/user/userstreams/{userId}")
    Call<List<Stream>> getAllUserStreams(@Path("userId") String userId);


    @GET("/api/user/notuserstreams/{userId}")
    Call<List<Stream>> getAllStreamsNotByUser(@Path("userId") String userId);

    @GET("/api/user/notcompletedstreams")
    Call<List<Stream>> getAllNotCompletedStreams();

    @PUT("api/user/setstreamtoongoing/{streamId}")
    Call<Stream> setStreamToOngoing(@Path("streamId") String streamId);


    @POST("/api/user/addstream/{userId}")
    Call<Stream> addNewStream(@Body Stream newStream, @Path("userId") String userId);

    @DELETE("/api/user/deletestream/{streamId}")
    Call<ResponseBody> deleteStream(@Path("streamId") String streamId);

    @GET("/api/user/searchstreams/{searchterm}")
    Call<List<Stream>> getStreamsBySearchTerm(@Path("searchterm") String searchTerm);
}
