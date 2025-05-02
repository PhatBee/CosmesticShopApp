package vn.phatbee.cosmesticshopapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.model.Product;
import vn.phatbee.cosmesticshopapp.retrofit.ApiService;
import vn.phatbee.cosmesticshopapp.retrofit.RetrofitClient;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageButton btnback;
    private ImageView btnWish, btnCart;
    private ImageView iProduct;
    private TextView tvProductName, tvCategory, tvPrice, tvBrand, tvVolume;
    private TextView tvOrigin, tvManufactureDate, tvExpirationDate;
    private TextView tvDescription, tvHowToUse, tvIngredients;
    private Toolbar toolbar;
    private Product currentProduct;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        anhXa();

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            finish();
            return;
        }

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getProductDetails();

    }
    void anhXa(){
        btnback = findViewById(R.id.ibtnBack);
        iProduct = findViewById(R.id.iProduct);
        btnWish = findViewById(R.id.ibtnWish);
        btnCart = findViewById(R.id.ibtnCart);
        tvProductName = findViewById(R.id.tvProductName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvBrand = findViewById(R.id.tvBrand);
        tvVolume = findViewById(R.id.tvVolume);
        tvOrigin = findViewById(R.id.tvOrigin);
        tvManufactureDate = findViewById(R.id.tvManufactureDate);
        tvExpirationDate = findViewById(R.id.tvExpirationDate);
        tvDescription = findViewById(R.id.tvDescription);
        tvHowToUse = findViewById(R.id.tvHowToUse);
        tvIngredients = findViewById(R.id.tvIngredients);
        toolbar = findViewById(R.id.toolbar);
    }

    private void getProductDetails() {
        // Make API call to get product details
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Product> productCall = apiService.getProductDetails(productId);
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful() && response.body() != null){
                    currentProduct = response.body();
                    // Update UI with product details
                    displayProductDetails(currentProduct);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable throwable) {
                Toast.makeText(ProductDetailsActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductDetails(Product product) {
        // Update UI with product details
        tvProductName.setText(product.getProductName());
        tvCategory.setText(product.getCategory().getCategoryName());
        tvPrice.setText(product.getPrice() + " VND");
        tvBrand.setText(product.getBrand());
        tvVolume.setText(product.getVolume());
        tvOrigin.setText(product.getOrigin());
        tvManufactureDate.setText(product.getManufactureDate());
        tvExpirationDate.setText(product.getExpirationDate());
        tvDescription.setText(product.getDescription());
        tvHowToUse.setText(product.getHow_to_use());
        tvIngredients.setText(product.getIngredient());
        if(product.getImage() != null){
            Glide.with(this).load(product.getImage()).into(iProduct);
        };
    }
}