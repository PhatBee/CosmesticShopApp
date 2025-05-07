package vn.phatbee.cosmesticshopapp.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.phatbee.cosmesticshopapp.model.Address;
import vn.phatbee.cosmesticshopapp.model.Banner;
import vn.phatbee.cosmesticshopapp.model.Cart;
import vn.phatbee.cosmesticshopapp.model.CartItemRequest;
import vn.phatbee.cosmesticshopapp.model.Category;
import vn.phatbee.cosmesticshopapp.model.ForgotPasswordRequest;
import vn.phatbee.cosmesticshopapp.model.LoginRequest;
import vn.phatbee.cosmesticshopapp.model.LoginResponse;
import vn.phatbee.cosmesticshopapp.model.OtpVerificationRequest;
import vn.phatbee.cosmesticshopapp.model.PasswordResetResponse;
import vn.phatbee.cosmesticshopapp.model.Product;
import vn.phatbee.cosmesticshopapp.model.RegistrationRequest;
import vn.phatbee.cosmesticshopapp.model.RegistrationResponse;
import vn.phatbee.cosmesticshopapp.model.ResetPasswordRequest;
import vn.phatbee.cosmesticshopapp.model.User;
import vn.phatbee.cosmesticshopapp.model.UserUpdateDTO;
import vn.phatbee.cosmesticshopapp.model.UserUpdateResponse;

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

    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("api/products/{productId}")
    Call<Product> getProductDetails(@Path("productId") Long productId);

    @GET("api/cart/user/{userId}")
    Call<Cart> getCart(@Path("userId") Long userId);

    @POST("api/cart/add")
    Call<Cart> addToCart(@Body CartItemRequest request);

    @PUT("api/cart/update")
    Call<Cart> updateCartItem(@Body CartItemRequest request);

    @DELETE("api/cart/remove/{userId}/{cartItemId}")
    Call<Cart> removeFromCart(@Path("userId") Long userId, @Path("cartItemId") Long cartItemId);

    @DELETE("api/cart/clear/{userId}")
    Call<Void> clearCart(@Path("userId") Long userId);

    @GET("/api/user/{userId}")
    Call<User> getUser(@Path("userId") Long userId);

    @PUT("/api/user/{userId}")
    Call<UserUpdateResponse> updateUser(@Path("userId") Long userId, @Body UserUpdateDTO userUpdateDTO);

    @GET("api/addresses/user/{userId}")
    Call<List<Address>> getAddressesByUserId(@Path("userId") Long userId);

    @POST("api/addresses/add")
    Call<Address> addAddress(@Query("userId") Long userId, @Body Address address);
}
