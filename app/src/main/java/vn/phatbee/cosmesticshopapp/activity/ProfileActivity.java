package vn.phatbee.cosmesticshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvMyAccount;
    private ImageView ivMyAccount;
    private ImageView btnLogout;
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

    }

    private void anhXa() {
        btnLogout = findViewById(R.id.ivLogout);
    }

}