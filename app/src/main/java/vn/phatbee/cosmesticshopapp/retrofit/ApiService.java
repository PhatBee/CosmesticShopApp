package vn.phatbee.cosmesticshopapp.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import vn.phatbee.cosmesticshopapp.model.Banner;

public interface ApiService {
    @GET("api/banners")
    Call<List<Banner>> getBanners();
}
