package vn.phatbee.cosmesticshopapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.adapter.BannerAdapter;
import vn.phatbee.cosmesticshopapp.model.Banner;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class MainActivity extends AppCompatActivity {
    private TextView tvUsername;
    private SharedPreferences sharedPreferences;
    private ViewPager2 viewPagerSlider;
    private DotsIndicator dotsIndicator;
    private ProgressBar progressBarBanner;
    private List<Banner> banners = new ArrayList<>();
    private BannerAdapter bannerAdapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        dotsIndicator = findViewById(R.id.dotsIndicator);
        viewPagerSlider = findViewById(R.id.viewPager2);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        progressBarBanner = findViewById(R.id.progressBar2);
        tvUsername = findViewById(R.id.tvUsername);

        // Set up adapter
        bannerAdapter = new BannerAdapter(this, banners);
        viewPagerSlider.setAdapter(bannerAdapter);

        // Connect dots indicator with ViewPager2
        dotsIndicator.setViewPager2(viewPagerSlider);

        // Load banners data
        loadBanners();

        // Auto-scroll feature
        setupAutoScroll();

        // Update username from SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        tvUsername.setText(username);

    }

    private void loadBanners() {
        progressBarBanner.setVisibility(View.VISIBLE);

        // Use Retrofit to fetch banner data from your Spring Boot backend
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getBanners().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                progressBarBanner.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    banners.clear();
                    banners.addAll(response.body());
                    bannerAdapter.notifyDataSetChanged();

                    // Show the indicator if we have more than one banner
                    dotsIndicator.setVisibility(banners.size() > 1 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                progressBarBanner.setVisibility(View.GONE);
                // Handle error
                Toast.makeText(MainActivity.this, "Failed to load banners", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAutoScroll() {
        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPagerSlider.getCurrentItem();
                int totalItems = bannerAdapter.getItemCount();

                if (totalItems > 1) {
                    int nextItem = (currentItem + 1) % totalItems;
                    viewPagerSlider.setCurrentItem(nextItem, true);
                    autoScrollHandler.postDelayed(this, 3000); // Change banner every 3 seconds
                }
            }
        };

        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);

        // Remember to reset timer when user manually swipes
        viewPagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                autoScrollHandler.removeCallbacks(autoScrollRunnable);
                autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop auto-scrolling when activity is paused
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume auto-scrolling when activity is resumed
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (viewPagerSlider != null) {
            viewPagerSlider.unregisterOnPageChangeCallback(null);
        }

        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
}