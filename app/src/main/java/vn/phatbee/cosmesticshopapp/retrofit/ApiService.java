package vn.phatbee.cosmesticshopapp.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import vn.phatbee.cosmesticshopapp.model.Banner;
import vn.phatbee.cosmesticshopapp.model.LoginRequest;
import vn.phatbee.cosmesticshopapp.model.LoginResponse;

public interface ApiService {
    @GET("api/banners")
    Call<List<Banner>> getBanners();

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
