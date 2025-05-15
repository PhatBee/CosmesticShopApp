package vn.phatbee.cosmesticshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.User;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvMyAccount;
    private ImageView ivMyAccount, editAccount;
    private ImageView btnLogout;
    private ImageView ivAddress;
    private TextView tvAddress, tvName, tvUsername;
    private UserSessionManager sessionManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        anhXa();

        tvMyAccount = findViewById(R.id.tvMyAccount);
        ivMyAccount = findViewById(R.id.ivAccount);

        tvMyAccount.setOnClickListener(v ->{
            Intent intent = new Intent(ProfileActivity.this, EditAccountActivity.class);
            startActivity(intent);
        });

        ivMyAccount.setOnClickListener(v ->{
            Intent intent = new Intent(ProfileActivity.this, EditAccountActivity.class);
            startActivity(intent);
        });


        ivAddress = findViewById(R.id.ivAddress);
        ivAddress.setOnClickListener(v ->{
            Intent intent = new Intent(ProfileActivity.this, AddressListActivity.class);
            startActivity(intent);
        });

        tvAddress = findViewById(R.id.tvAddress);
        tvAddress.setOnClickListener(v ->{
            Intent intent = new Intent(ProfileActivity.this, AddressListActivity.class);
            startActivity(intent);
        });

        editAccount = findViewById(R.id.ivEdit);
        editAccount.setOnClickListener(v ->{
            Intent intent = new Intent(ProfileActivity.this, EditAccountActivity.class);
            startActivity(intent);
        });

        // Initialize UserSessionManager
        sessionManager = new UserSessionManager(this);

        btnLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sessionManager.logoutUser();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Update UI
                    tvName.setText(user.getFullName() != null ? user.getFullName() : "");
                    tvUsername.setText(user.getUsername() != null ? user.getUsername() : "");

                    // Update session
                    sessionManager.createLoginSession(user);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user data: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void anhXa() {
        btnLogout = findViewById(R.id.ivLogout);
        tvName = findViewById(R.id.tv_name);
        tvUsername = findViewById(R.id.tv_username);
    }

}