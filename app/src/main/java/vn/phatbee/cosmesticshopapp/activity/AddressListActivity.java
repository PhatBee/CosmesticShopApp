package vn.phatbee.cosmesticshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.Address;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class AddressListActivity extends AppCompatActivity {

    private ListView lvAddresses;
    private ImageView ivBack;
    private UserSessionManager sessionManager;
    private ApiService apiService;
    private Button btnAddNewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        lvAddresses = findViewById(R.id.lvAddresses);
        ivBack = findViewById(R.id.ivBack);

        btnAddNewAddress = findViewById(R.id.btnAddAddress);
        btnAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });

        sessionManager = new UserSessionManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Back button click listener
        ivBack.setOnClickListener(v -> finish());

        loadAddresses();
    }

    private void loadAddresses() {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Please log in to view addresses", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<Address>> call = apiService.getAddressesByUserId(userId);
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> addresses = response.body();
                    displayAddresses(addresses);
                } else {
                    Toast.makeText(AddressListActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAddresses(List<Address> addresses) {
        String[] addressStrings = new String[addresses.size()];
        for (int i = 0; i < addresses.size(); i++) {
            Address address = addresses.get(i);
            addressStrings[i] = String.format("%s | %s\n%s, %s, %s, %s",
                    address.getReceiverName(),
                    address.getReceiverPhone(),
                    address.getAddress(),
                    address.getWard() != null ? address.getWard() : "",
                    address.getDistrict() != null ? address.getDistrict() : "",
                    address.getProvince() != null ? address.getProvince() : "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addressStrings);
        lvAddresses.setAdapter(adapter);
    }

    // Placeholder for Add New Address button
    public void startAddAddress(View view) {
        Toast.makeText(this, "Add New Address functionality to be implemented", Toast.LENGTH_SHORT).show();
    }
}