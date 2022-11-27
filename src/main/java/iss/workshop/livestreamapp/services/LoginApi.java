package iss.workshop.livestreamapp.services;

import java.util.List;

import iss.workshop.livestreamapp.models.LoginBag;
import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApi {

    @POST("/api/login")
    Call<User> checkUserIfExist(@Body LoginBag loginBag);
}
