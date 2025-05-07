package vn.phatbee.cosmesticshopapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.Address;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class AddAddressActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etReceiverName, etReceiverPhone, etAddress, etProvince, etDistrict, etWard;
    private Switch swDefault;
    private Button btnComplete;
    private UserSessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        anhXa();

        sessionManager = new UserSessionManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        ivBack.setOnClickListener(v -> finish());

        btnComplete.setOnClickListener(v -> saveAddress());
    }

    private void anhXa(){
        ivBack = findViewById(R.id.ivBack);
        etReceiverName = findViewById(R.id.etReceiverName);
        etReceiverPhone = findViewById(R.id.etReceiverPhone);
        etAddress = findViewById(R.id.etAddress);
        etProvince = findViewById(R.id.etProvince);
        etDistrict = findViewById(R.id.etDistrict);
        etWard = findViewById(R.id.etWard);
        swDefault = findViewById(R.id.swDefault);
        btnComplete = findViewById(R.id.btnComplete);
    }

    private void saveAddress() {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Please log in to add an address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etReceiverName.getText().toString().trim().isEmpty() ||
                etReceiverPhone.getText().toString().trim().isEmpty() ||
                etAddress.getText().toString().trim().isEmpty() ||
                etProvince.getText().toString().trim().isEmpty() ||
                etDistrict.getText().toString().trim().isEmpty() ||
                etWard.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Address address = new Address();
        address.setReceiverName(etReceiverName.getText().toString().trim());
        address.setReceiverPhone(etReceiverPhone.getText().toString().trim());
        address.setAddress(etAddress.getText().toString().trim());
        address.setProvince(etProvince.getText().toString().trim());
        address.setDistrict(etDistrict.getText().toString().trim());
        address.setWard(etWard.getText().toString().trim());

        Call<Address> call = apiService.addAddress(userId, address);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddAddressActivity.this, "Address added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddAddressActivity.this, "Failed to add address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}