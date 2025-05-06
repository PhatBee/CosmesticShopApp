package vn.phatbee.cosmesticshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.phatbee.cosmesticshopapp.R;

public class ProfileActivity extends AppCompatActivity {
    TextView tvMyAccount;
    ImageView ivMyAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

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

    }

}