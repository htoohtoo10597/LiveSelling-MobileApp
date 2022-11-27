package iss.workshop.livestreamapp.services;

import java.util.List;

import iss.workshop.livestreamapp.models.StreamLog;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StreamLogApi {

    @POST("/api/likes/newstreamlog/{sellerId}/{streamId}")
    Call<StreamLog> addNewLogList(@Body StreamLog streamLog, @Path("sellerId") String sellerId, @Path("streamId") String streamId);

}
