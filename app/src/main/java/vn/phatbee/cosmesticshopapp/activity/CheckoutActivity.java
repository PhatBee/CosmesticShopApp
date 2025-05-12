package vn.phatbee.cosmesticshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.adapter.CartItemCheckoutAdapter;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.Address;
import vn.phatbee.cosmesticshopapp.model.CartItem;
import vn.phatbee.cosmesticshopapp.model.User;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView ivBack, ivEditAddressCheckout, ivEditContact;
    private TextView tvAddress, tvPhone, tvEmail, tvTotal;
    private RecyclerView rvCartItems;
    private Button btnProceed, btnEditPayment;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCOD, rbVNPay;
    private UserSessionManager sessionManager;
    private ApiService apiService;
    private CartItemCheckoutAdapter cartItemAdapter;
    private ActivityResultLauncher<Intent> addressListLauncher;
    private Address defaultAddress;
    private List<CartItem> selectedCartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        ivEditAddressCheckout = findViewById(R.id.ivEditAddressCheckout);
        ivEditContact = findViewById(R.id.ivEdit2);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvTotal = findViewById(R.id.tvTotal);
        rvCartItems = findViewById(R.id.rvCartItems);
        btnProceed = findViewById(R.id.btnProceed);
        btnEditPayment = findViewById(R.id.btnEditPayment);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCOD = findViewById(R.id.rbCOD);
        rbVNPay = findViewById(R.id.rbVNPay);

        sessionManager = new UserSessionManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Get selected cart items from Intent
        Object extra = getIntent().getSerializableExtra("selectedCartItems");
        if (extra instanceof List<?>) {
            selectedCartItems = new ArrayList<>();
            for (Object item : (List<?>) extra) {
                if (item instanceof CartItem) {
                    selectedCartItems.add((CartItem) item);
                }
            }
        } else {
            selectedCartItems = new ArrayList<>();
        }
        if (selectedCartItems == null || selectedCartItems.isEmpty()) {
            Toast.makeText(this, "No items selected for checkout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerView with selected items
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartItemAdapter = new CartItemCheckoutAdapter(this, selectedCartItems);
        rvCartItems.setAdapter(cartItemAdapter);
        updateTotalPrice();

        // Register ActivityResultLauncher for address selection
        addressListLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadDefaultAddress();
                    }
                });

        // Setup click listeners
        ivBack.setOnClickListener(v -> finish());
        ivEditAddressCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, AddressListActivity.class);
            addressListLauncher.launch(intent);
        });
        ivEditContact.setOnClickListener(v -> {
            // Navigate to EditContactActivity (to be implemented)
            Toast.makeText(this, "Edit contact info not implemented", Toast.LENGTH_SHORT).show();
        });
        btnEditPayment.setOnClickListener(v -> rgPaymentMethod.setVisibility(View.VISIBLE));
        btnProceed.setOnClickListener(v -> proceedToPay());

        // Load data
        loadDefaultAddress();
        loadContactInfo();
    }

    private void loadDefaultAddress() {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Please log in to proceed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Call<Address> call = apiService.getDefaultAddress(userId);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    defaultAddress = response.body();
                    String addressText = String.format("%s, %s, %s, %s",
                            defaultAddress.getAddress(),
                            defaultAddress.getWard(),
                            defaultAddress.getDistrict(),
                            defaultAddress.getProvince());
                    tvAddress.setText(addressText);
                } else {
                    tvAddress.setText("No default address set");
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Error loading address: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadContactInfo() {
        Long userId = sessionManager.getUserDetails().getUserId();
        if (userId == null || userId == 0) return;

        Call<User> call = apiService.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvPhone.setText(user.getPhone());
                    tvEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(CheckoutActivity.this, "Failed to load contact info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Error loading contact info: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : selectedCartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        tvTotal.setText(String.format("VND %.2f", total));
    }

    private void proceedToPay() {
        if (defaultAddress == null) {
            Toast.makeText(this, "Please set a default address", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = selectedId == rbCOD.getId() ? "COD" : "VNPay";
        if (paymentMethod.equals("COD")) {
            Toast.makeText(this, "Proceeding with COD payment", Toast.LENGTH_SHORT).show();
            placeOrder(paymentMethod);
        } else {
            Toast.makeText(this, "Proceeding with VNPay payment", Toast.LENGTH_SHORT).show();
            // Implement VNPay integration here
        }
    }

    private void placeOrder(String paymentMethod) {
        Toast.makeText(this, "Order placed successfully with " + paymentMethod, Toast.LENGTH_SHORT).show();
        apiService.clearCart(sessionManager.getUserDetails().getUserId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Error clearing cart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}