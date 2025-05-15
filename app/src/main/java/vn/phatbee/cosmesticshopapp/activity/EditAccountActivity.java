package vn.phatbee.cosmesticshopapp.activity;


import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.manager.UserSessionManager;
import vn.phatbee.cosmesticshopapp.model.User;
import vn.phatbee.cosmesticshopapp.model.UserUpdateDTO;
import vn.phatbee.cosmesticshopapp.model.UserUpdateResponse;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class EditAccountActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etPhone;
    private AutoCompleteTextView genderSpinner;
    private AppCompatButton btnUpdateProfile;
    private ImageView ivBack;
    private TextView tvFullName, tvEmail;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        genderSpinner = findViewById(R.id.gender_spinner);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        ivBack = findViewById(R.id.ivBackEditAccount);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);

        // Initialize session manager
        sessionManager = new UserSessionManager(this);

        // Fetch user data from backend
        fetchUserData();

        // Set up gender dropdown
        String[] genders = {"MALE", "FEMALE", "OTHER"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, genders);
        genderSpinner.setAdapter(genderAdapter);

        // Back button
        ivBack.setOnClickListener(v -> finish());

        // Update profile button
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void fetchUserData() {
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
                    tvFullName.setText(user.getFullName() != null ? user.getFullName() : "");
                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                    etFullName.setText(user.getFullName() != null ? user.getFullName() : "");
                    etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
                    genderSpinner.setText(user.getGender() != null ? user.getGender() : "", false);

                    // Update session
                    sessionManager.createLoginSession(user);
                } else {
                    Toast.makeText(EditAccountActivity.this, "Failed to fetch user data: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditAccountActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String gender = genderSpinner.getText().toString();

        // Validate inputs
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gender.isEmpty() || (!gender.equals("MALE") && !gender.equals("FEMALE") && !gender.equals("OTHER"))) {
            Toast.makeText(this, "Please select a valid gender", Toast.LENGTH_SHORT).show();
            return;
        }

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFullName(fullName);
        userUpdateDTO.setPhone(phone);
        userUpdateDTO.setGender(gender);

        Long userId = sessionManager.getUserDetails().getUserId();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<UserUpdateResponse> call = apiService.updateUser(userId, userUpdateDTO);
        call.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserUpdateResponse updateResponse = response.body();
                    if (updateResponse.isSuccess()) {
                        Toast.makeText(EditAccountActivity.this, updateResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Update session
                        User user = sessionManager.getUserDetails();
                        user.setFullName(fullName);
                        user.setPhone(phone);
                        user.setGender(gender);
                        sessionManager.createLoginSession(user);
                        finish();
                    } else {
                        Toast.makeText(EditAccountActivity.this, "Update failed: " + updateResponse.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("UpdateUser", "Update failed: " + updateResponse.getMessage());
                    }
                } else {
                    String errorMessage = response.message();
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("UpdateUser", "Failed to read error body: " + e.getMessage());
                    }
                    Toast.makeText(EditAccountActivity.this, "Failed to update profile: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UpdateUser", "Error: HTTP " + response.code() + ", Message: " + errorMessage + ", Body: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                Toast.makeText(EditAccountActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UpdateUser", "Failure: " + t.getMessage(), t);
            }
        });
    }
}