package vn.phatbee.cosmesticshopapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.adapter.WishlistAdapter;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.Cart;
import vn.phatbee.cosmesticshopapp.model.CartItemRequest;
import vn.phatbee.cosmesticshopapp.model.Wishlist;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;
import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private static final String TAG = "WishlistActivity";
    private RecyclerView recyclerView;
    private WishlistAdapter wishlistAdapter;
    private List<Wishlist> wishlistItems;
    private UserSessionManager sessionManager;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        sessionManager = new UserSessionManager(this);
        recyclerView = findViewById(R.id.rvWishlist);
        btnBack = findViewById(R.id.ivBackWish);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wishlistItems = new ArrayList<>();
        wishlistAdapter = new WishlistAdapter(wishlistItems, new WishlistAdapter.OnWishlistActionListener() {
            @Override
            public void onRemove(Wishlist wishlist) {
                removeFromWishlist(wishlist);
            }

            @Override
            public void onAddToCart(Wishlist wishlist) {
                addToCart(wishlist);
            }
        });
        recyclerView.setAdapter(wishlistAdapter);

        btnBack.setOnClickListener(v -> finish());

        loadWishlist();
    }

    private void loadWishlist() {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Please log in to view wishlist", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Wishlist>> call = apiService.getWishlistByUserId(userId);
        call.enqueue(new Callback<List<Wishlist>>() {
            @Override
            public void onResponse(Call<List<Wishlist>> call, Response<List<Wishlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wishlistItems.clear();
                    wishlistItems.addAll(response.body());
                    wishlistAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(WishlistActivity.this, "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Wishlist>> call, Throwable t) {
                Log.e(TAG, "Error loading wishlist: " + t.getMessage());
                Toast.makeText(WishlistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWishlist(Wishlist wishlist) {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.removeFromWishlist(userId, wishlist.getProduct().getProductId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    wishlistItems.remove(wishlist);
                    wishlistAdapter.notifyDataSetChanged();
                    Toast.makeText(WishlistActivity.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WishlistActivity.this, "Failed to remove from wishlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error removing from wishlist: " + t.getMessage());
                Toast.makeText(WishlistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(Wishlist wishlist) {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Please log in to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItemRequest cartItemRequest = new CartItemRequest(userId, wishlist.getProduct().getProductId(), 1L);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Cart> call = apiService.addToCart(cartItemRequest);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WishlistActivity.this, wishlist.getProduct().getProductName() + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WishlistActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e(TAG, "Error adding to cart: " + t.getMessage());
                Toast.makeText(WishlistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}