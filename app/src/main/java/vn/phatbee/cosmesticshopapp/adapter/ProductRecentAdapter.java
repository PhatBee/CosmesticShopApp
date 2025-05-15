package vn.phatbee.cosmesticshopapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductRecentAdapter extends RecyclerView.Adapter<ProductRecentAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> products;
    private OnProductRecentClickListener listener;

    public interface OnProductRecentClickListener {
        void onProductRecentClick(Product product);
    }

    public ProductRecentAdapter(Context context, OnProductRecentClickListener listener) {
        this.context = context;
        this.products = new ArrayList<>();
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_recommend, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductRecentClick(products.get(getAdapterPosition()));
                }
            });
        }

        void bind(Product product) {
            // Handle product name
            tvProductName.setText(product.getProductName() != null ? product.getProductName() : "Unknown Product");

            // Handle product price
            Double price = product.getPrice();
            if (price != null) {
                tvProductPrice.setText(String.format("$%.2f", price));
            } else {
                tvProductPrice.setText("Price Unavailable");
            }

            // Handle product image
            Glide.with(context)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage);
        }
    }
}