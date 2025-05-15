package vn.phatbee.cosmesticshopapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.ProductFeedback;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class ReviewActivity extends AppCompatActivity {

    private ImageView ivBack, ivProductImage;
    private TextView tvProductName;
    private EditText etComment;
    private RatingBar rbRating;
    private Button btnSubmit;
    private ApiService apiService;
    private UserSessionManager sessionManager;
    private Long productId;
    private int orderId;
    private Map<String, Object> productSnapshot;
    private ProductFeedback existingFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        etComment = findViewById(R.id.etComment);
        rbRating = findViewById(R.id.rbRating);
        btnSubmit = findViewById(R.id.btnSubmit);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new UserSessionManager(this);

        // Get data from Intent
        productId = getIntent().getLongExtra("productId", -1);
        orderId = getIntent().getIntExtra("orderId", -1);
        productSnapshot = (HashMap<String, Object>) getIntent().getSerializableExtra("productSnapshot");
        existingFeedback = (ProductFeedback) getIntent().getSerializableExtra("existingFeedback");

        if (productId == -1 || orderId == -1 || productSnapshot == null) {
            Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display product info
        tvProductName.setText(productSnapshot.get("productName") != null ? productSnapshot.get("productName").toString() : "N/A");
        String imageUrl = productSnapshot.get("image") != null ? productSnapshot.get("image").toString() : "";
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivProductImage);

        // Pre-populate fields if editing
        if (existingFeedback != null) {
            etComment.setText(existingFeedback.getComment());
            rbRating.setRating(existingFeedback.getRating() != null ? existingFeedback.getRating().floatValue() : 0);
            btnSubmit.setText("Cập nhật đánh giá");
        }

        // Handle back button
        ivBack.setOnClickListener(v -> finish());

        // Handle submit button
        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        String comment = etComment.getText().toString().trim();
        float rating = rbRating.getRating();
        Long customerId = sessionManager.getUserDetails().getUserId();

        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if (customerId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductFeedback feedback = new ProductFeedback();
        feedback.setComment(comment);
        feedback.setRating((double) rating);
        feedback.setCustomerId(customerId);
        feedback.setOrderId(orderId);
        feedback.setProductId(productId);
        feedback.setProductSnapshotName(productSnapshot.get("productName") != null ? productSnapshot.get("productName").toString() : "N/A");
        // Image upload not implemented in this example

        Call<ProductFeedback> call;
        if (existingFeedback != null) {
            feedback.setProductFeedbackId(existingFeedback.getProductFeedbackId());
            call = apiService.updateFeedback(feedback.getProductFeedbackId(), feedback);
        } else {
            call = apiService.createFeedback(feedback);
        }

        call.enqueue(new Callback<ProductFeedback>() {
            @Override
            public void onResponse(Call<ProductFeedback> call, Response<ProductFeedback> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ReviewActivity.this, existingFeedback != null ? "Đã cập nhật đánh giá" : "Đã gửi đánh giá", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d("Feedback", response.code() + " " + response.message());
                    Toast.makeText(ReviewActivity.this, "Không thể gửi đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductFeedback> call, Throwable t) {
                Toast.makeText(ReviewActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}