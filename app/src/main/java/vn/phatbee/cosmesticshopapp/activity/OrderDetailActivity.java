package vn.phatbee.cosmesticshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.adapter.OrderLineAdapter;
import vn.phatbee.cosmesticshopapp.model.Order;
import vn.phatbee.cosmesticshopapp.model.OrderLine;
import vn.phatbee.cosmesticshopapp.model.ShippingAddress;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderDate, tvStatus, tvReceiverName, tvReceiverPhone, tvAddress, tvPaymentMethod, tvTotal, tvPaymentTime;
    private ImageView ivBack;
    private RecyclerView rvOrderItems;
    private Button btnCancelOrder, btnReviewProduct;
    private Order order;
    private OrderLineAdapter orderLineAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Khởi tạo các thành phần giao diện
//        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderTime);
//        tvStatus = findViewById(R.id.tvStatus);
//        tvReceiverName = findViewById(R.id.tvReceiverName);
//        tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotal = findViewById(R.id.tvTotalOrder);
        ivBack = findViewById(R.id.ivBack);
        rvOrderItems = findViewById(R.id.rvProductOrderItem);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnReviewProduct = findViewById(R.id.btnViewRate);
        tvPaymentTime = findViewById(R.id.tvPaymentTime);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Nhận dữ liệu từ Intent
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập RecyclerView cho danh sách sản phẩm
        List<OrderLine> orderLines = new ArrayList<>(order.getOrderLines());
        orderLineAdapter = new OrderLineAdapter(this, orderLines);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(orderLineAdapter);

        // Hiển thị thông tin đơn hàng
//        tvOrderId.setText("Mã đơn hàng: #" + order.getOrderId());
        if (order.getOrderDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            tvOrderDate.setText(order.getOrderDate().format(formatter));
        } else {
            tvOrderDate.setText("N/A");
        }

        if (order.getPayment().getPaymentDate() != null) {
            String paymentDateStr = order.getPayment().getPaymentDate(); // nếu là String
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(paymentDateStr, parser);

// Sau đó format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedDate = dateTime.format(formatter);
            tvPaymentTime.setText(formattedDate);

        }

        else {
            tvPaymentTime.setText("N/A");
        }

//        tvStatus.setText("Trạng thái: " + getStatusText(order.getOrderStatus()));
        tvTotal.setText("Tổng: " + String.format("%,.0f VND", order.getTotal()));

        // Hiển thị thông tin thanh toán
        if (order.getPayment() != null) {
            tvPaymentMethod.setText("Phương thức: " + order.getPayment().getPaymentMethod());
        } else {
            tvPaymentMethod.setText("Phương thức: N/A");
        }

        // Hiển thị địa chỉ giao hàng
        ShippingAddress shippingAddress = order.getShippingAddress();
        if (shippingAddress != null) {
//            tvReceiverName.setText("Người nhận: " + shippingAddress.getReceiverName());
//            tvReceiverPhone.setText("Số điện thoại: " + shippingAddress.getReceiverPhone());
            tvAddress.setText("Địa chỉ: " + shippingAddress.getAddress() + ", " +
                    shippingAddress.getWard() + ", " +
                    shippingAddress.getDistrict() + ", " +
                    shippingAddress.getProvince());
        } else {
//            tvReceiverName.setText("Người nhận: N/A");
//            tvReceiverPhone.setText("Số điện thoại: N/A");
            tvAddress.setText("Địa chỉ: N/A");
        }

        // Hiển thị nút hành động dựa trên trạng thái
        if ("PENDING".equals(order.getOrderStatus())) {
            btnCancelOrder.setVisibility(View.VISIBLE);
            btnCancelOrder.setOnClickListener(v -> cancelOrder());
        } else {
            btnCancelOrder.setVisibility(View.GONE);
        }

        if ("DELIVERED".equals(order.getOrderStatus())) {
            btnReviewProduct.setVisibility(View.VISIBLE);
            btnReviewProduct.setOnClickListener(v -> reviewProduct());
        } else {
            btnReviewProduct.setVisibility(View.GONE);
        }

        // Xử lý nút quay lại
        ivBack.setOnClickListener(v -> finish());
    }

    private String getStatusText(String status) {
        switch (status) {
            case "PENDING": return "Chờ xác nhận";
            case "PROCESSING": return "Đang xử lý";
            case "SHIPPING": return "Đang giao hàng";
            case "DELIVERED": return "Đã giao hàng";
            case "CANCELLED": return "Đã hủy";
            default: return "Không xác định";
        }
    }

    private void cancelOrder() {
        Call<Void> call = apiService.cancelOrder(order.getOrderId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Đơn hàng đã được hủy", Toast.LENGTH_SHORT).show();
                    order.setOrderStatus("CANCELLED");
                    tvStatus.setText("Trạng thái: Đã hủy");
                    btnCancelOrder.setVisibility(View.GONE);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reviewProduct() {
        // Giả sử bạn có một ReviewActivity để đánh giá sản phẩm
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("orderLines", new ArrayList<>(order.getOrderLines()));
        startActivity(intent);
    }
}