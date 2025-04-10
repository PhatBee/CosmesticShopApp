package vn.phatbee.cosmesticshopapp.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.phatbee.cosmesticshopapp.model.Banner;
import vn.phatbee.cosmesticshopapp.model.ForgotPasswordRequest;
import vn.phatbee.cosmesticshopapp.model.LoginRequest;
import vn.phatbee.cosmesticshopapp.model.LoginResponse;
import vn.phatbee.cosmesticshopapp.model.OtpVerificationRequest;
import vn.phatbee.cosmesticshopapp.model.PasswordResetResponse;
import vn.phatbee.cosmesticshopapp.model.RegistrationRequest;
import vn.phatbee.cosmesticshopapp.model.RegistrationResponse;
import vn.phatbee.cosmesticshopapp.model.ResetPasswordRequest;

public interface ApiService {
    @GET("api/banners")
    Call<List<Banner>> getBanners();

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<RegistrationResponse> register(@Body RegistrationRequest request);

    @POST("api/auth/verify-otp")
    Call<RegistrationResponse> verifyOtp(@Body OtpVerificationRequest request);

    @POST("api/auth/resend-otp")
    Call<RegistrationResponse> resendOtp(@Query("email") String email);

    @POST("api/auth/forgot-password")
    Call<PasswordResetResponse> requestPasswordReset(@Body ForgotPasswordRequest request);

    @POST("api/auth/reset-password")
    Call<PasswordResetResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("api/auth/resend-password-reset-otp")
    Call<PasswordResetResponse> resendPasswordResetOtp(@Query("email") String email);
}
