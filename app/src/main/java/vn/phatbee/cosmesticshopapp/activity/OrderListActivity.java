package vn.phatbee.cosmesticshopapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.adapter.OrderPagerAdapter;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.Order;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class OrderListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrderPagerAdapter pagerAdapter;
    private UserSessionManager sessionManager;
    private ApiService apiService;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        ivBack = findViewById(R.id.ivBack);

        sessionManager = new UserSessionManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Setup ViewPager and Tabs
        pagerAdapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Đang hoạt động");
                    break;
                case 1:
                    tab.setText("Hoàn thành");
                    break;
                case 2:
                    tab.setText("Đã hủy");
                    break;
            }
        }).attach();

        ivBack.setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Call<Map<String, List<Order>>> call = apiService.getOrdersByUserId(userId);
        call.enqueue(new Callback<Map<String, List<Order>>>() {
            @Override
            public void onResponse(Call<Map<String, List<Order>>> call, Response<Map<String, List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, List<Order>> categorizedOrders = response.body();
                    List<Order> activeOrders = categorizedOrders.get("active") != null ? categorizedOrders.get("active") : new ArrayList<>();
                    List<Order> completedOrders = categorizedOrders.get("completed") != null ? categorizedOrders.get("completed") : new ArrayList<>();
                    List<Order> cancelledOrders = categorizedOrders.get("cancelled") != null ? categorizedOrders.get("cancelled") : new ArrayList<>();

                    Log.d("OrderData", "Active: " + categorizedOrders.get("active").size());
                    Log.d("OrderData", "Completed: " + categorizedOrders.get("completed").size());
                    Log.d("OrderData", "Cancelled: " + categorizedOrders.get("cancelled").size());

                    pagerAdapter.updateFragments(activeOrders, completedOrders, cancelledOrders);
                } else {
                    Log.d("OrderShow", response.message() + response.code());
                    Toast.makeText(OrderListActivity.this, "Không thể tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, List<Order>>> call, Throwable t) {
                Log.d("OrderList", "Lỗi: " + t.getMessage());
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}