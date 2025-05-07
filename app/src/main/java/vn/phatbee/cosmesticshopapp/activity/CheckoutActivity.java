package vn.phatbee.cosmesticshopapp.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.adapter.CheckoutAdapter;
import vn.phatbee.cosmesticshopapp.model.CartItem;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private ImageView ivBack;
    private TextView tvTotal;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        rvCartItems = findViewById(R.id.rvCartItems);
        ivBack = findViewById(R.id.ivBack);
        tvTotal = findViewById(R.id.tvTotal);

        // Set up back button
        ivBack.setOnClickListener(v -> finish());

        // Get cart items from intent
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedCartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "No items in cart", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutAdapter(this, cartItems, (position, item) -> {
            cartItems.remove(position);
            checkoutAdapter.notifyItemRemoved(position);
            updateTotal();
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        rvCartItems.setAdapter(checkoutAdapter);

        // Update total price
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        tvTotal.setText(String.format("VND %.2f", total));
    }
}